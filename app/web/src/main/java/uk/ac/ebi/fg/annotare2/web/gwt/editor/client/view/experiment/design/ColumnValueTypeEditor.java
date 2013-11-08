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
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
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
    ValueListBox<EditorType> typeSelector;

    @UiField
    SimplePanel editorPanel;

    private Editor<? extends ColumnValueType> editor;

    private final ColumnValueType.Visitor visitor;

    public ColumnValueTypeEditor(final ColumnValueTypeEfoTerms efoTerms) {
        visitor = new ColumnValueType.Visitor() {
            @Override
            public void visitTextValueType(TextValueType valueType) {
                Editor<TextValueType> editor = new TextEditor();
                editor.addValueChangeHandler(new ValueChangeHandler<TextValueType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<TextValueType> event) {
                        fireValueChangeEvent(event.getValue());
                    }
                });
                setEditor(editor);
            }

            @Override
            public void visitTermValueType(OntologyTermValueType valueType) {
                Editor<OntologyTermValueType> editor = new EfoTermEditor(valueType, new SuggestService<OntologyTerm>() {
                    @Override
                    public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                        efoTerms.getTerms(query, limit, callback);
                    }
                });
                editor.addValueChangeHandler(new ValueChangeHandler<OntologyTermValueType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<OntologyTermValueType> event) {
                        fireValueChangeEvent(event.getValue());
                    }
                });
                setEditor(editor);
            }

            @Override
            public void visitNumericValueType(NumericValueType valueType) {
                Editor<NumericValueType> editor = new NumberEditor(valueType, new SuggestService<OntologyTerm>() {
                    @Override
                    public void suggest(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
                        efoTerms.getUnits(query, limit, callback);
                    }
                });
                editor.addValueChangeHandler(new ValueChangeHandler<NumericValueType>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<NumericValueType> event) {
                        fireValueChangeEvent(event.getValue());
                    }
                });
                setEditor(editor);
            }
        };

        typeSelector = new ValueListBox<EditorType>(new AbstractRenderer<EditorType>() {
            @Override
            public String render(EditorType object) {
                return object == null ? "" : object.getTitle();
            }
        });

        initWidget(Binder.BINDER.createAndBindUi(this));
        typeSelector.setValue(EditorType.TEXT);
        typeSelector.setAcceptableValues(Arrays.asList(EditorType.values()));
        typeSelector.addValueChangeHandler(new ValueChangeHandler<EditorType>() {
            @Override
            public void onValueChange(ValueChangeEvent<EditorType> event) {
                event.getValue().visit(visitor);
                fireValueChangeEvent(editor.getValue());
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
            fireValueChangeEvent(value);
        }
    }

    private void fireValueChangeEvent(ColumnValueType value) {
        ValueChangeEvent.fire(this, value);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ColumnValueType> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setEnabled(boolean enabled) {
        editor.setEnabled(enabled);
        DOM.setElementPropertyBoolean(typeSelector.getElement(), "disabled", !enabled);
    }

    private void setEditor(Editor<? extends ColumnValueType> newEditor) {
        editor = newEditor;
        this.editorPanel.setWidget(newEditor.asWidget());
        setType(newEditor.getType());
    }

    private void setType(EditorType editorType) {
        this.typeSelector.setValue(editorType);
    }

    private static enum EditorType {
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

        private EditorType(String title) {
            this.title = title;
        }

        private String getTitle() {
            return title;
        }

        public abstract void visit(ColumnValueType.Visitor visitor);
    }

    private interface Editor<T extends ColumnValueType> extends IsWidget, HasValueChangeHandlers<T> {

        T getValue();

        void setEnabled(boolean enabled);

        EditorType getType();
    }

    private static class EfoTermEditor extends Composite implements Editor<OntologyTermValueType> {

        private SuggestBox efoBranchSuggest;

        private OntologyTerm selection;

        private EfoTermEditor(OntologyTermValueType valueType, SuggestService<OntologyTerm> suggestService) {
            efoBranchSuggest = new SuggestBox(new EfoSuggestOracle(suggestService));
            efoBranchSuggest.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
                @Override
                public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                    EfoSuggestOracle.EfoTermSuggestion suggestion = (EfoSuggestOracle.EfoTermSuggestion) event.getSelectedItem();
                    selection = suggestion.getTerm();
                }
            });
            efoBranchSuggest.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    String value = event.getValue();
                    if (selection != null && !selection.getLabel().equals(value)) {
                        selection = null;
                    }
                    fireChangeEvent();
                }
            });

            if (valueType != null) {
                selection = valueType.getEfoTerm();
                efoBranchSuggest.setValue(selection == null ? "" : selection.getLabel());
            }
            initWidget(efoBranchSuggest);
        }

        @Override
        public EditorType getType() {
            return EditorType.EFO_TERM;
        }

        @Override
        public OntologyTermValueType getValue() {
            return new OntologyTermValueType(selection);
        }

        @Override
        public void setEnabled(boolean enabled) {
            efoBranchSuggest.setEnabled(enabled);
        }

        @Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<OntologyTermValueType> handler) {
            return addHandler(handler, ValueChangeEvent.getType());
        }

        private void fireChangeEvent() {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    private static class NumberEditor extends Composite implements Editor<NumericValueType> {

        private SuggestBox measureSuggest;

        private OntologyTerm selection;

        private NumberEditor(NumericValueType value, SuggestService<OntologyTerm> suggestService) {
            measureSuggest = new SuggestBox(new EfoSuggestOracle(suggestService));
            measureSuggest.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
                @Override
                public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event) {
                    EfoSuggestOracle.EfoTermSuggestion suggestion = (EfoSuggestOracle.EfoTermSuggestion) event.getSelectedItem();
                    selection = suggestion.getTerm();
                }
            });
            measureSuggest.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    String value = event.getValue();
                    if (selection != null && !selection.getLabel().equals(value)) {
                        selection = null;
                    }
                    fireChangeEvent();
                }
            });
            if (value != null) {
                selection = value.getUnits();
                measureSuggest.setValue(selection == null ? "" : selection.getLabel());
            }
            initWidget(measureSuggest);
        }

        @Override
        public EditorType getType() {
            return EditorType.NUMBER;
        }

        @Override
        public Widget getWidget() {
            return measureSuggest;
        }

        @Override
        public NumericValueType getValue() {
            return new NumericValueType(selection);
        }

        @Override
        public void setEnabled(boolean enabled) {
            //TODO: are units always enabled?
            // measureSuggest.setEnabled(enabled);
        }

        @Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<NumericValueType> handler) {
            return addHandler(handler, ValueChangeEvent.getType());
        }

        private void fireChangeEvent() {
            ValueChangeEvent.fire(this, getValue());
        }
    }

    private static class TextEditor extends Composite implements Editor<TextValueType> {

        private TextEditor() {
            initWidget(new Label(""));
        }

        @Override
        public EditorType getType() {
            return EditorType.TEXT;
        }

        @Override
        public TextValueType getValue() {
            return new TextValueType();
        }

        @Override
        public void setEnabled(boolean enabled) {
        }

        @Override
        public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TextValueType> handler) {
            return addHandler(handler, ValueChangeEvent.getType());
        }
    }
}
