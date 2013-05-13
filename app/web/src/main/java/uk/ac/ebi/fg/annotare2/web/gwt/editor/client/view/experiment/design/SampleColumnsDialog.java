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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.*;

import static java.lang.Integer.parseInt;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnsDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, SampleColumnsDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    protected ListBox columnTemplates;

    @UiField
    protected ListBox userColumns;

    @UiField
    protected Button moveUpButton;

    @UiField
    protected Button moveDownButton;

    @UiField
    protected Button removeButton;

    @UiField
    protected Button addButton;

    @UiField
    protected Label otherLabel;

    @UiField
    protected SimpleLayoutPanel columnEditor;

    @UiField
    protected Label errorMessage;

    private Map<Integer, SampleColumn> columnMap = new HashMap<Integer, SampleColumn>();

    private Callback callback;

    private int nextId;

    public SampleColumnsDialog(List<SampleColumn> columns, Callback callback) {
        setModal(true);
        setGlassEnabled(true);
        setText("Sample Attributes");

        setWidget(Binder.BINDER.createAndBindUi(this));
        center();

        this.callback = callback;
        setColumns(columns);
        updateTemplateColumns();
    }

    @UiHandler("userColumns")
    protected void userColumnSelected(ChangeEvent event) {
        final int index = userColumns.getSelectedIndex();
        SampleColumn column = index < 0 ? null : getColumn(userColumns.getValue(index));

        if (column == null) {
            columnEditor.setWidget(new Label("No selection"));
            return;
        }

        SampleColumnEditor editor = new SampleColumnEditor(column);
        editor.addValueChangeHandler(new ValueChangeHandler<SampleColumn>() {
            @Override
            public void onValueChange(ValueChangeEvent<SampleColumn> event) {
                updateColumn(index, event.getValue());
            }
        });

        columnEditor.setWidget(editor);
    }

    @UiHandler("addButton")
    protected void addButtonClicked(ClickEvent event) {
        SampleColumn template = getSelectedColumnTemplate();
        if (template != null) {
            columnTemplates.removeItem(columnTemplates.getSelectedIndex());
            addColumn(template);
        }
    }

    @UiHandler("removeButton")
    protected void removeButtonClicked(ClickEvent event) {
        removeSelectedColumn();
    }

    @UiHandler("otherLabel")
    protected void newColumnClicked(ClickEvent event) {
        addColumn(new SampleColumn());
    }

    @UiHandler("okButton")
    protected void okButtonClicked(ClickEvent event) {
        if (isValid()) {
            hide();
            if (callback != null) {
                callback.onOkay(getColumns());
            }
        }
    }

    @UiHandler("cancelButton")
    protected void cancelButtonClicked(ClickEvent event) {
        hide();
        if (callback != null) {
            callback.onCancel();
        }
    }

    private SampleColumn getSelectedColumnTemplate() {
        int index = columnTemplates.getSelectedIndex();
        return index < 0 ? null :
                SampleColumn.DEFAULTS.get(
                        parseInt(columnTemplates.getValue(index)));
    }

    private void addColumn(SampleColumn template) {
        if (getColumnNames().contains(template.getName())) {
            return;
        }
        setColumn(new SampleColumn(template));
        userColumns.setItemSelected(userColumns.getItemCount() - 1, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), userColumns);
    }

    private void updateColumn(int index, SampleColumn value) {
        int columnId = parseInt(userColumns.getValue(index));
        columnMap.put(columnId, value);
        updateColumnTitles();
        validate();
    }

    private void removeSelectedColumn() {
        int index = userColumns.getSelectedIndex();
        if (index < 0) {
            return;
        }
        int columnId = parseInt(userColumns.getValue(index));
        userColumns.removeItem(index);
        columnMap.remove(columnId);
        updateTemplateColumns();
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), userColumns);
    }

    private void updateColumnTitles() {
        for (int i = 0; i < userColumns.getItemCount(); i++) {
            String value = userColumns.getValue(i);
            userColumns.setItemText(i, getColumnTitle(getColumn(value)));
        }
    }

    private String getColumnTitle(SampleColumn column) {
        return (column.getType().isFactorValue() ? "[FV] " : "") + column.getName();
    }

    private List<String> getColumnNames() {
        List<String> names = new ArrayList<String>();
        for (SampleColumn column : columnMap.values()) {
            names.add(column.getName());
        }
        return names;
    }

    private void setColumns(List<SampleColumn> columns) {
        userColumns.clear();
        for (SampleColumn column : columns) {
            setColumn(column);
        }
    }

    private List<SampleColumn> getColumns() {
        List<SampleColumn> columns = new ArrayList<SampleColumn>();
        for (int i = 0; i < userColumns.getItemCount(); i++) {
            columns.add(getColumn(userColumns.getValue(i)));
        }
        return columns;
    }

    private SampleColumn getColumn(String id) {
        return columnMap.get(parseInt(id));
    }

    private void setColumn(SampleColumn column) {
        int id = columnId();
        columnMap.put(id, column);
        userColumns.addItem(getColumnTitle(column), Integer.toString(id));
    }

    private void updateTemplateColumns() {
        columnTemplates.clear();
        Set<String> used = new HashSet<String>(getColumnNames());

        int index = 0;
        for (SampleColumn column : SampleColumn.DEFAULTS) {
            if (!used.contains(column.getName())) {
                columnTemplates.addItem(column.getName(), Integer.toString(index));
            }
            index++;
        }
    }

    private void checkNamesUnique(List<String> errors) {
        Set<String> names = new HashSet<String>(getColumnNames());
        if (names.size() != columnMap.size()) {
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

    private int columnId() {
        return ++nextId;
    }

    public interface Callback {

        void onCancel();

        void onOkay(List<SampleColumn> columns);
    }
}
