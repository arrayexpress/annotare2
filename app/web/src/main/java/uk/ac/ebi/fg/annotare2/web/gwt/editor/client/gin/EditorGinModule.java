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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.gin;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.AdfData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.EfoTerms;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.gin.annotations.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header.AdfDetailsView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header.AdfDetailsViewImpl;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SamplesView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SamplesViewImpl;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.*;

/**
 * @author Olga Melnichuk
 */
public class EditorGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceHistoryMapper.class).to(EditorPlaceHistoryMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(EditorTitleBarDisplay.class).to(EditorTitleBarActivityMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(EditorTabBarDisplay.class).to(EditorTabBarActivityMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(EditorLeftMenuDisplay.class).to(EditorLeftNavigationActivityMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(EditorContentDisplay.class).to(EditorContentActivityMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(EditorLogBarDisplay.class).to(EditorLogBarActivityMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(EditorStartDisplay.class).to(EditorStartActivityMapper.class).in(Singleton.class);

        bind(EditorTitleBarView.class).to(EditorTitleBarViewImpl.class);
        bind(EditorTabBarView.class).to(EditorTabBarViewImpl.class);
        bind(EditorLogBarView.class).to(EditorLogBarViewImpl.class);

        bind(SheetModeView.class).to(SheetModeViewImpl.class);
        bind(LeftNavigationView.class).to(LeftNavigationViewImpl.class);

        bind(IdfTabToolBarView.class).to(IdfTabToolBarViewImpl.class);
        bind(ExpDetailsView.class).to(ExpDetailsViewImpl.class);
        bind(ContactListView.class).to(ContactListViewImpl.class);
        bind(PublicationListView.class).to(PublicationListViewImpl.class);
        bind(IdfTermSourceListView.class).to(ExpInfoTermSourceListViewImpl.class);
        bind(IdfExperimentalDesignListView.class).to(ExpInfoExperimentalDesignListViewImpl.class);

        bind(SamplesView.class).to(SamplesViewImpl.class);

        bind(AdfDetailsView.class).to(AdfDetailsViewImpl.class);

        bind(StartView.class).to(StartViewImpl.class);

        bind(AdfData.class).in(Singleton.class);
        bind(ExperimentData.class).in(Singleton.class);
        bind(EfoTerms.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(EventBus eventBus) {
        return new PlaceController(eventBus);
    }
}
