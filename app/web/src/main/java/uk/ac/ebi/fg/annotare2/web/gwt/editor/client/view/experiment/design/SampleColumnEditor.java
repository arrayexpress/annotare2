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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttributeType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnEditor extends Composite implements HasValueChangeHandlers<SampleColumn> {

    private static final String INVALID_TEXT_BOX_STYLE = "app-Invalid-TextBox";

    interface Binder extends UiBinder<Widget, SampleColumnEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    ListBox typeList;

    @UiField
    TextBox nameText;

    @UiField(provided = true)
    SuggestBox termSuggest;

    @UiField(provided = true)
    SuggestBox unitsSuggest;

    private final SampleColumn column;
    private final SampleAttributeEfoSuggest efoSuggestService;

    public SampleColumnEditor(SampleColumn column, final SampleAttributeEfoSuggest efoSuggestService) {
        this.column = column.copy();
        this.efoSuggestService = efoSuggestService;

        termSuggest = new SuggestBox(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                efoSuggestService.getTerms(query, limit, callback);
            }
        }));

        unitsSuggest = new SuggestBox(new EfoSuggestOracle(new SuggestService<OntologyTerm>() {
            @Override
            public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
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

        for (SampleAttributeType type : template.getTypes()) {
            typeList.addItem(type.getName(), type.name());
        }
        setType(column.getType());
        typeList.setEnabled(template.getTypes().size() > 1);
    }

    private void setType(SampleAttributeType type) {
        for (int i = 0; i < typeList.getItemCount(); i++) {
            if (type.name().equals(typeList.getValue(i))) {
                typeList.setItemSelected(i, true);
            }
        }
    }

    private SampleAttributeType getType() {
        int index = typeList.getSelectedIndex();
        return SampleAttributeType.valueOf(typeList.getValue(index));
    }

    @UiHandler("nameText")
    void nameChanged(ChangeEvent event) {
        column.setName(nameText.getValue());
        notifyColumnChanged();
    }

    @UiHandler("typeList")
    void typeChanged(ChangeEvent event) {
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
        efoSuggestService.getTermByLabel(value, new AsyncCallback<OntologyTerm>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("EFO Service is currently unavailable; please try later");
            }

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
        });
    }

    private void notifyColumnChanged() {
        ValueChangeEvent.fire(this, column);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<SampleColumn> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
