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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.activity;

import com.google.common.base.Function;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.event.DataFilesUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.proxy.DataFilesProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.HttpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.ImportSubmissionPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.place.SubmissionListPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.ImportSubmissionView;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Collections2.transform;

public class ImportSubmissionActivity extends AbstractActivity implements ImportSubmissionView.Presenter {

    private final ImportSubmissionView view;
    private final PlaceController placeController;
    private final SubmissionServiceAsync submissionService;
    private final DataFilesProxy dataFilesService;

    private HandlerRegistration dataUpdateHandler;
    //private EventBus eventBus;

    private Long submissionId;

    @Inject
    public ImportSubmissionActivity(ImportSubmissionView view,
                                    PlaceController placeController,
                                    SubmissionServiceAsync submissionService,
                                    DataFilesProxy dataFilesService) {
        this.view = view;
        this.placeController = placeController;
        this.submissionService = submissionService;
        this.dataFilesService = dataFilesService;
    }

    public ImportSubmissionActivity withPlace(ImportSubmissionPlace place) {
        submissionId = place.getSubmissionId();
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        //this.eventBus = eventBus;

        dataUpdateHandler = eventBus.addHandler(DataFilesUpdateEvent.getType(), new DataFilesUpdateEventHandler() {
            @Override
            public void onDataFilesUpdate() {
                loadFilesAsync();
            }
        });
        loadFilesAsync();

        view.startImport();
    }

    @Override
    public void onStop() {
        dataUpdateHandler.removeHandler();
        super.onStop();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void loadFilesAsync() {
        dataFilesService.getFiles(
                submissionId,
                new ReportingAsyncCallback<List<DataFileRow>>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_DATA_FILES_LIST) {
                    @Override
                    public void onSuccess(List<DataFileRow> result) {
                        view.setDataFiles(result);

                    }
                }
        );
    }

    @Override
    public void onImportCancelled() {
        goTo(new SubmissionListPlace());
    }

    @Override
    public void onImportValidate(AsyncCallback<ValidationResult> callback) {
        submissionService.validateSubmission(submissionId, AsyncCallbackWrapper.callbackWrap(callback));
    }

    @Override
    public void onImportSubmit(AsyncCallback<Void> callback) {
        submissionService.submitSubmission(submissionId, AsyncCallbackWrapper.callbackWrap(callback));
    }

    @Override
    public void onPostFeedback(Byte score, String comment) {
        submissionService.postFeedback(submissionId, score, comment,
                AsyncCallbackWrapper.callbackWrap(
                        new ReportingAsyncCallback<Void>(FailureMessage.GENERIC_FAILURE) {

                            @Override
                            public void onSuccess(Void result) {
                            }
                        }
                )
        );
    }

    @Override
    public void onFilesUploaded(List<HttpFileInfo> filesInfo, AsyncCallback<Map<Integer, String>> callback) {
        dataFilesService.registerHttpFiles(submissionId, filesInfo, callback);
    }

    @Override
    public void onFtpDataSubmit(List<String> filesInfo, AsyncCallback<String> callback) {
        dataFilesService.registerFtpFiles(submissionId, filesInfo, callback);
    }

    @Override
    public void renameFile(DataFileRow dataFile, String newFileName, AsyncCallback<Void> callback) {
        dataFilesService.renameFile(submissionId, dataFile, newFileName, callback);
    }

    @Override
    public void removeFiles(Set<DataFileRow> dataFiles, AsyncCallback<Void> callback) {
        dataFilesService.deleteFiles(submissionId, new ArrayList<Long>(transform(dataFiles, new Function<DataFileRow, Long>() {
            public Long apply(@Nullable DataFileRow input) {
                return null != input ? input.getId() : null;
            }
        })), callback);
    }
}
