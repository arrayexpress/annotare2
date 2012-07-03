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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.common.base.Function;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionStatus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UIUser;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Olga Melnichuk
 */
class DataObjects {

    static Function<User, UIUser> USER_TRANSFORM = new Function<User, UIUser>() {
        public UIUser apply(@Nullable User user) {
            checkNotNull(user);
            return new UIUser(user.getEmail());
        }
    };

    static Function<Submission, UISubmission> SUBMISSION_TRANSFORM = new Function<Submission, UISubmission>() {
        public UISubmission apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new UISubmission(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getDescription(),
                    submission.getCreated(),
                    createUIObject(submission.getStatus())
            );
        }
    };

    static Function<SubmissionStatus, UISubmissionStatus> SUBMISSION_STATUS_TRANSFORM = new Function<SubmissionStatus, UISubmissionStatus>() {
        public UISubmissionStatus apply(@Nullable SubmissionStatus submissionStatus) {
            checkNotNull(submissionStatus);
            return UISubmissionStatus.valueOf(submissionStatus.name());
        }
    };

    static UISubmissionStatus createUIObject(SubmissionStatus submissionStatus) {
        return SUBMISSION_STATUS_TRANSFORM.apply(submissionStatus);
    }

    static UISubmission createUIObject(Submission submission) {
        return SUBMISSION_TRANSFORM.apply(submission);
    }

    static UIUser createUIObject(User user) {
        return USER_TRANSFORM.apply(user);
    }

}
