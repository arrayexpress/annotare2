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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class ExperimentPublications {

    private final SubmissionServiceAsync submissionService;
    private final UpdateQueue updateQueue;

    private Map<Integer, PublicationDto> publicationsMap;

    public ExperimentPublications(SubmissionServiceAsync submissionService, UpdateQueue updateQueue) {
        this.submissionService = submissionService;
        this.updateQueue = updateQueue;
    }

    public void getPublicationsAsync(final AsyncCallback<List<PublicationDto>> callback) {
        if (publicationsMap != null) {
            callback.onSuccess(getPublications());
            return;
        }
        submissionService.getPublications(getSubmissionId(), new AsyncCallbackWrapper<List<PublicationDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<PublicationDto> result) {
                publicationsMap = new HashMap<Integer, PublicationDto>();
                for (PublicationDto dto : result) {
                    publicationsMap.put(dto.getId(), dto);
                }
                callback.onSuccess(result);
            }
        }.wrap());
    }

    private List<PublicationDto> getPublications() {
        List<PublicationDto> list = new ArrayList<PublicationDto>();
        list.addAll(publicationsMap.values());
        return list;
    }

    public void update(List<PublicationDto> toBeUpdated) {
        //TODO
    }
}
