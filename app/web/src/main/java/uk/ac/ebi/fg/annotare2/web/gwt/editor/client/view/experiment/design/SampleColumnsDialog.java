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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;

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
    ListBox columnTemplateList;

    @UiField
    ListBox userColumnList;

    @UiField
    Button moveUpButton;

    @UiField
    Button moveDownButton;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    @UiField
    Label newColumnLabel;

    @UiField
    SimpleLayoutPanel columnEditor;

    @UiField
    Label errorMessage;

    private Map<Integer, SampleColumn> columnMap = new HashMap<Integer, SampleColumn>();

    private DialogCallback<List<SampleColumn>> callback;

    private int nextId;

    private final ColumnValueTypeEfoTerms efoSuggestService;

    private final List<SampleColumn> templateColumns = new ArrayList<SampleColumn>();

    public SampleColumnsDialog(List<SampleColumn> columns,
                               ColumnValueTypeEfoTerms efoSuggestService,
                               DialogCallback<List<SampleColumn>> callback) {
        setModal(true);
        setGlassEnabled(true);
        setText("Sample Attributes");

        setWidget(Binder.BINDER.createAndBindUi(this));
        center();

        this.efoSuggestService = efoSuggestService;
        this.callback = callback;
        setColumns(columns);

        efoSuggestService.getSystemEfoTerms(new AsyncCallback<SystemEfoTermMap>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO proper logging
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(SystemEfoTermMap result) {
                templateColumns.addAll(SampleColumn.getTemplateColumns(result));
                updateTemplateColumns();
            }
        });
    }

    @UiHandler("userColumnList")
    void userColumnSelected(ChangeEvent event) {
        final int index = userColumnList.getSelectedIndex();
        SampleColumn column = index < 0 ? null : getColumn(userColumnList.getValue(index));

        if (column == null) {
            columnEditor.setWidget(new Label("No selection"));
            return;
        }

        SampleColumnEditor editor = new SampleColumnEditor(column, efoSuggestService);
        editor.addValueChangeHandler(new ValueChangeHandler<SampleColumn>() {
            @Override
            public void onValueChange(ValueChangeEvent<SampleColumn> event) {
                updateColumn(index, event.getValue());
            }
        });

        columnEditor.setWidget(editor);
    }

    @UiHandler("addButton")
    void addButtonClicked(ClickEvent event) {
        SampleColumn template = getSelectedColumnTemplate();
        if (template != null) {
            columnTemplateList.removeItem(columnTemplateList.getSelectedIndex());
            addColumn(template);
        }
    }

    @UiHandler("removeButton")
    void removeButtonClicked(ClickEvent event) {
        removeSelectedColumn();
    }

    @UiHandler("newColumnLabel")
    void newColumnClicked(ClickEvent event) {
        addColumn(new SampleColumn(0, "", null));
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        if (isValid()) {
            hide();
            if (callback != null) {
                callback.onOkay(getColumns());
            }
        }
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
        hide();
        if (callback != null) {
            callback.onCancel();
        }
    }

    @UiHandler("moveUpButton")
    void moveColumnUp(ClickEvent event) {
        int index = userColumnList.getSelectedIndex();
        if (index <= 0) {
            return;
        }
        move(index, index - 1);
    }

    @UiHandler("moveDownButton")
    void moveColumnDown(ClickEvent event) {
        int index = userColumnList.getSelectedIndex();
        if (index < 0 || index >= userColumnList.getItemCount() - 1) {
            return;
        }
        move(index, index + 1);
    }

    private void move(int from, int to) {
        String text = userColumnList.getItemText(from);
        String value = userColumnList.getValue(from);
        userColumnList.removeItem(from);
        userColumnList.insertItem(text, value, to);

        setItemSelected(userColumnList, to);
    }

    private SampleColumn getSelectedColumnTemplate() {
        int index = columnTemplateList.getSelectedIndex();
        return index < 0 ? null :
                templateColumns.get(
                        parseInt(columnTemplateList.getValue(index)));
    }

    private void addColumn(SampleColumn template) {
        if (!getColumnNames().contains(template.getName())) {
            setColumn(new SampleColumn(0, template), true);
        }
    }

    private void updateColumn(int index, SampleColumn value) {
        int columnId = parseInt(userColumnList.getValue(index));
        columnMap.put(columnId, value);
        updateColumnTitles();
        validate();
    }

    private void removeSelectedColumn() {
        int index = userColumnList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        int columnId = parseInt(userColumnList.getValue(index));
        userColumnList.removeItem(index);
        columnMap.remove(columnId);
        updateTemplateColumns();
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), userColumnList);
    }

    private void updateColumnTitles() {
        for (int i = 0; i < userColumnList.getItemCount(); i++) {
            String value = userColumnList.getValue(i);
            userColumnList.setItemText(i, getColumnTitle(getColumn(value)));
        }
    }

    private String getColumnTitle(SampleColumn column) {
        return (column.getType().isFactorValue() ? "[FV] " : "") + column.getName();
    }

    private Set<String> getColumnNames() {
        Set<String> names = new HashSet<String>();
        for (SampleColumn column : columnMap.values()) {
            names.add(column.getName());
        }
        return names;
    }

    private void setColumns(List<SampleColumn> columns) {
        userColumnList.clear();
        for (SampleColumn column : columns) {
            setColumn(column, false);
        }
    }

    private void setColumn(SampleColumn column, boolean select) {
        int id = columnId();
        columnMap.put(id, column);
        userColumnList.addItem(getColumnTitle(column), Integer.toString(id));
        if (select) {
            setItemSelected(userColumnList, userColumnList.getItemCount() - 1);
        }
    }

    private List<SampleColumn> getColumns() {
        List<SampleColumn> columns = new ArrayList<SampleColumn>();
        for (int i = 0; i < userColumnList.getItemCount(); i++) {
            columns.add(getColumn(userColumnList.getValue(i)));
        }
        return columns;
    }

    private SampleColumn getColumn(String id) {
        return columnMap.get(parseInt(id));
    }

    private void updateTemplateColumns() {
        columnTemplateList.clear();
        Set<String> used = getColumnNames();

        int index = 0;
        for (SampleColumn column : templateColumns) {
            if (!used.contains(column.getName())) {
                columnTemplateList.addItem(column.getName(), Integer.toString(index));
            }
            index++;
        }
    }

    private void checkNamesUnique(List<String> errors) {
        if (getColumnNames().size() != columnMap.size()) {
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

    private static void setItemSelected(ListBox listBox, int index) {
        listBox.setItemSelected(index, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
    }
}
