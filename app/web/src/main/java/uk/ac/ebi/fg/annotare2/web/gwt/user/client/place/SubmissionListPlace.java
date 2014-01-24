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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.SubmissionListFilter;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListPlace extends Place {

    private SubmissionListFilter filter;

    {
        /**
         * When the default constructor is invoked setValue the default value to the filter.
         */
        setFilter(null);
    }

    public SubmissionListFilter getFilter() {
        return filter;
    }

    public void setFilter(SubmissionListFilter filter) {
        setFilter(filter, SubmissionListFilter.ALL_SUBMISSIONS);
    }

    private void setFilter(SubmissionListFilter filter, SubmissionListFilter defaultFilter) {
        this.filter = filter == null ? defaultFilter : filter;
    }

    @Prefix("submList")
    public static class Tokenizer implements PlaceTokenizer<SubmissionListPlace> {

        private final Provider<SubmissionListPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<SubmissionListPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(SubmissionListPlace place) {
            return place.getFilter().name();
        }

        public SubmissionListPlace getPlace(String token) {
            SubmissionListPlace place = placeProvider.get();
            SubmissionListFilter filter = SubmissionListFilter.getIfPresent(token);
            if (filter == null) {
                return null;
            }
            place.setFilter(filter);
            return place;
        }
    }
}
