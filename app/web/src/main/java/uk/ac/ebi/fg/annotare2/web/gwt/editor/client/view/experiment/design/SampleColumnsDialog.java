/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;

import java.util.*;

import static java.lang.Integer.parseInt;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate.USER_DEFIED_ATTRIBUTE;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate.valueOf;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnsDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, SampleColumnsDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    ListBox templateColumnList;

    @UiField
    ListBox columnList;

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

    private final SampleAttributeEfoSuggest efoSuggest;

    public SampleColumnsDialog(List<SampleColumn> columns,
                               SampleAttributeEfoSuggest efoSuggest,
                               DialogCallback<List<SampleColumn>> callback) {
        setModal(true);
        setGlassEnabled(true);
        setText("Sample Attributes");

        setWidget(Binder.BINDER.createAndBindUi(this));
        center();

        this.efoSuggest = efoSuggest;
        this.callback = callback;
        setColumns(columns);
        updateTemplates();
    }

    @UiHandler("columnList")
    void columnSelected(ChangeEvent event) {
        final int index = columnList.getSelectedIndex();
        SampleColumn column = index < 0 ? null : getColumn(columnList.getValue(index));

        if (column == null) {
            columnEditor.setWidget(new Label("No selection"));
            return;
        }

        SampleColumnEditor editor = new SampleColumnEditor(column, efoSuggest);
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
        SampleAttributeTemplate template = getSelectedTemplate();
        if (template != null) {
            templateColumnList.removeItem(templateColumnList.getSelectedIndex());
            addColumn(template);
        }
    }

    @UiHandler("removeButton")
    void removeButtonClicked(ClickEvent event) {
        removeSelectedColumn();
    }

    @UiHandler("newColumnLabel")
    void newColumnClicked(ClickEvent event) {
        addColumn(USER_DEFIED_ATTRIBUTE);
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        if (isValid()) {
            hide();
            if (null != callback) {
                callback.onOkay(getColumns());
            }
        }
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onCancel();
        }
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                hide();
                if (null != callback) {
                    callback.onCancel();
                }
            }
        }
    }
    @UiHandler("moveUpButton")
    void moveColumnUp(ClickEvent event) {
        int index = columnList.getSelectedIndex();
        if (index <= 0) {
            return;
        }
        move(index, index - 1);
    }

    @UiHandler("moveDownButton")
    void moveColumnDown(ClickEvent event) {
        int index = columnList.getSelectedIndex();
        if (index < 0 || index >= columnList.getItemCount() - 1) {
            return;
        }
        move(index, index + 1);
    }

    private void updateTemplates() {
        templateColumnList.clear();
        Set<SampleAttributeTemplate> used = getUsedTemplates();
        Collection<SampleAttributeTemplate> all = Ordering.natural().onResultOf(new Function<SampleAttributeTemplate, String>() {
            @Override
            public String apply(SampleAttributeTemplate template) {
                return template.getName();
            }
        }).sortedCopy(SampleAttributeTemplate.getAll());

        for (SampleAttributeTemplate template : all) {
            if (!used.contains(template) && template.isVisible()) {
                templateColumnList.addItem(template.getName() + (template.isFactorValueOnly() ? " (Factor Value)" : ""), template.name());
            }
        }
    }

    private Set<SampleAttributeTemplate> getUsedTemplates() {
        Set<SampleAttributeTemplate> templates = new HashSet<SampleAttributeTemplate>();
        for (SampleColumn column : columnMap.values()) {
            templates.add(column.getTemplate());
        }
        return templates;
    }

    private Set<String> getUserColumnNamesLowerCased() {
        Set<String> names = new HashSet<String>();
        for (SampleColumn column : columnMap.values()) {
            names.add(column.getName().toLowerCase());
        }
        return names;
    }

    private void addColumn(final SampleAttributeTemplate template) {
        efoSuggest.getSystemEfoTerms(new AsyncCallback<SystemEfoTermMap>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert("Unable load the ontology terms to create a column");
            }

            @Override
            public void onSuccess(SystemEfoTermMap systemEfoTermMap) {
                addColumn(template, systemEfoTermMap);
            }
        });
    }

    private void addColumn(SampleAttributeTemplate template, SystemEfoTermMap context) {
        SampleColumn column = SampleColumn.create(template, context);
        if (column == null) {
            Window.alert("Unable to add an attribute");
        } else if (getUserColumnNamesLowerCased().contains(column.getName().toLowerCase())) {
            Window.alert("Unable to add '" + column.getName() + "': attribute is already defined");
        } else {
            setColumn(column, true);
        }
    }

    private void updateColumn(int index, SampleColumn value) {
        int columnId = parseInt(columnList.getValue(index));
        columnMap.put(columnId, value);
        updateColumnTitles();
        validate();
    }

    private void removeSelectedColumn() {
        int index = columnList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        int columnId = parseInt(columnList.getValue(index));
        if (!columnMap.get(columnId).getTemplate().isMandatory()) {
            columnList.removeItem(index);
            columnMap.remove(columnId);
            updateTemplates();
            DomEvent.fireNativeEvent(Document.get().createChangeEvent(), columnList);
        } else {
            showError("Unable to remove a mandatory attribute");
        }
    }

    private void updateColumnTitles() {
        for (int i = 0; i < columnList.getItemCount(); i++) {
            String value = columnList.getValue(i);
            columnList.setItemText(i, getColumnTitle(getColumn(value)));
        }
    }

    private String getColumnTitle(SampleColumn column) {
        return column.getName() + (column.getType().isFactorValue() ? " (Factor Value)" : "");
    }

    private void setColumns(List<SampleColumn> columns) {
        columnList.clear();
        for (SampleColumn column : columns) {
            setColumn(column, false);
        }
    }

    private void setColumn(SampleColumn column, boolean select) {
        int index = columnIndex();
        columnMap.put(index, column);
        columnList.addItem(getColumnTitle(column), Integer.toString(index));
        if (select) {
            setItemSelected(columnList, columnList.getItemCount() - 1);
        }
    }

    private List<SampleColumn> getColumns() {
        List<SampleColumn> columns = new ArrayList<SampleColumn>();
        for (int i = 0; i < columnList.getItemCount(); i++) {
            columns.add(getColumn(columnList.getValue(i)));
        }
        return columns;
    }

    private SampleColumn getColumn(String id) {
        return columnMap.get(parseInt(id));
    }

    private void move(int from, int to) {
        String text = columnList.getItemText(from);
        String value = columnList.getValue(from);
        columnList.removeItem(from);
        columnList.insertItem(text, value, to);

        setItemSelected(columnList, to);
    }

    private SampleAttributeTemplate getSelectedTemplate() {
        int index = templateColumnList.getSelectedIndex();
        return index < 0 ? null :
                valueOf(templateColumnList.getValue(index));
    }

    private void checkNamesUnique(List<String> errors) {
        if (getUserColumnNamesLowerCased().size() != columnMap.size()) {
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

    private int columnIndex() {
        return ++nextId;
    }

    private static void setItemSelected(ListBox listBox, int index) {
        listBox.setItemSelected(index, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
    }
}
