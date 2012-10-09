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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.gin.provider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;

/**
 * @author Olga Melnichuk
 */
public class SubmissionServiceProvider implements Provider<SubmissionServiceAsync> {

    private SubmissionServiceAsync service;

    @Override
    public SubmissionServiceAsync get() {
        return service == null ? (service = create()) : service;
    }

    private SubmissionServiceAsync create() {
        SubmissionServiceAsync service = GWT.create(SubmissionService.class);
        String url = GWT.getModuleBaseURL();
        url = url.replace(GWT.getModuleName(), "UserApp");
        ((ServiceDefTarget) service).setServiceEntryPoint(url + SubmissionService.NAME);
        return service;
    }
}
