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

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionListService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmissionRow;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListServiceImpl extends AuthBasedRemoteService implements SubmissionListService {
    private final SubmissionManager submissionManager;

    @Inject
    public SubmissionListServiceImpl(AuthService authService,
                                     SubmissionManager submissionManager) {
        super(authService);
        this.submissionManager = submissionManager;
    }

    public ArrayList<UISubmissionRow> getAllSubmissions() {
        return DataObjects.uiSubmissionRows(getMyAllSubmissions());
    }

    public ArrayList<UISubmissionRow> getCompletedSubmissions() {
        return DataObjects.uiSubmissionRows(getMyCompletedSubmissions());
    }


    public ArrayList<UISubmissionRow> getIncompleteSubmissions() {
        return DataObjects.uiSubmissionRows(getMyIncompleteSubmissions());
    }

    private List<Submission> getMyAllSubmissions() {
        List<Submission> list = newArrayList();
        list.addAll(submissionManager.getAllSubmissions(getCurrentUser()));
        return list;
    }

    private List<Submission> getMyCompletedSubmissions() {
        List<Submission> list = newArrayList();
        list.addAll(submissionManager.getCompletedSubmissions(getCurrentUser()));
        return list;
    }

    private List<Submission> getMyIncompleteSubmissions() {
        List<Submission> list = newArrayList();
        list.addAll(submissionManager.getIncompleteSubmissions(getCurrentUser()));
        return list;
    }
}
