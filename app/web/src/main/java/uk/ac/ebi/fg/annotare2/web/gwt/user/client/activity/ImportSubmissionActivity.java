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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.ImportSubmissionPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.ImportSubmissionPlace.ImportStage;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.ImportSubmissionView;

public class ImportSubmissionActivity extends AbstractActivity implements ImportSubmissionView.Presenter {

    private final ImportSubmissionView view;
    private final PlaceController placeController;
    private final SubmissionServiceAsync submissionService;

    private Long submissionId;
    private ImportSubmissionPlace.ImportStage importStage;

    @Inject
    public ImportSubmissionActivity(ImportSubmissionView view, PlaceController placeController,
                                  SubmissionServiceAsync submissionService) {
        this.view = view;
        this.placeController = placeController;
        this.submissionService = submissionService;
    }

    public ImportSubmissionActivity withPlace(ImportSubmissionPlace place) {
        submissionId = place.getSubmissionId();
        view.setImportStage(place.getImportStage());
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        view.startImport();
        //loadAsync();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    @Override
    public void onImportStarted() {
    }

    @Override
    public void onImportCancelled() {
        goTo(new SubmissionListPlace());
    }

    @Override
    public void onImportValidate() {
        ImportSubmissionPlace place = new ImportSubmissionPlace();
        place.setSubmissionId(submissionId);
        place.setImportStage(ImportStage.VALIDATE);
        goTo(place);
    }

    @Override
    public void onImportSubmit() {
        ImportSubmissionPlace place = new ImportSubmissionPlace();
        place.setSubmissionId(submissionId);
        place.setImportStage(ImportStage.SUBMIT);
        goTo(place);
    }
    /*
    private void loadAsync() {
        submissionService.getSubmissionDetails(submissionId, AsyncCallbackWrapper.callbackWrap(
                new ReportingAsyncCallback<SubmissionDetails>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_SUBMISSION_DETAILS) {
                    @Override
                    public void onSuccess(SubmissionDetails result) {
                        view.setSubmissionDetails(result);
                    }
                }
        ));
    }
    */
}
