/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AdfServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ApplicationDataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SubmissionDetails;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTitleBarView;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper.callbackWrap;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class EditorTitleBarActivity extends AbstractActivity implements EditorTitleBarView.Presenter {

    private final EditorTitleBarView view;
    private final PlaceController placeController;
    private final CurrentUserAccountServiceAsync userService;
    private final SubmissionServiceAsync submissionService;
    private final ApplicationDataServiceAsync dataService;
    private final AdfServiceAsync adfService;

    private EventBus eventBus;

    @Inject
    public EditorTitleBarActivity(EditorTitleBarView view,
                                  PlaceController placeController,
                                  CurrentUserAccountServiceAsync userService,
                                  SubmissionServiceAsync submissionService,
                                  ApplicationDataServiceAsync dataService,
                                  AdfServiceAsync adfService) {
        this.view = view;
        this.placeController = placeController;
        this.userService = userService;
        this.submissionService = submissionService;
        this.dataService = dataService;
        this.adfService = adfService;
    }

    public EditorTitleBarActivity withPlace(Place place) {
        return this;
    }

    @Override
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
                view.autoSaveStopped(event.getErrorMessage());
            }
        });

        eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
                 view.criticalUpdateStarted();
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                 view.criticalUpdateStopped();
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
        userService.me(AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<UserDto>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION) {
                    @Override
                    public void onSuccess(UserDto result) {
                        view.setCurator(result.isCurator());
                    }
                }
        ));
        submissionService.getSubmissionDetails(getSubmissionId(), AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<SubmissionDetails>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION_DETAILS) {
                    @Override
                    public void onSuccess(SubmissionDetails result) {
                        view.setTitle(result.getType(), result.getAccession().getText());
                        view.setSubmissionType(result.getType());
                        view.setSubmissionStatus(result.getStatus());
                        view.setOwnedByCreator(result.isOwnedByCreator());
                    }
                }
        ));
    }

    @Override
    public void assignSubmissionToMe(final AsyncCallback<Void> callback) {
        submissionService.assignSubmissionToMe(getSubmissionId(), callbackWrap(callback));
    }

    @Override
    public void assignSubmissionToCreator(final AsyncCallback<Void> callback) {
        submissionService.assignSubmissionToCreator(getSubmissionId(), callbackWrap(callback));
    }

    @Override
    public void validateSubmission(final EditorTitleBarView.ValidationHandler handler) {
        submissionService.validateSubmission(getSubmissionId(), callbackWrap(
                new ReportingAsyncCallback<ValidationResult>(FailureMessage.GENERIC_FAILURE) {
                    @Override
                    public void onFailure(Throwable caught) {
                        super.onFailure(caught);
                        handler.onFailure();
                        publishValidationResult(new ValidationResult(caught));
                    }

                    @Override
                    public void onSuccess(ValidationResult result) {
                        handler.onSuccess(result);
                        publishValidationResult(result);
                    }
                }
        ));
    }

    @Override
    public void submitSubmission(final EditorTitleBarView.SubmissionHandler handler) {
        submissionService.submitSubmission(getSubmissionId(), callbackWrap(
                new ReportingAsyncCallback<Void>(FailureMessage.GENERIC_FAILURE) {
                    @Override
                    public void onFailure(Throwable caught) {
                        handler.onFailure();
                    }

                    @Override
                    public void onSuccess(Void result) {
                        handler.onSuccess();
                    }
                }
        ));
    }

    private void publishValidationResult(ValidationResult result) {
        eventBus.fireEvent(new ValidationFinishedEvent(result));
    }

    @Override
    public void setupNewSubmission(ExperimentSetupSettings settings, AsyncCallback<Void> callback) {
        submissionService.setupExperiment(getSubmissionId(), settings, callbackWrap(callback));
    }

    @Override
    public void getArrayDesigns(String query, int limit, AsyncCallback<List<ArrayDesignRef>> callback) {
        dataService.getArrayDesignList(query, limit, callbackWrap(callback));
    }

//    @Override
//    public void importFile(AsyncCallback<Void> callback) {
//        adfService.importBodyData(getSubmissionId(), callbackWrap(callback));
//    }

    @Override
    public String getSubmissionExportUrl() {
        return GWT.getModuleBaseURL().replace("/" + GWT.getModuleName(), "") + "export?submissionId=" + getSubmissionId();
    }

    @Override
    public void postFeedback(Byte score, String comment) {
        submissionService.postFeedback(getSubmissionId(), score, comment,
                callbackWrap(
                        new ReportingAsyncCallback<Void>(FailureMessage.GENERIC_FAILURE) {

                            @Override
                            public void onSuccess(Void result) {
                            }
                        }
                )
        );
    }
}
