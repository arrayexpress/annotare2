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
import com.google.gwt.user.client.ui.TabBar;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Olga Melnichuk
 */
public class EditorTabBarViewImpl extends Composite implements EditorTabBarView {

    private final TabBar tabBar = new TabBar();

    private List<EditorTab> editorTabs = new ArrayList<EditorTab>();

    private Presenter presenter;

    public EditorTabBarViewImpl() {
        initWidget(tabBar);
    }

    @Override
    public void initWithTabs(EditorTab... tabs) {
        for(EditorTab tab : tabs) {
            editorTabs.add(tab);
            tabBar.addTab(tab.getTitle());
        }

        tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                onTabSelect(event.getSelectedItem());
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void selectTab(EditorTab tab) {
        tabBar.selectTab(indexOf(tab), false);
    }

    private void onTabSelect(Integer typeIndex) {
        EditorTab tab = editorTabs.get(typeIndex);
        if (presenter != null) {
            presenter.onTabSelect(tab);
        }
    }

    private int indexOf(EditorTab target) {
        int i=0;
        for(EditorTab tab : editorTabs) {
            if (tab.isEqualTo(target)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
