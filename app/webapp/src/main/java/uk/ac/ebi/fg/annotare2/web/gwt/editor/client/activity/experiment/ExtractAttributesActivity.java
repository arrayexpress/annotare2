/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExtractAttributesRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExtractAttributesView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ExtractAttributesActivity extends AbstractActivity implements ExtractAttributesView.Presenter {

    private final ExtractAttributesView view;
    private final ExperimentDataProxy expData;

    @Inject
    public ExtractAttributesActivity(ExtractAttributesView view,
                                     ExperimentDataProxy expData) {
        this.view = view;
        this.expData = expData;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
        loadAsync();
    }

    public ExtractAttributesActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    @Override
    public void updateRow(ExtractAttributesRow row) {
        expData.updateExtractAttributeRow(row);
    }

    private void loadAsync() {
        expData.getDetailsAsync(
                new ReportingAsyncCallback<ExperimentDetailsDto>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION_DETAILS) {
                    @Override
                    public void onSuccess(ExperimentDetailsDto result) {
                        view.setAeExperimentType(result.getAeExperimentType());
                    }
                }
        );

        expData.getExtractAttributeRowsAsync(
                new ReportingAsyncCallback<List<ExtractAttributesRow>>(FailureMessage.UNABLE_TO_LOAD_EXTRACT_ATTRIBUTES) {
                    @Override
                    public void onSuccess(List<ExtractAttributesRow> result) {
                       view.setData(result);
                    }
                }
        );
    }
}
