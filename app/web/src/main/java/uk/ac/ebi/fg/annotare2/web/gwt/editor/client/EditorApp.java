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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.gin.EditorAppGinjector;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.EditorAppPlaceFactory;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.EditorAppPlaceHistoryMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.IdfPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EditorAppLayout;

/**
 * @author Olga Melnichuk
 */
public class EditorApp implements EntryPoint {

    private final EditorAppGinjector injector = GWT.create(EditorAppGinjector.class);

    private EditorAppLayout appWidget = new EditorAppLayout();

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    private void loadModule(HasWidgets root) {
        EventBus eventBus = injector.getEventBus();
        PlaceController placeController = injector.getPlaceController();

        ActivityMapper headerActivityMapper = injector.getHeaderActivityMapper();
        ActivityManager headerActivityManager = new ActivityManager(headerActivityMapper, eventBus);
        headerActivityManager.setDisplay(appWidget.getHeaderDisplay());

        ActivityMapper tabHeaderActivityMapper = injector.getTabHeaderActivityMapper();
        ActivityManager tabHeaderActivityManager = new ActivityManager(tabHeaderActivityMapper, eventBus);
        tabHeaderActivityManager.setDisplay(appWidget.getTabHeaderDisplay());

        EditorAppPlaceFactory factory = injector.getPlaceFactory();
        IdfPlace defaultPlace = factory.getIdfPlace();

        EditorAppPlaceHistoryMapper historyMapper = GWT.create(EditorAppPlaceHistoryMapper.class);
        historyMapper.setFactory(factory);

        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        root.add(appWidget);

        historyHandler.handleCurrentHistory();
    }
}
