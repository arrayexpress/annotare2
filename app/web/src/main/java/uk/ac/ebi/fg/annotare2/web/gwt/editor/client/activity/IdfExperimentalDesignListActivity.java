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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.magetab.idf.Term;
import uk.ac.ebi.fg.annotare2.magetab.idf.TermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.IdfData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf.IdfExperimentalDesignListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class IdfExperimentalDesignListActivity extends AbstractActivity
        implements IdfExperimentalDesignListView.Presenter {

    private final IdfExperimentalDesignListView view;

    private final PlaceController placeController;

    private final IdfData idfData;

    private Investigation investigation;

    @Inject
    public IdfExperimentalDesignListActivity(IdfExperimentalDesignListView view,
                                             PlaceController placeController,
                                             IdfData idfData) {
        this.view = view;
        this.placeController = placeController;
        this.idfData = idfData;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
    }

    public IdfExperimentalDesignListActivity withPlace(Place place) {
        return this;
    }

    private void loadAsync() {
        idfData.getInvestigation(new AsyncCallbackWrapper<Investigation>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load Investigation Data.");
            }

            @Override
            public void onSuccess(Investigation inv) {
                if (inv != null) {
                    investigation = inv;
                    view.setExperimentalDesigns(inv.getExperimentalDesigns());
                }
            }
        }.wrap());
    }

    @Override
    public List<UITerm> getExperimentalDesignTemplates() {
        // TODO
        UITermSource ts = new UITermSource("efo", "", "", "aa");
        ArrayList<UITerm> list = new ArrayList<UITerm>();
        list.add(new UITerm("case control design", "", ts, "biological variation design"));
        list.add(new UITerm("all pairs", "", ts, "methodological variation design"));
        list.add(new UITerm("array platform variation design", "", ts, "methodological variation design"));
        return list;
    }

    @Override
    public Term createExperimentalDesign() {
        return investigation.createExperimentalDesign();
    }

    @Override
    public TermSource getOrCreateTermSource(UITermSource template) {
        TermSource ts = investigation.getTermSource(template.getName());
        if (ts == null) {
            ts = investigation.createTermSource();
            ts.getName().setValue(template.getName());
            ts.getVersion().setValue(template.getVersion());
            ts.getFile().setValue(template.getUrl());
        }
        return ts;
    }

    @Override
    public void removeExperimentalDesigns(List<Integer> indices) {
        investigation.removeExperimentalDesigns(indices);
    }

    @Override
    public List<TermSource> getTermSources() {
        return investigation.getTermSources();
    }
}
