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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.user.client.rpc.AsyncCallback;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public interface SampleAttributeEfoSuggest {

    void getUnits(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback);

    void getTerms(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback);

    void getTerms(String query, OntologyTerm root, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback);

    void getTermByLabel(String label, AsyncCallback<OntologyTerm> callback);

    void getSystemEfoTerms(AsyncCallback<SystemEfoTermMap> asyncCallback);
}
