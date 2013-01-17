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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.magetab.idf.Term;
import uk.ac.ebi.fg.annotare2.magetab.idf.TermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.DynamicList;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ChangeableValues.hasChangeableValue;

/**
 * @author Olga Melnichuk
 */
public class ExperimentalDesignView extends IdfItemView<Term> {

    interface Binder extends UiBinder<Widget, ExperimentalDesignView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    protected TextBox nameBox;

    @UiField
    protected ListBox termSourceBox;

    private final List<TermSource> termSourceList = new ArrayList<TermSource>();

    public ExperimentalDesignView(Term term, final DynamicList<TermSource> termSources) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        termSources.addChangeHandler(new DynamicList.ChangeHandler() {
            @Override
            public void onChange() {
                updateListBox(termSources, getItem());
            }
        });

        updateListBox(termSources, term);

        addHeaderField(hasChangeableValue(nameBox));

        addField(new EditableField<Term, String>(hasChangeableValue(nameBox)) {

            @Override
            protected String getValue(Term obj) {
                return obj.getName().getValue();
            }

            @Override
            protected void setValue(Term obj, String value) {
                obj.getName().setValue(value);
            }
        });

        addField(new EditableField<Term, Integer>(hasChangeableValue(termSourceBox)) {

            @Override
            protected Integer getValue(Term obj) {
                TermSource ts = obj.getTermSource();
                return ts == null ? 0 : termSourceList.indexOf(ts);
            }

            @Override
            protected void setValue(Term obj, Integer value) {
                obj.setTermSource(termSourceList.get(value));
            }
        });

        setItem(term);
    }

    private void updateListBox(DynamicList<TermSource> termSources, Term selected) {
        String selectedValue = (selected.getTermSource() == null) ? "" : selected.getTermSource().getName().getValue();

        termSourceList.clear();
        termSourceList.addAll(termSources.getValues());

        termSourceBox.clear();
        termSourceBox.addItem("none");
        for (TermSource ts : termSourceList) {
            String v = ts.getName().getValue();
            termSourceBox.addItem(v);
            if (selectedValue.contains(v)) {
                termSourceBox.setSelectedIndex(termSourceBox.getItemCount() - 1);
            }
        }
    }
}
