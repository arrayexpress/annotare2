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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.model.ExpProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRowsAndColumns;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ApplicationDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.OntologyDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SampleAttributeEfoSuggest;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SamplesView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SamplesActivity extends AbstractActivity implements SamplesView.Presenter {

    private final SamplesView view;
    private final ExperimentDataProxy expDataProxy;
    private final ApplicationDataProxy appDataProxy;

    private final SampleAttributeEfoSuggest efoTerms;

    private HandlerRegistration criticalUpdateHandler;
    private Collection<OntologyTerm> expDesigns = new ArrayList<>();

    private ExpProfileType expProfileType;

    @Inject
    public SamplesActivity(SamplesView view,
                           ExperimentDataProxy expDataProxy,
                           ApplicationDataProxy appDataProxy,
                           OntologyDataProxy efoTerms) {
        this.view = view;
        this.expDataProxy = expDataProxy;
        this.appDataProxy = appDataProxy;
        this.efoTerms = wrapEfoTerms(efoTerms);
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view);
        loadAsync();
        criticalUpdateHandler = eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                loadAsync();
            }
        });
    }

    @Override
    public void onStop() {
        criticalUpdateHandler.removeHandler();
        super.onStop();
    }

    public SamplesActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    @Override
    public SampleAttributeEfoSuggest getEfoTerms() {
        return efoTerms;
    }

    @Override
    public Collection<OntologyTerm> getExperimentDesigns()
    {
        expDataProxy.getDetailsAsync(new AsyncCallbackWrapper<ExperimentDetailsDto>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(ExperimentDetailsDto experimentDetailsDto) {
                expDesigns = experimentDetailsDto.getExperimentalDesigns();
            }
        });
        return expDesigns;
    }

    @Override
    public ExpProfileType getExperimentProfileType()
    {
        expDataProxy.getExperimentProfileTypeAsync(new AsyncCallbackWrapper<ExpProfileType>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(ExpProfileType experimentProfileType) {
                expProfileType = experimentProfileType;
            }
        });
        return expProfileType;
    }

    @Override
    public void updateColumns(List<SampleColumn> newColumns) {
        expDataProxy.updateSampleColumns(newColumns);
    }

    @Override
    public void updateRow(SampleRow row) {
        expDataProxy.updateSampleRow(row);
    }

    @Override
    public void getGeneratedSampleNamesAsync(int numOfSamples, String namingPattern, int startingNumber, AsyncCallback<String> callback) {
        expDataProxy.getSampleNamesPreviewAsync(numOfSamples, namingPattern, startingNumber, callback);
    }

    @Override
    public void createSamples(int numOfSamples, String namingPattern, int startingNumber) {
        expDataProxy.createSamples(numOfSamples, namingPattern, startingNumber);
    }

    @Override
    public void removeSamples(ArrayList<SampleRow> rows) {
        expDataProxy.removeSamples(rows);
    }

    @Override
    public void getMaterialTypesAsync(AsyncCallback<ArrayList<String>> callback) {
        appDataProxy.getMaterialTypesAsync(callback);
    }

    private void loadAsync() {
        expDataProxy.getExperimentProfileTypeAsync(
                new ReportingAsyncCallback<ExpProfileType>(FailureMessage.UNABLE_TO_LOAD_SUBMISSION_TYPE) {
                    @Override
                    public void onSuccess(ExpProfileType result) {
                        view.setExperimentType(result);
                    }
                }
        );
        expDataProxy.getSamplesAsync(
                new ReportingAsyncCallback<SampleRowsAndColumns>(FailureMessage.UNABLE_TO_LOAD_SAMPLES_LIST) {
                    @Override
                    public void onSuccess(SampleRowsAndColumns result) {
                        view.setData(result.getSampleRows(), result.getSampleColumns());
                    }
                }
        );
    }

    private SampleAttributeEfoSuggest wrapEfoTerms(final OntologyDataProxy efoTerms) {
        return new SampleAttributeEfoSuggest() {
            @Override
            public void getUnits(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
                efoTerms.getUnits(query, limit, callback);
            }

            @Override
            public void getTerms(String query, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
                efoTerms.getEfoTerms(query, limit, callback);
            }

            @Override
            public void getTerms(String query, OntologyTerm root, int limit, AsyncCallback<ArrayList<OntologyTerm>> callback) {
                efoTerms.getEfoTerms(query, root, limit, callback);
            }

            @Override
            public void getSystemEfoTerms(AsyncCallback<SystemEfoTermMap> callback) {
                efoTerms.getSystemEfoTerms(callback);
            }

            @Override
            public void getTermByLabel(String label, AsyncCallback<OntologyTerm> callback) {
                efoTerms.getEfoTermByLabel(label, callback);
            }
        };
    }
}
