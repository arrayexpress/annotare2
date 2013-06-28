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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.configmodel.AttributeType;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.ColumnValueType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnEditor extends Composite implements HasValueChangeHandlers<SampleColumn> {

    interface Binder extends UiBinder<Widget, SampleColumnEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox nameBox;

    @UiField
    CheckBox factorValueCheckbox;

    @UiField(provided = true)
    ColumnValueTypeEditor valueTypeEditor;

    @UiField(provided = true)
    SuggestBox nameTermBox;

    private SampleColumn.Editor columnEditor;

    public SampleColumnEditor(SampleColumn column, final ColumnValueTypeEfoTerms efoSuggestService) {
        valueTypeEditor = new ColumnValueTypeEditor(efoSuggestService);
        nameTermBox = new SuggestBox(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                efoSuggestService.getTerms(query, limit, callback);
            }
        }));

        initWidget(Binder.BINDER.createAndBindUi(this));

        nameBox.setValue(column.getName());
        nameBox.setEnabled(column.isEditable());

        nameTermBox.setValue(column.getTerm() == null ? "" : column.getTerm().getLabel());
        nameTermBox.setEnabled(column.isEditable());
        nameTermBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
            @Override
            public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                EfoSuggestOracle.EfoTermSuggestion suggestion = (EfoSuggestOracle.EfoTermSuggestion) event.getSelectedItem();
                columnEditor.setTerm(suggestion.getTerm());
                notifyColumnChanged();
            }
        });

        valueTypeEditor.setValue(column.getValueType());
        valueTypeEditor.setEnabled(column.isEditable());

        AttributeType type = column.getType();
        factorValueCheckbox.setValue(type.isFactorValue());
        factorValueCheckbox.setVisible(type.isFactorValue() || type.isCharacteristic());

        this.columnEditor = column.editor();
    }

    @UiHandler("nameBox")
    void nameValueChanged(ChangeEvent event) {
        if (!columnEditor.isEditable()) {
            return;
        }
        columnEditor.setName(nameBox.getValue());
        notifyColumnChanged();
    }

    @UiHandler("factorValueCheckbox")
    void factorValueCheckboxChanged(ValueChangeEvent<Boolean> event) {
        AttributeType type = columnEditor.getType();
        if (type.isFactorValue() || type.isCharacteristic()) {
            columnEditor.setType(event.getValue() ? AttributeType.FACTOR_VALUE_ATTRIBUTE : AttributeType.CHARACTERISTIC_ATTRIBUTE);
            notifyColumnChanged();
        }
    }

    @UiHandler("valueTypeEditor")
    void valueTypeChanged(ValueChangeEvent<ColumnValueType> event) {
        if (!columnEditor.isEditable()) {
            return;
        }
        columnEditor.setValueType(event.getValue());
        notifyColumnChanged();
    }

    private void notifyColumnChanged() {
        ValueChangeEvent.fire(this, columnEditor.copy());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SampleColumn> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
