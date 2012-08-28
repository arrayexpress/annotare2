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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.magetab.init.GwtMagetab;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.gin.EditorGinjector;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.EditorPlaceFactory;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.EditorPlaceHistoryMapper;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.IdfPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EditorLayout;

/**
 * @author Olga Melnichuk
 */
public class EditorApp implements EntryPoint {

    private final EditorGinjector injector = GWT.create(EditorGinjector.class);

    private EditorLayout appWidget = new EditorLayout();

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    private void loadModule(HasWidgets root) {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                Window.alert("Uncaught Exception: " + e.getMessage());
                e.printStackTrace();
            }
        });

        GwtMagetab.init();

        EventBus eventBus = injector.getEventBus();
        PlaceController placeController = injector.getPlaceController();

        ActivityMapper titleBarActivityMapper = injector.getTitleBarActivityMapper();
        ActivityManager titleBarActivityManager = new ActivityManager(titleBarActivityMapper, eventBus);
        titleBarActivityManager.setDisplay(appWidget.getTitleBarDisplay());

        ActivityMapper tabBarActivityMapper = injector.getTabBarActivityMapper();
        ActivityManager tabBarActivityManager = new ActivityManager(tabBarActivityMapper, eventBus);
        tabBarActivityManager.setDisplay(appWidget.getTabBarDisplay());

        ActivityMapper leftMenuActivityMapper = injector.getLeftMenuActivityMapper();
        ActivityManager leftMenuActivityManager = new ActivityManager(leftMenuActivityMapper, eventBus);
        leftMenuActivityManager.setDisplay(appWidget.getLeftMenuDisplay());

        ActivityMapper contentActivityMapper = injector.getContentActivityMapper();
        ActivityManager contentActivityManager = new ActivityManager(contentActivityMapper, eventBus);
        contentActivityManager.setDisplay(appWidget.getContentDisplay());

        EditorPlaceFactory factory = injector.getPlaceFactory();
        IdfPlace defaultPlace = factory.getIdfPlace();

        EditorPlaceHistoryMapper historyMapper = GWT.create(EditorPlaceHistoryMapper.class);
        historyMapper.setFactory(factory);

        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        root.add(appWidget);

        historyHandler.handleCurrentHistory();
    }
}
