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

/**
 * @author Olga Melnichuk
 */
public class SubmissionViewPlace extends Place {

    private Long submissionId;

    public long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(long submissionId) {
        this.submissionId = submissionId;
    }

    @Prefix("view")
    public static class Tokenizer implements PlaceTokenizer<SubmissionViewPlace> {

        private final Provider<SubmissionViewPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<SubmissionViewPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(SubmissionViewPlace place) {
            return "" + place.getSubmissionId();
        }

        public SubmissionViewPlace getPlace(String token) {
            SubmissionViewPlace place = placeProvider.get();
            place.setSubmissionId(Integer.valueOf(token));
            return place;
        }
    }
}
