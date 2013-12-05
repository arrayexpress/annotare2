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
import uk.ac.ebi.fg.annotare2.db.model.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionListService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionRow;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListServiceImpl extends AuthBasedRemoteService implements SubmissionListService {

    private final SubmissionManager submissionManager;

    @Inject
    public SubmissionListServiceImpl(AccountService accountService, SubmissionManager submissionManager) {
        super(accountService);
        this.submissionManager = submissionManager;
    }

    @Transactional
    @Override
    public ArrayList<SubmissionRow> getAllSubmissions() {
        return UIObjectConverter.uiSubmissionRows(getMyAllSubmissions());
    }

    @Transactional
    @Override
    public ArrayList<SubmissionRow> getCompletedSubmissions() {
        return UIObjectConverter.uiSubmissionRows(getMyCompletedSubmissions());
    }

    @Transactional
    @Override
    public ArrayList<SubmissionRow> getIncompleteSubmissions() {
        return UIObjectConverter.uiSubmissionRows(getMyIncompleteSubmissions());
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
