/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.gin;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.proxy.DataFilesProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.gin.annotations.ContentDisplay;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.gin.annotations.HeaderDisplay;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.gin.annotations.LeftMenuDisplay;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.ContentActivityMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.HeaderActivityMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.LeftMenuActivityMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp.UserAppPlaceHistoryMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.resources.ImageResources;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.*;

/**
 * @author Olga Melnichuk
 */
public class UserAppGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(PlaceHistoryMapper.class).to(UserAppPlaceHistoryMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(HeaderDisplay.class).to(HeaderActivityMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(LeftMenuDisplay.class).to(LeftMenuActivityMapper.class).in(Singleton.class);
        bind(ActivityMapper.class).annotatedWith(ContentDisplay.class).to(ContentActivityMapper.class).in(Singleton.class);

        bind(HeaderView.class).to(HeaderViewImpl.class).in(Singleton.class);
        bind(LeftMenuView.class).to(LeftMenuViewImpl.class).in(Singleton.class);
        bind(SubmissionView.class).to(SubmissionViewImpl.class).in(Singleton.class);
        bind(SubmissionListView.class).to(SubmissionListViewImpl.class).in(Singleton.class);
        bind(ImportSubmissionView.class).to(ImportSubmissionViewImpl.class).in(Singleton.class);

        bind(ImageResources.class).in(Singleton.class);
        bind(DataFilesProxy.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(EventBus eventBus) {
        return new PlaceController(eventBus);
    }
}
