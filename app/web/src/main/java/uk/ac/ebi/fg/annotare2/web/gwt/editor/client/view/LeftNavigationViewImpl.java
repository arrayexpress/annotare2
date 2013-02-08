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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class LeftNavigationViewImpl extends Composite implements LeftNavigationView {

    private Map<Object, Label> labelMap = new HashMap<Object, Label>();

    private NavigationSection selected;

    private FlowPanel flowPanel;

    private Presenter presenter;

    public LeftNavigationViewImpl() {
        flowPanel = new FlowPanel();
        flowPanel.setStyleName("app-IdfNavigation");
        initWidget(flowPanel);
    }

    @Override
    public void initSections(NavigationSection... sections) {
        for(final NavigationSection s : sections) {
            Label label = new Label(s.getTitle());
            label.setStyleName("app-IdfNavigationItem");
            label.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    onSectionClick(s);
                }
            });
            labelMap.put(s.getId(), label);
            flowPanel.add(label);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void selectSection(NavigationSection section) {
        selectLabel(section);
    }

    private void onSectionClick(NavigationSection s) {
        selectLabel(s);
        if (presenter != null) {
            presenter.goTo(s);
        }
    }

    private void selectLabel(NavigationSection s) {
        if (selected != null) {
            labelMap.get(selected).removeStyleName("selected");
        }
        labelMap.get(s.getId()).addStyleName("selected");
        selected = s;
    }
}
