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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;

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

    static Function<User, UserDto> USER_TRANSFORM = new Function<User, UserDto>() {
        public UserDto apply(@Nullable User user) {
            checkNotNull(user);
            return new UserDto(user.getEmail());
        }
    };

    static Function<Submission, SubmissionRow> SUBMISSION_ROW = new Function<Submission, SubmissionRow>() {
        public SubmissionRow apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new SubmissionRow(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission)
            );
        }
    };

    static Function<Submission, SubmissionDetails> SUBMISSION_DETAILS = new Function<Submission, SubmissionDetails>() {
        public SubmissionDetails apply(@Nullable Submission submission) {
            checkNotNull(submission);
            return new SubmissionDetails(
                    submission.getId(),
                    submission.getAccession(),
                    submission.getTitle(),
                    submission.getCreated(),
                    submission.getStatus(),
                    SUBMISSION_TYPE.apply(submission),
                    submission.hasNoData()
            );
        }
    };

    static Function<Submission, SubmissionType> SUBMISSION_TYPE = new Function<Submission, SubmissionType>() {
        public SubmissionType apply(@Nullable Submission submission) {
            checkNotNull(submission);
            if (submission instanceof ExperimentSubmission)
                return SubmissionType.EXPERIMENT;
            else if (submission instanceof ArrayDesignSubmission)
                return SubmissionType.ARRAY_DESIGN;
            throw new IllegalStateException("Submission is of unknown type: " + submission.getClass());
        }
    };

    static ArrayList<SubmissionRow> uiSubmissionRows(List<Submission> submissions) {
        return new ArrayList<SubmissionRow>(filter(
                transform(submissions, SUBMISSION_ROW), Predicates.notNull()));
    }

    static SubmissionDetails uiSubmissionDetails(Submission submission) {
        return SUBMISSION_DETAILS.apply(submission);
    }

    static UserDto uiUser(User user) {
        return USER_TRANSFORM.apply(user);
    }
}
