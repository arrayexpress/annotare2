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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.core.data.SystemEfoTerm;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ApplicationDataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolType;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.core.data.SystemEfoTerm.*;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper.callbackWrap;

/**
 * @author Olga Melnichuk
 */
public class OntologyDataProxy {

    private final ApplicationDataServiceAsync dataService;
    private SystemEfoTermMap systemTerms;
    private List<OntologyTerm> contactRoles;
    private List<OntologyTerm> publicationStatuses;
    private List<OntologyTermGroup> experimentalDesigns;

    @Inject
    public OntologyDataProxy(ApplicationDataServiceAsync dataService) {
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

    public void getPublicationStatuses(final AsyncCallback<ArrayList<OntologyTerm>> callback) {
        if (publicationStatuses != null && !publicationStatuses.isEmpty()) {
            callback.onSuccess(new ArrayList<>(publicationStatuses));
            return;
        }
        getTerms(new TermSuggest(PUBLICATION_STATUS), "", 20, new AsyncCallbackWrapper<ArrayList<OntologyTerm>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ArrayList<OntologyTerm> result) {
                publicationStatuses = new ArrayList<OntologyTerm>(result);
                callback.onSuccess(result);
            }
        });
    }

    public void getUnits(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
        getTerms(new TermSuggest(UNIT), query, limit, callback);
    }

    public void getOrganisms(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
        getTerms(new TermSuggest(ORGANISM), query, limit, callback);
    }

    public void getEfoTerms(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
        getTerms(query, null, limit, callback);
    }

    public void getEfoTerms(String query, OntologyTerm root, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
        getTerms(query, root, limit, callback);
    }

    public void getEfoTermByLabel(String label, AsyncCallback<OntologyTerm> callback) {
        dataService.getEfoTermByLabel(label, callback);
    }

    private void getTerms(final TermSuggest term, final String query, final int limit, final AsyncCallback<ArrayList<OntologyTerm>> callback) {
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

    private void getTerms(String query, OntologyTerm root, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
        if (root == null) {
            dataService.getEfoTerms(query, limit, callback);
        } else {
            dataService.getEfoTerms(query, root.getAccession(), limit, callback);
        }
    }

    public void getProtocolTypes(ExperimentProfileType expType, final AsyncCallback<ArrayList<ProtocolType>> callback) {
        dataService.getProtocolTypes(expType, callbackWrap(callback));
    }

    public void getContactRoles(final AsyncCallback<List<OntologyTerm>> callback) {
        if (contactRoles != null && !contactRoles.isEmpty()) {
            callback.onSuccess(new ArrayList<OntologyTerm>(contactRoles));
            return;
        }
        dataService.getContactRoles(new AsyncCallbackWrapper<ArrayList<OntologyTerm>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ArrayList<OntologyTerm> result) {
                contactRoles = new ArrayList<OntologyTerm>(result);
                callback.onSuccess(result);
            }
        });
    }

    public void getExperimentalDesigns(final AsyncCallback<List<OntologyTermGroup>> callback) {
        if (experimentalDesigns != null && !experimentalDesigns.isEmpty()) {
            callback.onSuccess(new ArrayList<OntologyTermGroup>(experimentalDesigns));
            return;
        }
        dataService.getExperimentalDesigns(new AsyncCallbackWrapper<ArrayList<OntologyTermGroup>>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(ArrayList<OntologyTermGroup> result) {
                experimentalDesigns = new ArrayList<OntologyTermGroup>(result);
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
