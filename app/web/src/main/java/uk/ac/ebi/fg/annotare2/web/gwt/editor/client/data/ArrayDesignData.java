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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateCommand;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.ArrayDesignUpdateResult;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateArrayDesignDetailsCommand;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignData {

    private final SubmissionServiceAsync submissionService;

    private final UpdateQueue<ArrayDesignUpdateCommand, ArrayDesignUpdateResult> updateQueue;

    private ArrayDesignDetailsDto details;

    @Inject
    public ArrayDesignData(
            EventBus eventBus,
            SubmissionServiceAsync submissionServiceAsync) {
        submissionService = submissionServiceAsync;

        updateQueue = new UpdateQueue<ArrayDesignUpdateCommand, ArrayDesignUpdateResult>(eventBus,
                new UpdateQueue.Transport<ArrayDesignUpdateCommand, ArrayDesignUpdateResult>() {
                    @Override
                    public void sendUpdates(List<ArrayDesignUpdateCommand> commands, final AsyncCallback<ArrayDesignUpdateResult> callback) {
                        submissionService.updateArrayDesign(getSubmissionId(), commands, new AsyncCallbackWrapper<ArrayDesignUpdateResult>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }

                            @Override
                            public void onSuccess(ArrayDesignUpdateResult result) {
                                applyUpdates(result);
                                callback.onSuccess(result);
                            }
                        }.wrap());
                    }
                });
    }

    public void getDetailsAsync(final AsyncCallback<ArrayDesignDetailsDto> callback) {
        if (details != null) {
            callback.onSuccess(details);
            return;
        }
        submissionService.getArrayDesignDetails(getSubmissionId(), new AsyncCallbackWrapper<ArrayDesignDetailsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ArrayDesignDetailsDto result) {
                details = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void updateDetails(ArrayDesignDetailsDto details) {
        updateQueue.add(new UpdateArrayDesignDetailsCommand(details));
    }

    private void applyUpdates(ArrayDesignUpdateResult result) {
       details = result.getUpdatedDetails();
    }

}
