/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity.ImportSubmissionActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity.SubmissionListActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity.SubmissionViewActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.ImportSubmissionPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionViewPlace;

/**
 * @author Olga Melnichuk
 */
public class ContentActivityMapper implements ActivityMapper {

    private final Provider<SubmissionListActivity> listActivityProvider;
    private final Provider<SubmissionViewActivity> viewActivityProvider;
    private final Provider<ImportSubmissionActivity> importActivityProvider;

    @Inject
    public ContentActivityMapper(Provider<SubmissionListActivity> listActivityProvider,
                                 Provider<SubmissionViewActivity> viewActivityProvider,
                                 Provider<ImportSubmissionActivity> importActivityProvider) {
        this.listActivityProvider = listActivityProvider;
        this.viewActivityProvider = viewActivityProvider;
        this.importActivityProvider = importActivityProvider;
    }
    
    public Activity getActivity(Place place) {
        if (place instanceof SubmissionListPlace) {
            return listActivityProvider.get().withPlace((SubmissionListPlace) place);
        } else if (place instanceof SubmissionViewPlace) {
            return viewActivityProvider.get().withPlace((SubmissionViewPlace) place);
        } else if (place instanceof ImportSubmissionPlace) {
            return importActivityProvider.get().withPlace((ImportSubmissionPlace) place);
        }
        //TODO
        return null;
    }
}
