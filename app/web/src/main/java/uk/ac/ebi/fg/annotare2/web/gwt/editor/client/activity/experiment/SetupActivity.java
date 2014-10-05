/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.StartView;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class SetupActivity extends AbstractActivity implements StartView.Presenter {

    private final StartView view;
    private final SubmissionServiceAsync submissionService;
    private final DataServiceAsync dataService;

    @Inject
    public SetupActivity(StartView view,
                         SubmissionServiceAsync submissionService,
                         DataServiceAsync dataService) {
        this.view = view;
        this.submissionService = submissionService;
        this.dataService = dataService;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        dataService.getArrayDesignList(null, 999999,
                new ReportingAsyncCallback<List<ArrayDesignRef>>(FailureMessage.UNABLE_TO_LOAD_ARRAYS_LIST) {
            @Override
            public void onSuccess(List<ArrayDesignRef> result) {
                view.setArrayDesignList(result);
            }
        });
        containerWidget.setWidget(view.asWidget());
    }

    public SetupActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void setupNewSubmission(ExperimentSetupSettings settings, AsyncCallback<Void> callback) {
        submissionService.setupExperiment(getSubmissionId(), settings, callback);
    }

    @Override
    public void getArrayDesigns(String query, int limit, AsyncCallback<List<ArrayDesignRef>> callback) {
        dataService.getArrayDesignList(query,limit, AsyncCallbackWrapper.callbackWrap(callback));
    }
}
