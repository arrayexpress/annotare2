/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.CookiePopupDeatils;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;

import java.util.ArrayList;

public interface ApplicationDataServiceAsync {

    void getSystemEfoTerms(AsyncCallback<SystemEfoTermMap> async);

    void getArrayDesignList(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> async);

    void getEfoTerms(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> async);

    void getEfoTerms(String query, String rootAccession, int limit, AsyncCallback<ArrayList<OntologyTerm>> async);

    void getEfoTermByLabel(String label, AsyncCallback<OntologyTerm> async);

    void getProtocolTypes(ExperimentProfileType type, AsyncCallback<ArrayList<ProtocolType>> async);

    void getApplicationProperties(AsyncCallback<ApplicationProperties> async);

    void getContactRoles(AsyncCallback<ArrayList<OntologyTerm>> async);

    void getExperimentalDesigns(AsyncCallback<ArrayList<OntologyTermGroup>> async);

    void getAeExperimentTypes(ExperimentProfileType type, AsyncCallback<ArrayList<String>> async);

    void getMaterialTypes(AsyncCallback<ArrayList<String>> async);

    void getSequencingHardware(AsyncCallback<ArrayList<String>> async);

    void getCookiePopupDetails(AsyncCallback<CookiePopupDeatils> async);
}
