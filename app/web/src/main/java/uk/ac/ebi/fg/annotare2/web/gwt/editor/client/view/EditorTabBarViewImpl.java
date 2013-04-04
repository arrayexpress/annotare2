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

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EditorTabBar;


/**
 * @author Olga Melnichuk
 */
public class EditorTabBarViewImpl extends Composite implements EditorTabBarView {

    private EditorTabBar tabBar;

    private Presenter presenter;

    public EditorTabBarViewImpl() {
        tabBar = new EditorTabBar();
        tabBar.addSelectionHandler(new SelectionHandler<EditorTab>() {
            @Override
            public void onSelection(SelectionEvent<EditorTab> event) {
                onTabSelect(event.getSelectedItem());
            }
        });
        initWidget(tabBar);
    }

    @Override
    public void initWithTabs(EditorTab... tabs) {
       tabBar.addTabs(tabs);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void selectTab(EditorTab tab) {
        tabBar.setSelected(tab, false);
    }

    private void onTabSelect(EditorTab tab) {
        if (presenter != null) {
            presenter.onTabSelect(tab);
        }
    }
}
