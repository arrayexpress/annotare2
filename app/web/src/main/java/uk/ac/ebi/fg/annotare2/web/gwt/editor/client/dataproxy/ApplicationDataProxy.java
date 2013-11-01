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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.dataproxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ApplicationDataProxy {

    private final DataServiceAsync dataService;

    private ApplicationProperties properties;
    private List<String> aeExperimentTypes;

    @Inject
    public ApplicationDataProxy(DataServiceAsync dataService) {
        this.dataService = dataService;
    }

    public void getApplicationPropertiesAsync(final AsyncCallback<ApplicationProperties> callback) {
        if (properties != null) {
            callback.onSuccess(properties);
            return;
        }

        dataService.getApplicationProperties(new AsyncCallbackWrapper<ApplicationProperties>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ApplicationProperties result) {
                properties = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getAeExperimentTypesAsync(final AsyncCallback<List<String>> callback) {
        if (aeExperimentTypes != null && !aeExperimentTypes.isEmpty()) {
            callback.onSuccess(new ArrayList<String>(aeExperimentTypes));
            return;
        }

        dataService.getAeExperimentTypes(new AsyncCallbackWrapper<List<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<String> result) {
                aeExperimentTypes = new ArrayList<String>(result);
                callback.onSuccess(result);
            }
        });
    }
}
