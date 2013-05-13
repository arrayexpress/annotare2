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
    SimpleLayoutPanel attributeEditor;

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
        attributeEditor.setWidget(
                createValueEditor(getSelectedColumn()));
    }

    @UiHandler("addButton")
    void addButtonClick(ClickEvent event) {
        SampleColumn template = getSelectedColumnTemplate();
        if (template != null) {
            columnTemplates.removeItem(columnTemplates.getSelectedIndex());
            addColumn(template);
        }
    }

    @UiHandler("removeButton")
    void removeButtonClick(ClickEvent event) {
        removeSelectedColumn();
    }

    @UiHandler("otherLabel")
    void newColumnClick(ClickEvent event) {
        addColumn(new SampleColumn());
    }

    @UiHandler("okButton")
    void okButtonClick(ClickEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    void cancelButtonClick(ClickEvent event) {
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
        return new SampleColumnEditor(column);
    }

    private void addColumn(SampleColumn template) {
        if (columns.contains(template)) {
            return;
        }
        SampleColumn column = new SampleColumn(template);
        columns.add(column);
        userColumns.addItem(column.getName());
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
}
