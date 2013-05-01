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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.TermSource;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ChangeableValues.hasChangeableValue;

/**
 * @author Olga Melnichuk
 */
public class TermSourceView extends ItemView<TermSource> {

    interface Binder extends UiBinder<Widget, TermSourceView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox nameBox;

    @UiField
    TextBox urlBox;

    @UiField
    TextBox versionBox;

    public TermSourceView(TermSource ts) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        addHeaderField(hasChangeableValue(nameBox));

        addField(new EditableField<TermSource, String>(hasChangeableValue(nameBox)) {

            @Override
            protected String getValue(TermSource obj) {
                return obj.getName().getValue();
            }

            @Override
            protected void setValue(TermSource obj, String value) {
                obj.getName().setValue(value);
            }
        });

        addField(new EditableField<TermSource, String>(hasChangeableValue(urlBox)) {
            @Override
            protected String getValue(TermSource obj) {
                return obj.getFile().getValue();
            }

            @Override
            protected void setValue(TermSource obj, String value) {
                obj.getFile().setValue(value);
            }
        });

        addField(new EditableField<TermSource, String>(hasChangeableValue(versionBox)) {
            @Override
            protected String getValue(TermSource obj) {
                return obj.getVersion().getValue();
            }

            @Override
            protected void setValue(TermSource obj, String value) {
                obj.getVersion().setValue(value);
            }
        });

        setItem(ts);
    }
}
