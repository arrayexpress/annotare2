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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class EditorTabBar extends Composite implements IsWidget, HasSelectionHandlers<EditorTab> {

    private HorizontalPanel panel;

    private List<EditorTab> tabs = new ArrayList<EditorTab>();

    private int selected = -1;

    public EditorTabBar() {
        HorizontalPanel wrapper = new HorizontalPanel();
        wrapper.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        wrapper.setHeight("100%");
        wrapper.setWidth("100%");

        panel = new HorizontalPanel();
        SimplePanel simple = new SimplePanel();
        simple.add(panel);
        simple.addStyleName("app-TabBar");

        wrapper.add(simple);
        initWidget(wrapper);
    }

    public void addTabs(EditorTab... tabs) {
        for (final EditorTab tab : tabs) {
            this.tabs.add(tab);
            final int index = this.tabs.size() - 1;
            Label tabItem = new Label(tab.getTitle());
            tabItem.addStyleName("app-TabItem");
            tabItem.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectTab(index, true);
                }
            });
            panel.add(tabItem);
        }
    }

    public void selectTab(EditorTab tab, boolean fireEvents) {
        selectTab(indexOf(tab), fireEvents);
    }

    private void selectTab(int index, boolean fireEvents) {
        if (selected == index) {
            return;
        }
        selectTab(index);
        if (fireEvents) {
            SelectionEvent.fire(this, tabs.get(index));
        }
    }

    private void selectTab(int index) {
        if (selected >= 0) {
            setSelectedStyle(panel.getWidget(selected), false);
        }
        setSelectedStyle(panel.getWidget(index), true);
        selected = index;
    }

    public void setSelectedStyle(Widget widget, boolean selected) {
        if (selected) {
            widget.addStyleName("selected");
        } else {
            widget.removeStyleName("selected");
        }
    }

    private int indexOf(EditorTab target) {
        int i = 0;
        for (EditorTab tab : tabs) {
            if (tab.isEqualTo(target)) {
                return i;
            }
            i++;
        }
        throw new IllegalArgumentException("No such tab: " + target);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<EditorTab> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }
}
