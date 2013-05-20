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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class EfoTerms {

    private enum Term {
        UNIT {
            @Override
            EfoTermDto get(SystemEfoTermsDto systemTerms) {
                return systemTerms.getUnitTerm();
            }
        },
        ORGANISM {
            @Override
            EfoTermDto get(SystemEfoTermsDto systemTerms) {
                return systemTerms.getOrganismTerm();
            }
        },
        ORGANISM_PART {
            @Override
            EfoTermDto get(SystemEfoTermsDto systemTerms) {
                return systemTerms.getOrganismPartTerm();
            }
        },
        STUDY_DESIGN {
            @Override
            EfoTermDto get(SystemEfoTermsDto systemTerms) {
                return systemTerms.getStudyDesignTerm();
            }
        };

        abstract EfoTermDto get(SystemEfoTermsDto systemTerms);
    }

    private final DataServiceAsync dataService;
    private SystemEfoTermsDto systemTerms;

    @Inject
    public EfoTerms(DataServiceAsync dataService) {
        this.dataService = dataService;
    }

    public void getSystemEfoTerms(final AsyncCallback<SystemEfoTermsDto> callback) {
        if (systemTerms != null) {
            callback.onSuccess(systemTerms);
            return;
        }
        dataService.getSystemEfoTerms(new AsyncCallbackWrapper<SystemEfoTermsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SystemEfoTermsDto result) {
                systemTerms = result;
                callback.onSuccess(result);
            }
        }.wrap());
    }

    public void getUnits(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
        getTerms(Term.UNIT, query, limit, callback);
    }

    public void getOrganisms(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
        getTerms(Term.ORGANISM, query, limit, callback);
    }

    public void getOrganismParts(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
        getTerms(Term.ORGANISM_PART, query, limit, callback);
    }

    public void getStudyDesigns(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
        getTerms(Term.STUDY_DESIGN, query, limit, callback);
    }

    public void getEfoTerms(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
        getTerms(query, null, limit, callback);
    }

    public void getEfoTerms(String query, EfoTermDto root, int limit, AsyncCallback<List<EfoTermDto>> callback) {
        getTerms(query, root, limit, callback);
    }

    private void getTerms(final Term term, final String query, final int limit, final AsyncCallback<List<EfoTermDto>> callback) {
        getSystemEfoTerms(new AsyncCallback<SystemEfoTermsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(SystemEfoTermsDto result) {
                getEfoTerms(query, term.get(result), limit, callback);
            }
        });
    }

    private void getTerms(String query, EfoTermDto root, int limit, AsyncCallback<List<EfoTermDto>> callback) {
        if (root == null) {
            dataService.getEfoTerms(query, limit, callback);
        } else {
            dataService.getEfoTerms(query, root.getAccession(), limit, callback);
        }
    }
}
