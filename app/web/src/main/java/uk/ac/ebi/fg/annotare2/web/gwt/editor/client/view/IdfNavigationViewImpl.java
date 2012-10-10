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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf.IdfSection;

import java.util.HashMap;

/**
 * @author Olga Melnichuk
 */
public class IdfNavigationViewImpl extends Composite implements IdfNavigationView {

    private Presenter presenter;

    private HashMap<IdfSection, Label> labelMap = new HashMap<IdfSection, Label>();

    private IdfSection currentSection;

    public IdfNavigationViewImpl() {
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.setStyleName("app-IdfNavigation");

        for(final IdfSection s : IdfSection.values()) {
            Label label = new Label(s.getTitle());
            label.setStyleName("app-IdfNavigationItem");
            label.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    onIdfSectionClick(s);
                }
            });
            labelMap.put(s, label);
            flowPanel.add(label);
        }

        initWidget(flowPanel);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setIdfSection(IdfSection section) {
        selectNavigationItem(section);
    }

    private void onIdfSectionClick(IdfSection s) {
        selectNavigationItem(s);
        if (presenter != null) {
            presenter.goTo(s);
        }
    }

    private void selectNavigationItem(IdfSection s) {
        if (currentSection != null) {
            labelMap.get(currentSection).removeStyleName("selected");
        }
        labelMap.get(s).addStyleName("selected");
        currentSection = s;
    }
}
