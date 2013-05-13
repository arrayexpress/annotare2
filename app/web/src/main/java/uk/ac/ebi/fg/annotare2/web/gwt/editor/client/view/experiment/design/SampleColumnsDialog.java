/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEventHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Integer.parseInt;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnsDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, SampleColumnsDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    ListBox columnTemplates;

    @UiField
    ListBox userColumns;

    @UiField
    Button moveUpButton;

    @UiField
    Button moveDownButton;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    @UiField
    Label otherLabel;

    @UiField
    SimpleLayoutPanel columnEditor;

    @UiField
    Label errorMessage;

    private List<SampleColumn> columns = new ArrayList<SampleColumn>();

    public SampleColumnsDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("Sample Attributes");

        setWidget(Binder.BINDER.createAndBindUi(this));
        center();

        updateColumnTemplateList();
    }

    @UiHandler("userColumns")
    void userColumnSelected(ChangeEvent event) {
        columnEditor.setWidget(
                createValueEditor(getSelectedColumn()));
    }

    @UiHandler("addButton")
    void addButtonClicked(ClickEvent event) {
        SampleColumn template = getSelectedColumnTemplate();
        if (template != null) {
            columnTemplates.removeItem(columnTemplates.getSelectedIndex());
            addColumn(template);
        }
    }

    @UiHandler("removeButton")
    void removeButtonClicked(ClickEvent event) {
        removeSelectedColumn();
    }

    @UiHandler("otherLabel")
    void newColumnClicked(ClickEvent event) {
        addColumn(new SampleColumn());
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        if (isValid()) {
            hide();
        }
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
        hide();
    }

    private SampleColumn getSelectedColumn() {
        int index = userColumns.getSelectedIndex();
        return index < 0 ? null : columns.get(index);
    }

    private SampleColumn getSelectedColumnTemplate() {
        int index = columnTemplates.getSelectedIndex();
        return index < 0 ? null :
                SampleColumn.DEFAULTS.get(
                        parseInt(columnTemplates.getValue(index)));
    }

    private Widget createValueEditor(SampleColumn column) {
        if (column == null) {
            return new Label("No selection");
        }
        SampleColumnEditor editor = new SampleColumnEditor(column);
        editor.addContentChangeEventHandler(new ContentChangeEventHandler() {
            @Override
            public void onContentChange(ContentChangeEvent event) {
                updateColumnTitles();
                validate();
            }
        });
        return editor;
    }

    private void addColumn(SampleColumn template) {
        if (columns.contains(template)) {
            return;
        }
        SampleColumn column = new SampleColumn(template);
        columns.add(column);
        userColumns.addItem(getColumnTitle(column), column.getName());
        userColumns.setItemSelected(columns.size() - 1, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), userColumns);
    }

    private void removeSelectedColumn() {
        int index = userColumns.getSelectedIndex();
        if (index < 0) {
            return;
        }
        userColumns.removeItem(index);
        columns.remove(index);
        updateColumnTemplateList();
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), userColumns);
    }

    private void updateColumnTitles() {
        int index = 0;
        for (SampleColumn column : columns) {
            userColumns.setItemText(index++, getColumnTitle(column));
        }
    }

    private String getColumnTitle(SampleColumn column) {
        return (column.getType().isFactorValue() ? "[FV] " : "") + column.getName();
    }

    private void updateColumnTemplateList() {
        columnTemplates.clear();
        Set<SampleColumn> used = new HashSet<SampleColumn>();
        for (SampleColumn column : columns) {
            used.add(column);
        }
        int index = 0;
        for (SampleColumn column : SampleColumn.DEFAULTS) {
            if (!used.contains(column)) {
                columnTemplates.addItem(column.getName(), Integer.toString(index));
            }
            index++;
        }
    }

    private void checkNamesUnique(List<String> errors) {
        Set<String> names = new HashSet<String>();
        for (SampleColumn column : columns) {
            names.add(column.getName());
        }
        if (names.size() != columns.size()) {
            errors.add("Attribute names must be unique");
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
    }

    private List<String> validate() {
        List<String> errors = new ArrayList<String>();
        checkNamesUnique(errors);
        showError(errors.isEmpty() ? "" : errors.get(0));
        return errors;
    }

    private boolean isValid() {
        return validate().isEmpty();
    }
}
