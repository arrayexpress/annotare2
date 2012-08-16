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
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabBar;

import java.util.ArrayList;


/**
 * @author Olga Melnichuk
 */
public class EditorTabBarViewImpl extends Composite implements EditorTabBarView {

    interface Binder extends UiBinder<HTMLPanel, EditorTabBarViewImpl> {
    }

    @UiField
    TabBar tabBar;

    @UiField
    HeadingElement tabTitle;

    private ArrayList<EditorTabType> tabTypes = new ArrayList<EditorTabType>();

    private Presenter presenter;

    public EditorTabBarViewImpl() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));

        tabTypes.add(EditorTabType.IDF);
        tabTypes.add(EditorTabType.SDRF);

        for(EditorTabType type : tabTypes) {
            tabBar.addTab(type.name());
        }

        tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                onTabSelect(event.getSelectedItem());
            }
        });
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void selectTab(EditorTabType tabType) {
        tabBar.selectTab(indexOf(tabType), false);
    }

    private void onTabSelect(Integer typeIndex) {
        EditorTabType tabType = tabTypes.get(typeIndex);

        if (presenter != null) {
            presenter.onTabSelect(tabType);
        }

        //TODO move this code into separate method
        tabTitle.setInnerText(tabType.getTitle());
    }

    private int indexOf(EditorTabType desiredType) {
        int i=0;
        for(EditorTabType type : tabTypes) {
            if (type.equals(desiredType)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
