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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.ColumnValueType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.NumericValueType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.OntologyTermValueType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.TextValueType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ColumnValueTypeEditor extends Composite implements HasValue<ColumnValueType> {

    interface Binder extends UiBinder<Widget, ColumnValueTypeEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField(provided = true)
    ValueListBox<ValueType> typeSelector;

    @UiField
    SimplePanel typeExtras;

    private ValueTypeEditor editor;

    private final ColumnValueType.Visitor visitor;

    public ColumnValueTypeEditor(final ColumnValueTypeEfoTerms efoTerms) {
        visitor = new ColumnValueType.Visitor() {
            @Override
            public void visitTextValueType(TextValueType valueType) {
                setEditor(new TextTypeEditor());
            }

            @Override
            public void visitTermValueType(OntologyTermValueType valueType) {
                setEditor(new EfoTermTypeEditor(valueType, new SuggestService<OntologyTerm>() {
                    @Override
                    public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                        efoTerms.getTerms(query, limit, callback);
                    }
                }));
            }

            @Override
            public void visitNumericValueType(NumericValueType valueType) {
                setEditor(new NumberTypeEditor(valueType, new SuggestService<OntologyTerm>() {
                    @Override
                    public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                        efoTerms.getUnits(query, limit, callback);
                    }
                }));
            }
        };

        typeSelector = new ValueListBox<ValueType>(new AbstractRenderer<ValueType>() {
            @Override
            public String render(ValueType object) {
                return object == null ? "" : object.getTitle();
            }
        });

        initWidget(Binder.BINDER.createAndBindUi(this));
        typeSelector.setValue(ValueType.TEXT);
        typeSelector.setAcceptableValues(Arrays.asList(ValueType.values()));
        typeSelector.addValueChangeHandler(new ValueChangeHandler<ValueType>() {
            @Override
            public void onValueChange(ValueChangeEvent<ValueType> event) {
                event.getValue().visit(visitor);
            }
        });
    }

    @Override
    public ColumnValueType getValue() {
        return editor.getValue();
    }

    @Override
    public void setValue(ColumnValueType value) {
        setValue(value, false);
    }

    @Override
    public void setValue(ColumnValueType value, boolean fireEvents) {
        value.visit(visitor);
        if (fireEvents) {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ColumnValueType> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setEnabled(boolean enabled) {
        editor.setEnabled(enabled);
        DOM.setElementPropertyBoolean(typeSelector.getElement(), "disabled", !enabled);
    }

    private void setEditor(ValueTypeEditor editor) {
        this.editor = editor;
        this.typeExtras.setWidget(editor.getWidget());
        setType(editor.getType());
    }

    private void setType(ValueType valueType) {
        this.typeSelector.setValue(valueType);
    }

    private static enum ValueType {
        TEXT("Text") {
            @Override
            public void visit(ColumnValueType.Visitor visitor) {
                visitor.visitTextValueType(null);
            }
        },
        NUMBER("Measure") {
            @Override
            public void visit(ColumnValueType.Visitor visitor) {
                visitor.visitNumericValueType(null);
            }
        },
        EFO_TERM("EFO branch") {
            @Override
            public void visit(ColumnValueType.Visitor visitor) {
                visitor.visitTermValueType(null);
            }
        };

        private final String title;

        private ValueType(String title) {
            this.title = title;
        }

        private String getTitle() {
            return title;
        }

        public abstract void visit(ColumnValueType.Visitor visitor);
    }

    private interface ValueTypeEditor {
        ColumnValueType getValue();

        void setEnabled(boolean enabled);

        Widget getWidget();

        ValueType getType();
    }

    private static class EfoTermTypeEditor implements ValueTypeEditor {

        private SuggestBox efoTermBox;

        private OntologyTerm selection;

        private EfoTermTypeEditor(OntologyTermValueType valueType, SuggestService<OntologyTerm> suggestService) {
            efoTermBox = new SuggestBox(new EfoSuggestOracle(suggestService));
            efoTermBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
                @Override
                public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                    EfoSuggestOracle.EfoTermSuggestion suggestion = (EfoSuggestOracle.EfoTermSuggestion) event.getSelectedItem();
                    setSelection(suggestion.getTerm());
                }
            });

            if (valueType != null) {
                setSelection(valueType.getEfoTerm());
                efoTermBox.setValue(valueType.getEfoTerm() == null ? "" : valueType.getEfoTerm().getLabel());
            }
        }

        private void setSelection(OntologyTerm efoTerm) {
            selection = efoTerm;
        }

        @Override
        public ValueType getType() {
            return ValueType.EFO_TERM;
        }

        @Override
        public Widget getWidget() {
            return efoTermBox;
        }

        @Override
        public ColumnValueType getValue() {
            return new OntologyTermValueType(selection);
        }

        @Override
        public void setEnabled(boolean enabled) {
            efoTermBox.setEnabled(enabled);
        }
    }

    private static class NumberTypeEditor implements ValueTypeEditor {

        private SuggestBox units;

        private NumberTypeEditor(NumericValueType value, SuggestService<OntologyTerm> suggestService) {
            units = new SuggestBox(new EfoSuggestOracle(suggestService));
            if (value != null) {
                // TODO set units
            }
        }

        @Override
        public ValueType getType() {
            return ValueType.NUMBER;
        }

        @Override
        public Widget getWidget() {
            return units;
        }

        @Override
        public ColumnValueType getValue() {
            return new NumericValueType();
        }

        @Override
        public void setEnabled(boolean enabled) {
            units.setEnabled(enabled);
        }
    }

    private static class TextTypeEditor implements ValueTypeEditor {

        private TextTypeEditor() {
        }

        @Override
        public ValueType getType() {
            return ValueType.TEXT;
        }

        @Override
        public Widget getWidget() {
            return new Label("");
        }

        @Override
        public ColumnValueType getValue() {
            return new TextValueType();
        }

        @Override
        public void setEnabled(boolean enabled) {
        }
    }
}
