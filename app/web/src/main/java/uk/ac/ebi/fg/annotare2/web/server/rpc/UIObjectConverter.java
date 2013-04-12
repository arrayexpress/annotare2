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
import com.google.common.base.Predicates;
import uk.ac.ebi.fg.annotare2.om.ArrayDesignSubmission;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UIUser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.transform;

/**
 * @author Olga Melnichuk
 */
class UIObjectConverter {

    static Function<User, UIUser> USER_TRANSFORM = new Function<User, UIUser>() {
        public UIUser apply(@Nullable User user) {
            checkNotNull(user);
            return new UIUser(user.getEmail());
        }
    };

    static Function<Submission, UISubmissionRow> SUBMISSION_ROW = new Function<Submission, UISubmissionRow>() {
        public UISubmissionRow apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new UISubmissionRow(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission)
            );
        }
    };

    static Function<Submission, UISubmissionDetails> SUBMISSION_DETAILS = new Function<Submission, UISubmissionDetails>() {
        public UISubmissionDetails apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new UISubmissionDetails(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission)
            );
        }
    };

    static Function<Submission, UISubmissionType> SUBMISSION_TYPE = new Function<Submission, UISubmissionType>() {
        public UISubmissionType apply(@Nullable Submission submission) {
            checkNotNull(submission);
            if (submission instanceof ExperimentSubmission)
                return UISubmissionType.EXPERIMENT;
            else if (submission instanceof ArrayDesignSubmission)
                return UISubmissionType.ARRAY_DESIGN;
            throw new IllegalStateException("Submission is of unknown type: " + submission.getClass());
        }
    };

    static ArrayList<UISubmissionRow> uiSubmissionRows(List<Submission> submissions) {
        return new ArrayList<UISubmissionRow>(filter(
                transform(submissions, SUBMISSION_ROW), Predicates.notNull()));
    }

    static UISubmissionDetails uiSubmissionDetails(Submission submission) {
        return SUBMISSION_DETAILS.apply(submission);
    }

    static UIUser uiUser(User user) {
        return USER_TRANSFORM.apply(user);
    }
}
