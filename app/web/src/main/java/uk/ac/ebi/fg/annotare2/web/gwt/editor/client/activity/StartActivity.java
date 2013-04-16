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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.StartView;

import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class StartActivity extends AbstractActivity implements StartView.Presenter {

    private final StartView view;
    private final SubmissionServiceAsync submissionService;
    private final DataServiceAsync dataService;

    @Inject
    public StartActivity(StartView view,
                         SubmissionServiceAsync submissionService,
                         DataServiceAsync dataService) {
        this.view = view;
        this.submissionService = submissionService;
        this.dataService = dataService;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
    }

    public StartActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void setupNewSubmission(Map<String, String> properties, AsyncCallback<Void> callback) {
        submissionService.setupExperimentSubmission(getSubmissionId(), properties, callback);
    }

    @Override
    public void getArrayDesigns(AsyncCallback<List<ArrayDesignRef>> callback) {
        dataService.getArrayDesignList(AsyncCallbackWrapper.wrap(callback));
    }
}
