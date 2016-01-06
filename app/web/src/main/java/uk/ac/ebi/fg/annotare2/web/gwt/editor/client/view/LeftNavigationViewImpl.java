/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class LeftNavigationViewImpl extends Composite implements LeftNavigationView {

    public interface Resources extends ClientBundle {

        @Source("../../public/LeftNavigationView.css")
        Style style();
    }

    public interface Style extends CssResource {

        String navigationPanel();

        String navigationItem();

        String selectedItem();
    }

    static Resources DEFAULT_RESOURCES;

    private final Style style;
    private final FlowPanel panel;
    private final VerticalPanel verticalPanel;
    private ExperimentSettingsPanel settingsPanel;

    private final Map<String, Integer> indexMap = new HashMap<String, Integer>();
    private Section selected;
    private Presenter presenter;

    public LeftNavigationViewImpl() {
        Resources resources = getDefaultResources();
        style = resources.style();
        style.ensureInjected();

        panel = new FlowPanel();
        panel.setStyleName(style.navigationPanel());

        verticalPanel = new VerticalPanel();
        verticalPanel.setWidth("100%");
        verticalPanel.add(panel);
        initWidget(verticalPanel);
    }

    @Override
    public void setSections(Section... sections) {
        List<Section> list = new ArrayList<Section>();
        Collections.addAll(list, sections);
        setSections(list);
    }

    @Override
    public void setExperimentSettings(ExperimentSettings settings) {
        if (settingsPanel != null) {
            settingsPanel.update(settings);
            return;
        }

        settingsPanel = new ExperimentSettingsPanel(settings);
        settingsPanel.setPresenter(new ExperimentSettingsPanel.Presenter() {
            @Override
            public void getArrayDesigns(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> callback) {
                if (presenter != null) {
                    presenter.getArrayDesigns(query, limit, callback);
                }
            }

            @Override
            public void saveSettings(ExperimentSettings settings) {
                if (presenter != null) {
                    presenter.saveSettings(settings);
                }
            }
        });
        verticalPanel.add(settingsPanel);
    }

    @Override
    public void setSections(List<? extends Section> sections) {
        indexMap.clear();
        for (final Section section : sections) {
            Label label = new Label(section.getTitle());
            label.setStyleName(style.navigationItem());
            label.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    onSectionClick(section);
                }
            });
            indexMap.put(section.getKey(), indexMap.size());
            panel.add(label);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setSelected(Section section) {
        selectLabel(section);
    }

    @Override
    public void setArrayDesignList(List<ArrayDesignRef> arrayDesigns) {
        settingsPanel.setArrayDesignList(arrayDesigns);
    }

    private void onSectionClick(Section section) {
        selectLabel(section);
        if (presenter != null) {
            presenter.navigateTo(section);
        }
    }

    private void selectLabel(Section section) {
        if (selected != null) {
            int index = indexMap.get(selected.getKey());
            panel.getWidget(index).removeStyleName(style.selectedItem());
        }
        int index = indexMap.get(section.getKey());
        panel.getWidget(index).addStyleName(style.selectedItem());
        selected = section;
    }

    private static Resources getDefaultResources() {
        if (DEFAULT_RESOURCES == null) {
            DEFAULT_RESOURCES = GWT.create(Resources.class);
        }
        return DEFAULT_RESOURCES;
    }
}
