package uk.ac.ebi.fg.annotare2.autosubs;

/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

public class SubsTrackingException extends Exception {
    public static final String INVALID_ID_EXCEPTION = "Invalid Submission Tracking db record id (NULL)";
    public static final String MISSING_RECORD_EXCEPTION = "Submission Tracking db record is missing or has is_deleted = 1";
    public static final String IN_CURATION_ON_RESUBMISSION_EXCEPTION = "Submission Tracking database record has in_curation = 1 on re-submission";
    public static final String CAUGHT_EXCEPTION = "Caught an exception";
    public static final String NOT_IMPLEMENTED_EXCEPTION = "Functionality called is not implemented yet";
    public static final String USER_NOT_CONFIGURED_EXCEPTION = "Submission Tracking user name is not defined in the configuration";
    public static final String UNABLE_TO_OBTAIN_CONNECTION = "Unable to obtain Submission Tracking database connection";

    public SubsTrackingException(String message) {
        super(message);
    }

    public SubsTrackingException(String message, Throwable cause) {
        super(message, cause);
    }
}
