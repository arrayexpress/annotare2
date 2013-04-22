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
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentData {

    private final SubmissionServiceAsync submissionService;

    private Map<Integer, SampleRow> sampleMap;

    private List<Integer> samples;

    private ExperimentSettings settings;

    @Inject
    public ExperimentData(SubmissionServiceAsync submissionService) {
        this.submissionService = submissionService;
    }

    public void getSettingsAsync(final AsyncCallback<ExperimentSettings> callback) {
        if (settings != null) {
            callback.onSuccess(settings);
            return;
        }
        submissionService.getExperimentSettings(getSubmissionId(), new AsyncCallbackWrapper<ExperimentSettings>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ExperimentSettings result) {
                settings = result;
                callback.onSuccess(result);
            }
        });
    }


    public void getSamplesAsync(final AsyncCallback<List<SampleRow>> callback) {
        if (samples != null) {
            callback.onSuccess(getSamples());
            return;
        }
        submissionService.getSamples(getSubmissionId(), new AsyncCallbackWrapper<List<SampleRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<SampleRow> result) {
                setSamples(result);
                callback.onSuccess(result);
            }
        });
    }

    private List<SampleRow> getSamples() {
        List<SampleRow> rows = new ArrayList<SampleRow>();
        for (Integer id : samples) {
            rows.add(sampleMap.get(id));
        }
        return rows;
    }

    private void setSamples(List<SampleRow> rows) {
        sampleMap = new HashMap<Integer, SampleRow>();
        samples = new ArrayList<Integer>();
        for(SampleRow row :  rows) {
            sampleMap.put(row.getId(), row);
            samples.add(row.getId());
        }
    }
}
