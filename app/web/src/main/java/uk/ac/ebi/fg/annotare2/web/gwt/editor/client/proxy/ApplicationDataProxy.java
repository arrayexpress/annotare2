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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ApplicationDataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class ApplicationDataProxy {

    private final ApplicationDataServiceAsync dataService;

    private ApplicationProperties properties;
    private Map<ExperimentProfileType,List<String>> aeExperimentTypes;
    private List<String> materialTypes;
    private List<String> sequencingHardware;

    @Inject
    public ApplicationDataProxy(ApplicationDataServiceAsync dataService) {
        this.dataService = dataService;
        this.aeExperimentTypes = new HashMap<ExperimentProfileType, List<String>>();
    }

    public void getApplicationPropertiesAsync(final AsyncCallback<ApplicationProperties> callback) {
        if (null != properties) {
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

    public void getAeExperimentTypesAsync(final ExperimentProfileType type, final AsyncCallback<List<String>> callback) {
        if (aeExperimentTypes.containsKey(type)) {
            callback.onSuccess(aeExperimentTypes.get(type));
        } else {
            dataService.getAeExperimentTypes(type, new AsyncCallbackWrapper<List<String>>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(List<String> result) {
                    aeExperimentTypes.put(type, result);
                    callback.onSuccess(result);
                }
            }.wrap());
        }
    }

    public void getMaterialTypesAsync(final AsyncCallback<List<String>> callback) {
        if (null != materialTypes && !materialTypes.isEmpty()) {
            callback.onSuccess(new ArrayList<String>(materialTypes));
            return;
        }

        dataService.getMaterialTypes(new AsyncCallbackWrapper<List<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<String> result) {
                materialTypes = new ArrayList<String>(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getSequencingHardwareAsync(final AsyncCallback<List<String>> callback) {
        if (null != sequencingHardware && !sequencingHardware.isEmpty()) {
            callback.onSuccess(new ArrayList<String>(sequencingHardware));
            return;
        }

        dataService.getSequencingHardware(new AsyncCallbackWrapper<List<String>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<String> result) {
                sequencingHardware = new ArrayList<String>(result);
                callback.onSuccess(result);
            }
        }.wrap());
    }
}
