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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabBar;


/**
 * @author Olga Melnichuk
 */
public class EditorTabHeaderViewImpl extends Composite implements EditorTabHeaderView {

    interface Binder extends UiBinder<HTMLPanel, EditorTabHeaderViewImpl> {
    }

    @UiField
    TabBar tabBar;

    public EditorTabHeaderViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        tabBar.addTab("IDF");
        tabBar.addTab("SDRF");
        tabBar.addTab("DATA");
        tabBar.addTab("ADF");
        tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> integerSelectionEvent) {
               // Window.alert("Tab " + integerSelectionEvent.getSelectedItem() + " selected");
            }
        });
    }

}
