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
import uk.ac.ebi.fg.annotare2.magetab.idf.TermSource;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ChangeableValues.hasChangeableValue;

/**
 * @author Olga Melnichuk
 */
public class TermSourceView extends IdfItemView<TermSource> {

    interface Binder extends UiBinder<Widget, TermSourceView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox nameBox;

    @UiField
    TextBox urlBox;

    @UiField
    TextBox versionBox;

    public TermSourceView() {
        initWidget(Binder.BINDER.createAndBindUi(this));

        addTitleField(hasChangeableValue(nameBox));
    }

    public void update(TermSource ts) {
        setItem(ts);
        nameBox.setValue(ts.getName().getValue());
        urlBox.setValue(ts.getFile().getValue());
        versionBox.setValue(ts.getVersion().getValue());
        fireTitleChangedEvent();
    }
}
