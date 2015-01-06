package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.submission.model.ImportedExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;

public interface ImportSubmissionServiceAsync {

    void createImportedExperiment(AsyncCallback<Long> async);

    void getExperimentProfile(long id, AsyncCallback<ImportedExperimentProfile> async);

    void updateExperimentProfile(long id, ImportedExperimentProfile profile, AsyncCallback<Void> async);

    void validateSubmission(long id, AsyncCallback<ValidationResult> async);

    void submitSubmission(long id, AsyncCallback<Void> async);

    void deleteSubmission(long id, AsyncCallback<Void> async);

    void postFeedback(long id, Byte score, String comment, AsyncCallback<Void> async);
}
