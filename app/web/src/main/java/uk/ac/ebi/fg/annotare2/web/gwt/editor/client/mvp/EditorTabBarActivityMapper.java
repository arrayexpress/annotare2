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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign.ArrayDesignTabBarActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.ExperimentTabBarActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ArrayDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExperimentPlace;

/**
 * @author Olga Melnichuk
 */
public class EditorTabBarActivityMapper implements ActivityMapper {

    private final Provider<ExperimentTabBarActivity> expActivityProvider;
    private final Provider<ArrayDesignTabBarActivity> adActivityProvider;
    private ExperimentTabBarActivity currExperimentActivity;
    private ArrayDesignTabBarActivity currArrayDesignActivity;

    @Inject
    public EditorTabBarActivityMapper(Provider<ExperimentTabBarActivity> expActivityProvider,
                                      Provider<ArrayDesignTabBarActivity> adActivityProvider) {
        this.expActivityProvider = expActivityProvider;
        this.adActivityProvider = adActivityProvider;
    }

    public Activity getActivity(Place place) {
        if (place instanceof ArrayDesignPlace) {
            return (currArrayDesignActivity == null ?
                    (currArrayDesignActivity = adActivityProvider.get()) : currArrayDesignActivity)
                    .withPlace((ArrayDesignPlace) place);
        }
        return (currExperimentActivity == null ?
                (currExperimentActivity = expActivityProvider.get()) : currExperimentActivity)
                .withPlace((ExperimentPlace)place);
    }
}
