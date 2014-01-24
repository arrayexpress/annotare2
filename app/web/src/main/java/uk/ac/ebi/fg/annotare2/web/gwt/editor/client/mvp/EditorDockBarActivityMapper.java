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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.DockBarActivity;

/**
 * @author Olga Melnichuk
 */
public class EditorDockBarActivityMapper implements ActivityMapper {

    private final Provider<DockBarActivity> provider;
    private DockBarActivity current;

    @Inject
    public EditorDockBarActivityMapper(Provider<DockBarActivity> provider) {
        this.provider = provider;
    }

    @Override
    public Activity getActivity(Place place) {
        return (current == null ? (current = provider.get()) : current).withPlace(place);
    }
}
