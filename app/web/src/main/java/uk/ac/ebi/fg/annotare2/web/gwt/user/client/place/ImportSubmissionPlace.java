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

public class ImportSubmissionPlace extends Place {

    private Long submissionId;

    public enum ImportStage {
        FILE_UPLOAD("upload"),
        VALIDATE("validate"),
        SUBMIT("submit");

        private final String token;

        ImportStage(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        private boolean matches(String token) {
            return this.token.equalsIgnoreCase(token);
        }

        public static ImportStage getDefault() {
            return FILE_UPLOAD;
        }

        public static ImportStage getFromToken(String token) {
            if (null == token || token.isEmpty()) {
                return getDefault();
            }
            for (ImportStage f : values()) {
                if (f.matches(token)) {
                    return f;
                }
            }
            return getDefault();
        }
    }

    private ImportStage importStage = ImportStage.getDefault();

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(long submissionId) {
        this.submissionId = submissionId;
    }

    public ImportStage getImportStage() {
        return importStage;
    }

    public void setImportStage(ImportStage importStage) {
        this.importStage = importStage;
    }

    @Override
    public boolean equals(Object place) {
        if (place instanceof ImportSubmissionPlace) {
            ImportSubmissionPlace importSubmissionPlace = (ImportSubmissionPlace)place;
            if ((null == submissionId && null == importSubmissionPlace.submissionId) ||
                    (null != submissionId && submissionId.equals(importSubmissionPlace.submissionId))) {
                return importStage.equals(importSubmissionPlace.importStage);
            }
        }
        return false;
    }

    @Prefix("import")
    public static class Tokenizer implements PlaceTokenizer<ImportSubmissionPlace> {

        private final Provider<ImportSubmissionPlace> placeProvider;

        @Inject
        public Tokenizer(Provider<ImportSubmissionPlace> placeProvider) {
            this.placeProvider = placeProvider;
        }

        public String getToken(ImportSubmissionPlace place) {
            return String.valueOf(place.getSubmissionId()) + ':' + place.getImportStage().getToken();
        }

        public ImportSubmissionPlace getPlace(String token) {
            ImportSubmissionPlace place = placeProvider.get();
            String[] tokens = token.split("[:]");
            place.setSubmissionId(Integer.valueOf(tokens[0]));
            place.setImportStage(ImportStage.getFromToken(tokens[1]));
            return place;
        }
    }
}
