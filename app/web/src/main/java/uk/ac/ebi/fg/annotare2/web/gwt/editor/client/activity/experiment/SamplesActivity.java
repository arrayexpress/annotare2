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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoTermDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SamplesView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EfoSuggestService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SamplesActivity extends AbstractActivity implements SamplesView.Presenter {

    private final SamplesView view;
    private final ExperimentData expData;
    private final DataServiceAsync dataService;

    private final EfoSuggestService efoSuggestService;

    @Inject
    public SamplesActivity(SamplesView view,
                           ExperimentData expData,
                           DataServiceAsync dataService
    ) {
        this.view = view;
        this.expData = expData;
        this.dataService = dataService;
        this.efoSuggestService = new EfoSuggestServiceImpl(dataService);
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view);
        loadSamples();
    }

    public SamplesActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    @Override
    public EfoSuggestService getEfoSuggestService() {
       return efoSuggestService;
    }

    private void loadSamples() {
        expData.getSamplesAsync(new AsyncCallback<List<SampleRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(List<SampleRow> result) {
                //TODO load column list
                view.setData(result, new ArrayList<SampleColumn>());
            }
        });
    }

    private static class EfoSuggestServiceImpl implements EfoSuggestService {

        private SystemEfoTermsDto systemTerms;
        private final DataServiceAsync dataService;

        private EfoSuggestServiceImpl(DataServiceAsync dataService) {
            this.dataService = dataService;
        }

        @Override
        public void getUnits(final String query, final int limit, final AsyncCallback<List<EfoTermDto>> callback) {
            getSystemTerms(new AsyncCallback<SystemEfoTermsDto>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(SystemEfoTermsDto result) {
                    dataService.getEfoTerms(query, result.getUnitTerm().getAccession(), limit, callback);
                }
            });
        }

        @Override
        public void getOrganisms(final String query, final int limit, final AsyncCallback<List<EfoTermDto>> callback) {
            getSystemTerms(new AsyncCallback<SystemEfoTermsDto>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(SystemEfoTermsDto result) {
                    dataService.getEfoTerms(query, result.getOrganismTerm().getAccession(), limit, callback);
                }
            });
        }

        @Override
        public void getOrganismParts(final String query, final int limit, final AsyncCallback<List<EfoTermDto>> callback) {
            getSystemTerms(new AsyncCallback<SystemEfoTermsDto>() {
                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(SystemEfoTermsDto result) {
                    dataService.getEfoTerms(query, result.getOrganismPartTerm().getAccession(), limit, callback);
                }
            });
        }

        @Override
        public void getTerms(String query, int limit, AsyncCallback<List<EfoTermDto>> callback) {
            dataService.getEfoTerms(query, limit, callback);
        }

        @Override
        public void getTerms(String query, String rootAccession, int limit, AsyncCallback<List<EfoTermDto>> callback) {
            dataService.getEfoTerms(query, rootAccession, limit, callback);
        }

        private void getSystemTerms(final AsyncCallback<SystemEfoTermsDto> callback) {
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
    }
}
