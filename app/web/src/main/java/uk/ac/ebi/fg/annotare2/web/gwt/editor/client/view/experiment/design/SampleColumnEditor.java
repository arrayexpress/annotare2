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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttributeType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate;
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
    ListBox typeBox;

    @UiField
    TextBox nameBox;

    @UiField(provided = true)
    SuggestBox termBox;

    @UiField(provided = true)
    SuggestBox unitsBox;

    private final SampleColumn column;

    public SampleColumnEditor(SampleColumn column, final SampleAttributeEfoSuggest efoSuggestService) {
        this.column = column;

        termBox = new SuggestBox(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                efoSuggestService.getTerms(query, limit, callback);
            }
        }));

        unitsBox = new SuggestBox(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                efoSuggestService.getUnits(query, limit, callback);
            }
        }));

        initWidget(Binder.BINDER.createAndBindUi(this));

        SampleAttributeTemplate template = column.getTemplate();

        termBox.setEnabled(template.getTermRange().isAny());

        unitsBox.setEnabled(template.getUnitRange().isAny());

        nameBox.setValue(column.getName());
        nameBox.setEnabled(template.getNameRange().isAny());

        termBox.setValue(column.getTerm() == null ? "" : column.getTerm().getLabel());
        termBox.setEnabled(template.getTermRange().isAny());

        for (SampleAttributeType type : template.getTypes()) {
            typeBox.addItem(type.getName(), type.name());
        }
        //TODO
        //setType(column.getType());
        typeBox.setEnabled(template.getTypes().size() > 1);
    }

/*
//TODO

    @UiHandler("nameBox")
    void nameValueChanged(ChangeEvent event) {
        column.setName(nameBox.getValue());
        notifyColumnChanged();
    }
    @UiHandler("factorValueCheckbox")
    void factorValueCheckboxChanged(ValueChangeEvent<Boolean> event) {
        SampleAttributeType type = columnEditor.getType();
        if (type.isFactorValue() || type.isCharacteristic()) {
            columnEditor.setType(event.getValue() ? SampleAttributeType.CHARACTERISTIC_AND_FACTOR_VALUE : SampleAttributeType.CHARACTERISTIC);
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
    }*/

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SampleColumn> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
