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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.mvp;

import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.ImportSubmissionPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionViewPlace;

/**
 * @author Olga Melnichuk
 */
public class UserAppPlaceFactory {

    @Inject
    SubmissionViewPlace.Tokenizer sbmViewPlaceTokenizer;

    @Inject
    SubmissionListPlace.Tokenizer sbmListPlaceTokenizer;

    @Inject
    ImportSubmissionPlace.Tokenizer importSbmPlaceTokenizer;

    @Inject
    Provider<SubmissionListPlace> sbmListPlaceProvider;

    public SubmissionViewPlace.Tokenizer getSubmissionViewPlaceTokenizer() {
        return sbmViewPlaceTokenizer;
    }

    public SubmissionListPlace.Tokenizer getSubmissionListPlaceTokenizer() {
        return sbmListPlaceTokenizer;
    }

    public ImportSubmissionPlace.Tokenizer getImportSubmissionPlaceTokenizer() {
        return importSbmPlaceTokenizer;
    }

    public SubmissionListPlace getSubmissionListPlace() {
        return sbmListPlaceProvider.get();
    }
}
