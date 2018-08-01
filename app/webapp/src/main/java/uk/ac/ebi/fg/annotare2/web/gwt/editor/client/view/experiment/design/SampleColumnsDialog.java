/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.ExpProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDesignToAttributesMapping;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDesignType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentProfileTypeToAttributesMapping;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.*;

import static java.lang.Integer.parseInt;
import static java.util.EnumSet.of;
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

    private Map<Integer, SampleColumn> columnMap = new HashMap<Integer, SampleColumn>();

    private DialogCallback<List<SampleColumn>> callback;

    private Collection<OntologyTerm> experimentDesigns = new ArrayList<>();

    private int nextId;

    private List<String> attributeTemplates = new ArrayList<>();

    private List<String> mandatoryAttributeTemplates = new ArrayList<>();

    private List<SampleAttributeTemplate> mandatoryTemplates = new ArrayList<>();

    private final SampleAttributeEfoSuggest efoSuggest;

    private final ExpProfileType experimentProfileType;

    public SampleColumnsDialog(List<SampleColumn> columns,
                               SampleAttributeEfoSuggest efoSuggest,
                               Collection<OntologyTerm> experimentDesigns,
                               ExpProfileType experimentProfileType,
                               DialogCallback<List<SampleColumn>> callback) {
        setModal(true);
        setGlassEnabled(true);
        setText("Sample Attributes and Experimental Variables");

        setWidget(Binder.BINDER.createAndBindUi(this));
        center();

        this.experimentDesigns = experimentDesigns;

        this.templateColumnList.sinkEvents(Event.ONDBLCLICK);
        this.templateColumnList.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                addButtonClicked(null);
            }
        }, DoubleClickEvent.getType());

        this.columnList.sinkEvents(Event.ONDBLCLICK);
        this.columnList.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                removeButtonClicked(null);
            }
        }, DoubleClickEvent.getType());

        this.efoSuggest = efoSuggest;
        this.callback = callback;

        setColumns(columns);
        updateTemplates();

        for (Map.Entry<Integer,SampleColumn> entry : columnMap.entrySet())
        {
            attributeTemplates.add(entry.getValue().getName());
        }

        this.experimentProfileType = experimentProfileType;
        addMandatoryColumns(experimentProfileType);
    }

    private void setMandatoryColumn()
    {
        List<String> expDesignTypes = new ArrayList<>();
        for(OntologyTerm term: experimentDesigns)
        {
            expDesignTypes.add(term.getLabel());
        }

        for(ExperimentDesignType designType: ExperimentDesignType.values())
        {
            String experimentDesignType = designType.getLabel();
            if(expDesignTypes.contains(designType.getLabel())) {

                for (SampleAttributeTemplate attribute : designType.getAttributes()) {

                    if(!attributeTemplates.contains(attribute.getName())) {

                        attribute.setIsVisible(true);
                        attributeTemplates.add(attribute.getName());

                        if(mandatoryAttributeTemplates.contains(attribute.getName().toLowerCase()) &&
                                (isUsedIn(experimentDesignType))) {

                            removeAddedColumn(attribute);
                            addColumn(attribute, experimentDesignType);
                        }
                        else if(isUsedIn(experimentDesignType))
                        {
                            addColumn(attribute, experimentDesignType);
                        }
                        else {
                            if(!mandatoryAttributeTemplates.contains(attribute.getName().toLowerCase()))
                                addColumn(attribute);
                        }
                    }
                }
            }

        }
    }

    private boolean isUsedIn(String experimentDesign)
    {
        for (ExperimentDesignToAttributesMapping exp:
             ExperimentDesignToAttributesMapping.values()) {

            if(exp.isOkay(experimentDesign))
            {
                return true;
            }
        }
        return false;
    }

    private void removeAddedColumn(SampleAttributeTemplate attributeTemplate)
    {
        for (int i = 0; i< columnList.getItemCount();i++) {

            if (columnList.getItemText(i).replaceAll("[()\\s]","").equalsIgnoreCase(attributeTemplate.getName().replaceAll("[()\\s]","")) ||
                    columnList.getItemText(i).replaceAll("[()\\s]","").equalsIgnoreCase(attributeTemplate.getName().replaceAll("[()\\s]","")+"experimentalvariable")) {


                int columnId = parseInt(columnList.getValue(i));
                    columnList.removeItem(i);
                    columnMap.remove(columnId);
                    updateTemplates();
                    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), columnList);
                break;
            }

        }
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
        updateColumnTitles();
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
        if (areNamesUnique()) {
            hide();
            if (null != callback) {
                callback.onOk(getColumns());
            }
        } else {
            NotificationPopupPanel.error("There are multiple attributes or experimental variables with the same name.", true, false);
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

    private void addMandatoryColumns(ExpProfileType experimentProfileType) {

        List<SampleAttributeTemplate> attributeTemplates;
        ExpProfileType experimentType;

        Set<SampleAttributeTemplate> used = getUsedTemplates();

        for (ExperimentProfileTypeToAttributesMapping expTypeToAttribute:
             ExperimentProfileTypeToAttributesMapping.values()) {

            experimentType = expTypeToAttribute.getExpProfileType();

            if(experimentType == experimentProfileType) {
                attributeTemplates = expTypeToAttribute.getAttributes();

                for (SampleAttributeTemplate attributeTemplate :
                        attributeTemplates) {
                    if (!used.contains(attributeTemplate))
                    {
                        mandatoryTemplates.add(attributeTemplate);
                        mandatoryAttributeTemplates.add(attributeTemplate.getName().toLowerCase());
                    }
                }
            }
        }


        Collection<SampleAttributeTemplate> all = SampleAttributeTemplate.getAll();

        for (SampleAttributeTemplate template : all) {
            if (!used.contains(template) && template.isMandatory() && !mandatoryTemplates.contains(template)) {
                mandatoryTemplates.add(template);
                mandatoryAttributeTemplates.add(template.getName().toLowerCase());
            }
        }
        addColumn(mandatoryTemplates);
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
                templateColumnList.addItem(template.getName() + (template.isFactorValueOnly() ? " (Experimental Variable)" : ""), template.name());
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

    private void addColumn(final SampleAttributeTemplate template, final String experimentDesignType) {
        efoSuggest.getSystemEfoTerms(
                new ReportingAsyncCallback<SystemEfoTermMap>(FailureMessage.UNABLE_TO_LOAD_EFO) {
                    @Override
                    public void onSuccess(SystemEfoTermMap systemEfoTermMap) {
                        addColumn(template, systemEfoTermMap, experimentDesignType);
                    }
                }
        );
    }

    private void addColumn(final SampleAttributeTemplate template) {
        efoSuggest.getSystemEfoTerms(
                new ReportingAsyncCallback<SystemEfoTermMap>(FailureMessage.UNABLE_TO_LOAD_EFO) {
                    @Override
                    public void onSuccess(SystemEfoTermMap systemEfoTermMap) {
                        addColumn(template, systemEfoTermMap);
                    }
                }
        );
    }

    private void addColumn(final List<SampleAttributeTemplate> templates) {
        efoSuggest.getSystemEfoTerms(
                new ReportingAsyncCallback<SystemEfoTermMap>(FailureMessage.UNABLE_TO_LOAD_EFO) {
                    @Override
                    public void onSuccess(SystemEfoTermMap systemEfoTermMap) {
                        addColumn(templates, systemEfoTermMap);
                        setMandatoryColumn();
                        updateTemplates();

                    }
                }
        );
    }

    private void addColumn(SampleAttributeTemplate template, SystemEfoTermMap context, String experimentDesignType) {
        SampleColumn column = SampleColumn.create(template, context, experimentDesignType);
        if (null == column) {
            NotificationPopupPanel.error("Unable to add an attribute.", true, false);
        } else if (getUserColumnNamesLowerCased().contains(column.getName().toLowerCase())) {
            NotificationPopupPanel.error("Unable to add '" + column.getName() + "': attribute is already defined.", true, false);
        } else {
                setColumn(column, true);
        }
    }

    private void addColumn(SampleAttributeTemplate template, SystemEfoTermMap context) {
        SampleColumn column = SampleColumn.create(template, context);
        if (null == column) {
            NotificationPopupPanel.error("Unable to add an attribute.", true, false);
        } else if (getUserColumnNamesLowerCased().contains(column.getName().toLowerCase())) {
            NotificationPopupPanel.error("Unable to add '" + column.getName() + "': attribute is already defined.", true, false);
        } else {
            setColumn(column, true);
        }
    }

    private void addColumn(List<SampleAttributeTemplate> templates, SystemEfoTermMap context) {
        for (SampleAttributeTemplate template: templates
             ) {
            SampleColumn column = SampleColumn.create(template, context);
            if (null == column) {
                NotificationPopupPanel.error("Unable to add an attribute.", true, false);
            } else if (getUserColumnNamesLowerCased().contains(column.getName().toLowerCase())) {
                NotificationPopupPanel.error("Unable to add '" + column.getName() + "': attribute is already defined.", true, false);
            } else {
                setColumn(column, true);

            }
        }
    }

    private void updateColumn(int index, SampleColumn value) {
        int columnId = parseInt(columnList.getValue(index));
        columnMap.put(columnId, value);
        updateColumnTitles();
        if (!areNamesUnique()) {
            NotificationPopupPanel.error("Sample attribute with the name '" + value.getName() + "' already exists.", true, false);
        }
    }

    private void removeSelectedColumn() {
        int index = columnList.getSelectedIndex();
        if (index < 0) {
            return;
        }
        int columnId = parseInt(columnList.getValue(index));
        SampleAttributeTemplate template = columnMap.get(columnId).getTemplate();
        if (!template.isMandatory() && !mandatoryTemplates.contains(template) && !hasTemplate(template,experimentProfileType)) {
            columnList.removeItem(index);
            columnMap.remove(columnId);
            updateTemplates();
            DomEvent.fireNativeEvent(Document.get().createChangeEvent(), columnList);
        } else {
            NotificationPopupPanel.error("Attribute '" + template.getName() + "' is mandatory and cannot be removed.", true, false);
        }
    }

    private void updateColumnTitles() {
        for (int i = 0; i < columnList.getItemCount(); i++) {
            String value = columnList.getValue(i);
            columnList.setItemText(i, getColumnTitle(getColumn(value)));
        }
    }

    private String getColumnTitle(SampleColumn column) {
        return column.getName() + (column.getType().isFactorValue() ? " (Experimental Variable)" : "");
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

    private boolean areNamesUnique() {
        return getUserColumnNamesLowerCased().size() == columnMap.size();
    }

    private int columnIndex() {
        return ++nextId;
    }

    private static void setItemSelected(ListBox listBox, int index) {
        listBox.setItemSelected(index, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
    }

    private boolean hasTemplate(SampleAttributeTemplate template, ExpProfileType experimentProfileType)
    {
        List<SampleAttributeTemplate> attributeTemplates;
        ExpProfileType experimentType;

        for (ExperimentProfileTypeToAttributesMapping expTypeToAttribute:
                ExperimentProfileTypeToAttributesMapping.values()) {

            experimentType = expTypeToAttribute.getExpProfileType();

            if(experimentType == experimentProfileType) {
                attributeTemplates = expTypeToAttribute.getAttributes();

                for (SampleAttributeTemplate attributeTemplate :
                        attributeTemplates) {
                    if (template == attributeTemplate) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
