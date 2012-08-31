/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources.EditorResources;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactListItem;

/**
 * @author Olga Melnichuk
 */
public class IdfContactListViewImpl extends Composite implements IdfContactListView {

    interface Binder extends UiBinder<Widget, IdfContactListViewImpl> {
    }

    @UiField
    VerticalPanel listPanel;

    public IdfContactListViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        listPanel.add(new ContactListItem());
        listPanel.add(new ContactListItem());
        listPanel.add(new ContactListItem());
        listPanel.add(new ContactListItem());
    }

    @UiFactory
    public EditorResources getResources() {
        EditorResources.INSTANCE.editorStyles().ensureInjected();
        return EditorResources.INSTANCE;
    }

}
