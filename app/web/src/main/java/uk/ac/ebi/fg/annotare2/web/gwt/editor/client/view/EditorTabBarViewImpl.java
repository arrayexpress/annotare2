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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.TabItem;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Olga Melnichuk
 */
public class EditorTabBarViewImpl extends Composite implements EditorTabBarView {

   /* interface Binder extends UiBinder<Widget, EditorTabBarViewImpl> {
    }
*/
    private List<EditorTab> editorTabs = new ArrayList<EditorTab>();

    private HorizontalPanel panel = new HorizontalPanel();

    private TabItem selected = null;

    private Presenter presenter;

    public EditorTabBarViewImpl() {
        HorizontalPanel wrap = new HorizontalPanel();
        wrap.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        wrap.setHeight("100%");
        wrap.setWidth("100%");

        SimplePanel simple = new SimplePanel();
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        hPanel.add(panel);
        simple.add(hPanel);
        simple.addStyleName("app-TabBar");

        wrap.add(simple);
        initWidget(wrap);
    }

    @Override
    public void initWithTabs(EditorTab... tabs) {
        for(final EditorTab tab : tabs) {
            editorTabs.add(tab);
            final TabItem tabItem = new TabItem(tab.getTitle());
            tabItem.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    setSelected(tabItem);
                    onTabSelect(tab);
                }
            });
            panel.add(tabItem);
            //tabBar.addItem(new MenuItem(SafeHtmlUtils.fromString(tab.getTitle())));
        }

       /*tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                onTabSelect(event.getSelectedItem());
            }
        });*/
    }

    private void setSelected(TabItem item) {
        if (selected != null) {
            selected.setSelected(false);
        }
        item.setSelected(true);
        selected = item;
    }
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void selectTab(EditorTab tab) {
        setSelected((TabItem)panel.getWidget(indexOf(tab)));
       // tabBar.selectTab(indexOf(tab), false);
    }

    private void onTabSelect(EditorTab tab) {
        //EditorTab tab = editorTabs.get(typeIndex);
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
