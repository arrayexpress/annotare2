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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionValidationServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.AutoSaveEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.AutoSaveEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ValidationFinishedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTitleBarView;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper.wrap;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class EditorTitleBarActivity extends AbstractActivity implements EditorTitleBarView.Presenter {

    private final EditorTitleBarView view;
    private final PlaceController placeController;
    private final SubmissionServiceAsync submissionService;
    private final SubmissionValidationServiceAsync validationService;
    private final DataServiceAsync dataServiceAsync;

    private EventBus eventBus;

    @Inject
    public EditorTitleBarActivity(EditorTitleBarView view,
                                  PlaceController placeController,
                                  SubmissionServiceAsync submissionService,
                                  SubmissionValidationServiceAsync validationService,
                                  DataServiceAsync dataServiceAsync) {
        this.view = view;
        this.placeController = placeController;
        this.submissionService = submissionService;
        this.validationService = validationService;
        this.dataServiceAsync = dataServiceAsync;
    }

    public EditorTitleBarActivity withPlace(Place place) {
        return this;
    }

    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.addHandler(AutoSaveEvent.getType(), new AutoSaveEventHandler() {
            @Override
            public void autoSaveStarted(AutoSaveEvent event) {
                view.autoSaveStarted();
            }

            @Override
            public void autoSaveStopped(AutoSaveEvent event) {
                view.autoSaveStopped(null);
            }

            @Override
            public void autoSaveFailed(AutoSaveEvent event) {
                view.autoSaveStopped(event.getCaught());
            }
        });
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        initAsync();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void initAsync() {
        submissionService.getSubmission(getSubmissionId(), new AsyncCallbackWrapper<SubmissionDetails>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO add proper logging
                Window.alert("Can't load submission details ");
            }

            @Override
            public void onSuccess(SubmissionDetails result) {
                view.setTitle(result.getType(), result.getAccession().getText());
                view.setSubmissionType(result.getType());
            }
        }.wrap());
    }

    @Override
    public void validateSubmission(final EditorTitleBarView.ValidationHandler handler) {
       validationService.validate(getSubmissionId(), new AsyncCallbackWrapper<ValidationResult>() {
           @Override
           public void onFailure(Throwable throwable) {
               handler.onValidationFinished();
               publishValidationResult(new ValidationResult(throwable));
               //TODO log exception here
           }

           @Override
           public void onSuccess(ValidationResult result) {
               handler.onValidationFinished();
               publishValidationResult(result);
           }
       }.wrap());
    }

    private void publishValidationResult(ValidationResult result) {
        eventBus.fireEvent(new ValidationFinishedEvent(result));
    }

    @Override
    public void discardSubmissionData(AsyncCallback<Void> callback) {
        submissionService.discardSubmissionData(getSubmissionId(), wrap(callback));
    }

    @Override
    public void setupNewSubmission(ExperimentSetupSettings settings, AsyncCallback<Void> callback) {
        submissionService.setupExperimentSubmission(getSubmissionId(), settings, wrap(callback));
    }

    @Override
    public void getArrayDesigns(String query, int limit, AsyncCallback<List<ArrayDesignRef>> callback) {
        dataServiceAsync.getArrayDesignList(query, limit, wrap(callback));
    }
}
