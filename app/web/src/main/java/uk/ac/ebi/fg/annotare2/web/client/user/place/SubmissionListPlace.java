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

package uk.ac.ebi.fg.annotare2.web.client.user.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListPlace extends Place {

    private String placeName;
    private String filter;
    private String sortorder;
    

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    public static class Tokenizer implements PlaceTokenizer<SubmissionListPlace> {

        private final Provider<SubmissionListPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<SubmissionListPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(SubmissionListPlace place) {
            return place.getPlaceName();
        }

        public SubmissionListPlace getPlace(String token) {
            SubmissionListPlace place = placeProvider.get();
            place.setPlaceName(token);
            return place;
        }
    }
}
