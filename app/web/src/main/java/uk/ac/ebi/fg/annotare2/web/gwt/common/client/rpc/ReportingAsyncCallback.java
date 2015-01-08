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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;

public abstract class ReportingAsyncCallback<T> implements AsyncCallback<T> {

    private final FailureMessage failureMessage;

    public ReportingAsyncCallback() {
        this(null);
    }

    public ReportingAsyncCallback(FailureMessage failureMessage) {
        this.failureMessage = failureMessage;
    }

    @Override
    public void onFailure(Throwable caught) {
        NotificationPopupPanel.failure(
                null != failureMessage ? failureMessage.getMessage() : caught.getMessage(),
                caught);
    }

    public enum FailureMessage {

        GENERIC_FAILURE("Operation failed"),
        UNABLE_TO_CREATE_SUBMISSION("Unable to create a submission"),
        UNABLE_TO_DELETE_FILES("Unable to delete files"),
        UNABLE_TO_DELETE_SUBMISSION("Unable to delete a submission"),
        UNABLE_TO_LOAD_ADF_DATA("Unable to load ADF data"),
        UNABLE_TO_LOAD_AE_EXPERIMENT_TYPES("Unable to experiment type options"),
        UNABLE_TO_LOAD_APP_PROPERTIES("Unable to load application properties"),
        UNABLE_TO_LOAD_ARRAYS_LIST("Unable to load array designs list"),
        UNABLE_TO_LOAD_CONTACT_LIST("Unable to load contacts list"),
        UNABLE_TO_LOAD_DATA_ASSIGNMENT("Unable to load data assignments"),
        UNABLE_TO_LOAD_DATA_FILES_LIST("Unable to load data files list"),
        UNABLE_TO_LOAD_EFO("Unable to load ontology terms"),
        UNABLE_TO_LOAD_EXPERIMENTAL_DESIGNS("Unable to load experimental designs list"),
        UNABLE_TO_LOAD_EXTRACT_ATTRIBUTES("Unable to load extract attributes list"),
        UNABLE_TO_LOAD_LABELED_EXTRACTS("Unable to labeled extracts list"),
        UNABLE_TO_LOAD_PREVIEW_TABLE("Unable to load preview table"),
        UNABLE_TO_LOAD_PROTOCOL_ASSIGNMENTS("Unable to load protocol assignments"),
        UNABLE_TO_LOAD_PROTOCOL_TYPES("Unable to load protocol types"),
        UNABLE_TO_LOAD_PROTOCOLS_LIST("Unable to load protocols list"),
        UNABLE_TO_LOAD_PUBLICATIONS_LIST("Unable to load publications list"),
        UNABLE_TO_LOAD_PUBLICATION_STATUS_LIST("Unable to load publication status options"),
        UNABLE_TO_LOAD_SAMPLES_LIST("Unable to load samples list"),
        UNABLE_TO_LOAD_SUBMISSION("Unable to load a submission"),
        UNABLE_TO_LOAD_SUBMISSION_DETAILS("Unable to load submission details"),
        UNABLE_TO_LOAD_SUBMISSION_TYPE("Unable to load submission type"),
        UNABLE_TO_LOAD_SUBMISSION_SETTINGS("Unable to load submission settings"),
        UNABLE_TO_LOAD_SUBMISSIONS_LIST("Unable to load submissions list"),
        UNABLE_TO_LOAD_UPDATES("Unable to load updates"),
        UNABLE_TO_LOAD_USER_INFORMATION("Unable to load user information"),
        UNABLE_TO_SEND_UPDATES("Unable to send updates"),
        UNABLE_TO_UPLOAD_FILES("Unable to upload files");


        private final String message;

        FailureMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
