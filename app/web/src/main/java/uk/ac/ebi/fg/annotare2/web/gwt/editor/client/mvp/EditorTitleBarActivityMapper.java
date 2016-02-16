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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.EditorTitleBarActivity;

/**
 * @author Olga Melnichuk
 */
public class EditorTitleBarActivityMapper implements ActivityMapper {

    private final Provider<EditorTitleBarActivity> activityProvider;
    private EditorTitleBarActivity currentActivity;

    @Inject
    public EditorTitleBarActivityMapper(Provider<EditorTitleBarActivity> activityProvider) {
        this.activityProvider = activityProvider;
    }

    public Activity getActivity(Place place) {
        return (currentActivity == null ? (currentActivity = activityProvider.get()) : currentActivity)
                .withPlace(place);
    }
}
