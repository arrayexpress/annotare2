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

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionListService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UISubmission;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionListServiceImpl extends RemoteServiceBase implements SubmissionListService {

    @Inject
    private SubmissionManager manager;

    public ArrayList<UISubmission> getAllSubmissions() {
        return new ArrayList<UISubmission>(
                Lists.transform(manager.getAllSubmissions(getCurrentUser()), DataObjects.SUBMISSION_TRANSFORM)
        );
    }

    public ArrayList<UISubmission> getCompletedSubmissions() {
        return new ArrayList<UISubmission>(
                Lists.transform(manager.getCompletedSubmissions(getCurrentUser()), DataObjects.SUBMISSION_TRANSFORM)
        );
    }

    public ArrayList<UISubmission> getIncompleteSubmissions() {
        return new ArrayList<UISubmission>(
                Lists.transform(manager.getIncompleteSubmissions(getCurrentUser()), DataObjects.SUBMISSION_TRANSFORM)
        );
    }
}
