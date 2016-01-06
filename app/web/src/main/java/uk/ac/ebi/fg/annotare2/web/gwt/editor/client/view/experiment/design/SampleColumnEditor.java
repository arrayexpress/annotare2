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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttributeType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnEditor extends Composite implements HasValueChangeHandlers<SampleColumn> {

    private static final String INVALID_TEXT_BOX_STYLE = "app-Invalid-TextBox";

    interface Binder extends UiBinder<Widget, SampleColumnEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    CheckBox saChkBox;

    @UiField
    CheckBox fvChkBox;

    @UiField
    TextBox nameText;

    @UiField(provided = true)
    SuggestBox termSuggest;

    @UiField(provided = true)
    SuggestBox unitsSuggest;

    private final SampleColumn column;
    private final SampleAttributeEfoSuggest efoSuggestService;

    private boolean isSaEnabled;
    private boolean isFvEnabled;

    public SampleColumnEditor(SampleColumn column, final SampleAttributeEfoSuggest efoSuggestService) {
        this.column = column.copy();
        this.efoSuggestService = efoSuggestService;

        termSuggest = new SuggestBox(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
                efoSuggestService.getTerms(query, limit, callback);
            }
        }));

        unitsSuggest = new SuggestBox(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
                efoSuggestService.getUnits(query, limit, callback);
            }
        }));

        initWidget(Binder.BINDER.createAndBindUi(this));

        SampleAttributeTemplate template = column.getTemplate();

        nameText.setValue(column.getName());
        nameText.setEnabled(template.getNameRange().isAny());

        termSuggest.setValue(column.getTerm() == null ? "" : column.getTerm().getLabel());
        termSuggest.setEnabled(template.getTermRange().isAny());

        unitsSuggest.setValue(column.getUnits() == null ? "" : column.getUnits().getLabel());
        unitsSuggest.setEnabled(template.getUnitRange().isAny());

        boolean saPresent = false, saAbsent = false, fvPresent = false, fvAbsent = false;
        for (SampleAttributeType type : template.getTypes()) {
            if (type.isCharacteristic()) {
                saPresent = true;
            } else {
                saAbsent = true;
            }
            if (type.isFactorValue()) {
                fvPresent = true;
            } else {
                fvAbsent = true;
            }
        }
        isSaEnabled = saPresent && saAbsent;
        isFvEnabled = fvPresent && fvAbsent;

        saChkBox.setValue(column.getType().isCharacteristic());
        fvChkBox.setValue(column.getType().isFactorValue());

        setChkBoxesEnabled();

        saChkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                saChkBoxChanged();
            }
        });
        fvChkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> booleanValueChangeEvent) {
                fvChkBoxChanged();
            }
        });
    }

    @UiHandler("nameText")
    void nameChanged(ChangeEvent event) {
        column.setName(nameText.getValue());
        notifyColumnChanged();
    }

    void setChkBoxesEnabled() {
        if (isSaEnabled && isFvEnabled) {
            saChkBox.setEnabled(fvChkBox.getValue());
            fvChkBox.setEnabled(saChkBox.getValue());
        } else {
            saChkBox.setEnabled(isSaEnabled);
            fvChkBox.setEnabled(isFvEnabled);
        }
    }

    void saChkBoxChanged() {
        setChkBoxesEnabled();
        typeChanged();
    }

    void fvChkBoxChanged() {
        setChkBoxesEnabled();
        typeChanged();
    }

    SampleAttributeType getType() {
        if (saChkBox.getValue()) {
            if (fvChkBox.getValue()) {
                return SampleAttributeType.CHARACTERISTIC_AND_FACTOR_VALUE;
            } else {
                return SampleAttributeType.CHARACTERISTIC;
            }
        } else {
            if (fvChkBox.getValue()) {
                return SampleAttributeType.FACTOR_VALUE;
            }
        }
        return null;
    }

    void typeChanged() {
        column.setType(getType());
        notifyColumnChanged();
    }

    @UiHandler("termSuggest")
    void termValueChanged(ValueChangeEvent<String> event) {
        validateEfoTerm(termSuggest, new AsyncCallback<OntologyTerm>() {
            @Override
            public void onFailure(Throwable caught) {
                //not called
            }

            @Override
            public void onSuccess(OntologyTerm term) {
                column.setTerm(term);
                notifyColumnChanged();
            }
        });
    }

    @UiHandler("unitsSuggest")
    void unitsValueChanged(ValueChangeEvent<String> event) {
        validateEfoTerm(unitsSuggest, new AsyncCallback<OntologyTerm>() {
            @Override
            public void onFailure(Throwable caught) {
                //not called
            }

            @Override
            public void onSuccess(OntologyTerm term) {
                column.setUnits(term);
                notifyColumnChanged();
            }
        });
    }

    private void validateEfoTerm(final SuggestBox suggestBox, final AsyncCallback<OntologyTerm> asyncCallback) {
        suggestBox.removeStyleName(INVALID_TEXT_BOX_STYLE);
        String value = suggestBox.getValue();
        value = value.trim();
        if (value.isEmpty()) {
            asyncCallback.onSuccess(null);
            return;
        }
        efoSuggestService.getTermByLabel(value,
                new ReportingAsyncCallback<OntologyTerm>(FailureMessage.UNABLE_TO_LOAD_EFO) {
                    @Override
                    public void onSuccess(OntologyTerm term) {
                        if (term == null) {
                            suggestBox.addStyleName(INVALID_TEXT_BOX_STYLE);
                            asyncCallback.onSuccess(null);
                        } else {
                            suggestBox.setValue(term.getLabel(), false);
                            asyncCallback.onSuccess(term);
                        }
                    }
                }
        );
    }

    private void notifyColumnChanged() {
        ValueChangeEvent.fire(this, column);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SampleColumn> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
