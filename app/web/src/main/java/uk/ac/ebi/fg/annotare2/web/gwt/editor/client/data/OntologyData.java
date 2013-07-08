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
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoGraphDto;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTerm.ORGANISM;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTerm.UNIT;

/**
 * @author Olga Melnichuk
 */
public class OntologyData {

    private final DataServiceAsync dataService;
    private SystemEfoTermMap systemTerms;
    private EfoGraphDto protocolTypes;

    @Inject
    public OntologyData(DataServiceAsync dataService) {
        this.dataService = dataService;
    }

    public void getSystemEfoTerms(final AsyncCallback<SystemEfoTermMap> callback) {
        if (systemTerms != null) {
            callback.onSuccess(systemTerms);
            return;
        }
        dataService.getSystemEfoTerms(new AsyncCallbackWrapper<SystemEfoTermMap>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SystemEfoTermMap result) {
                systemTerms = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getUnits(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
        getTerms(new TermSuggest(UNIT), query, limit, callback);
    }

    public void getOrganisms(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
        getTerms(new TermSuggest(ORGANISM), query, limit, callback);
    }

    public void getEfoTerms(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
        getTerms(query, null, limit, callback);
    }

    public void getEfoTerms(String query, OntologyTerm root, int limit, AsyncCallback<List<OntologyTerm>> callback) {
        getTerms(query, root, limit, callback);
    }

    private void getTerms(final TermSuggest term, final String query, final int limit, final AsyncCallback<List<OntologyTerm>> callback) {
        getSystemEfoTerms(new AsyncCallback<SystemEfoTermMap>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SystemEfoTermMap result) {
                getEfoTerms(query, term.get(result), limit, callback);
            }
        });
    }

    private void getTerms(String query, OntologyTerm root, int limit, AsyncCallback<List<OntologyTerm>> callback) {
        if (root == null) {
            dataService.getEfoTerms(query, limit, callback);
        } else {
            dataService.getEfoTerms(query, root.getAccession(), limit, callback);
        }
    }

    public void getProtocolTypes(ExperimentProfileType expType, final AsyncCallback<EfoGraphDto> callback) {
        if (protocolTypes != null) {
            callback.onSuccess(protocolTypes);
            return;
        }
        dataService.getProtocolTypes(expType, new AsyncCallbackWrapper<EfoGraphDto>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(EfoGraphDto result) {
                protocolTypes = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getProtocols(ExperimentProfileType expType, OntologyTerm protocolType, final AsyncCallback<List<OntologyTerm>> callback) {
        dataService.getProtocols(expType, protocolType, new AsyncCallbackWrapper<List<OntologyTerm>>() {
            @Override
            public void onFailure(Throwable caught) {
                 callback.onFailure(caught);
            }

            @Override
            public void onSuccess(List<OntologyTerm> result) {
                callback.onSuccess(result);
            }
        });
    }

    private static class TermSuggest {

        private final SystemEfoTerm term;

        private TermSuggest(SystemEfoTerm term) {
            this.term = term;
        }

        public OntologyTerm get(SystemEfoTermMap map) {
            return map.getEfoTerm(term);
        }
    }
}
