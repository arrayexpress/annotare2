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
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.magetab.parser.IdfParser;
import uk.ac.ebi.fg.annotare2.magetab.parser.MageTabParseException;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.om.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.om.User;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionStatus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UIUser;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.transform;

/**
 * @author Olga Melnichuk
 */
class DataObjects {

    private static final Logger log = LoggerFactory.getLogger(DataObjects.class);

    static Function<User, UIUser> USER_TRANSFORM = new Function<User, UIUser>() {
        public UIUser apply(@Nullable User user) {
            checkNotNull(user);
            return new UIUser(user.getEmail());
        }
    };

    static Function<Submission, UISubmissionRow> SUBMISSION_ROW = new Function<Submission, UISubmissionRow>() {
        public UISubmissionRow apply(@Nullable Submission submission) {
            checkNotNull(submission);
            try {
                Investigation investigation = getInvestigation(submission);
                return new UISubmissionRow(
                        submission.getId(),
                        investigation.getAccession().getAccession(),
                        investigation.getTitle(),
                        submission.getCreated(),
                        uiSubmissionStatus(submission.getStatus())
                );
            } catch (IOException e) {
                log.error("Can't parse investigation data from a submissionId (id=" + submission.getId() + ")", e);
            } catch (MageTabParseException e) {
                log.error("Can't parse investigation data from a submissionId (id=" + submission.getId() + ")", e);
            }
            return null;
        }
    };

    static Function<Submission, UISubmissionDetails> SUBMISSION_DETAILS = new Function<Submission, UISubmissionDetails>() {
        public UISubmissionDetails apply(@Nullable Submission submission) {
            checkNotNull(submission);
            try {
                Investigation investigation = getInvestigation(submission);
                return new UISubmissionDetails(
                        submission.getId(),
                        investigation.getAccession().getAccession(),
                        investigation.getTitle(),
                        submission.getCreated(),
                        uiSubmissionStatus(submission.getStatus())
                );
            } catch (IOException e) {
                log.error("Can't parse investigation data from a submissionId (id=" + submission.getId() + ")", e);
            } catch (MageTabParseException e) {
                log.error("Can't parse investigation data from a submissionId (id=" + submission.getId() + ")", e);
            }
            return null;
        }
    };

    private static Investigation getInvestigation(Submission submission) throws IOException, MageTabParseException {
        return new IdfParser().parse(submission.getInvestigation());
    }

    static Function<SubmissionStatus, UISubmissionStatus> SUBMISSION_STATUS = new Function<SubmissionStatus, UISubmissionStatus>() {
        public UISubmissionStatus apply(@Nullable SubmissionStatus submissionStatus) {
            checkNotNull(submissionStatus);
            return UISubmissionStatus.valueOf(submissionStatus.name());
        }
    };

    static UISubmissionStatus uiSubmissionStatus(SubmissionStatus submissionStatus) {
        return SUBMISSION_STATUS.apply(submissionStatus);
    }

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
