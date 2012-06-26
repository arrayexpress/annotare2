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
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.om.Submission;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceImpl extends RemoteServiceBase implements SubmissionService {

    @Inject
    private SubmissionManager manager;

    public List<SubmissionDetails> getSubmissions() {
        return new ArrayList<SubmissionDetails>(
                Lists.transform(manager.getSubmissions(getCurrentUser()), new Function<Submission, SubmissionDetails>() {
                    public SubmissionDetails apply(@Nullable Submission submission) {
                        return new SubmissionDetails(
                                submission.getId(),
                                submission.getTitle(),
                                submission.getDescription(),
                                submission.getCreated()
                        );
                    }
                })
        );
    }
}
