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
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.gin.annotations.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp.EditorPlaceFactory;

/**
 * @author Olga Melnichuk
 */
@GinModules({EditorGinModule.class})
public interface EditorGinjector extends Ginjector {

    EventBus getEventBus();

    PlaceController getPlaceController();

    EditorPlaceFactory getPlaceFactory();

    @EditorTitleBarDisplay
    ActivityMapper getTitleBarActivityMapper();

    @EditorTabBarDisplay
    ActivityMapper getTabBarActivityMapper();

    @EditorTabToolBarDisplay
    ActivityMapper getTabToolBarActivityMapper();

    @EditorLeftMenuDisplay
    ActivityMapper getLeftMenuActivityMapper();

    @EditorContentDisplay
    ActivityMapper getContentActivityMapper();
}
