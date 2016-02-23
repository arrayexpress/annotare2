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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.DesignNavigationActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.InfoNavigationActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExpDesignSection;

/**
 * @author Olga Melnichuk
 */
public class EditorLeftNavigationActivityMapper implements ActivityMapper {

    private final Provider<InfoNavigationActivity> infoNavActivityProvider;
    private final Provider<DesignNavigationActivity> designNavActivityProvider;

    @Inject
    public EditorLeftNavigationActivityMapper(
            Provider<InfoNavigationActivity> infoNavActivityProvider,
            Provider<DesignNavigationActivity> designNavActivityProvider) {
        this.infoNavActivityProvider = infoNavActivityProvider;
        this.designNavActivityProvider = designNavActivityProvider;
    }

    public Activity getActivity(Place place) {
        if (place instanceof ExpInfoPlace) {
            return (infoNavActivityProvider.get()).withPlace((ExpInfoPlace) place);

        } else if (place instanceof ExpDesignPlace) {
            ExpDesignSection section = ((ExpDesignPlace) place).getExpDesignSection();
            if (!section.isNone()) {
                return (designNavActivityProvider.get()).withPlace((ExpDesignPlace) place);
            }
        }
        // hide left menu
        return null;
    }
}
