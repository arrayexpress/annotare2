/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSamples {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private Set<SampleRow> samples;

    public ExperimentSamples(SubmissionServiceAsync submissionService, UpdateQueue updateQueue) {
        this.submissionService = submissionService;
        this.updateQueue = updateQueue;
    }

    public void getSamplesAsync(final AsyncCallback<List<SampleRow>> callback) {
        if (samples != null) {
            callback.onSuccess(new ArrayList<SampleRow>(samples));
            return;
        }
        submissionService.getSamples(getSubmissionId(), new AsyncCallbackWrapper<List<SampleRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<SampleRow> result) {
                samples = new LinkedHashSet<SampleRow>(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }

}
