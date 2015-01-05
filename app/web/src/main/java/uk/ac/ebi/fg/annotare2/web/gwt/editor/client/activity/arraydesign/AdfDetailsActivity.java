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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.AdHeaderPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ArrayDesignDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.OntologyDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header.AdfDetailsView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AdfDetailsActivity extends AbstractActivity implements AdfDetailsView.Presenter {

    private AdfDetailsView view;
    private final ArrayDesignDataProxy adfDataProxy;
    private final OntologyDataProxy efoTerms;

    @Inject
    public AdfDetailsActivity(AdfDetailsView view,
                              ArrayDesignDataProxy adfDataProxy,
                              OntologyDataProxy efoTerms) {
        this.view = view;
        this.adfDataProxy = adfDataProxy;
        this.efoTerms = efoTerms;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
    }

    @Override
    public void onStop() {
        adfDataProxy.updateDetails(view.getDetails());
        super.onStop();
    }

    public AdfDetailsActivity withPlace(AdHeaderPlace place) {
        return this;
    }

    @Override
    public void getOrganisms(String query, int limit, AsyncCallback<List<OntologyTerm>> callback) {
        efoTerms.getOrganisms(query, limit, callback);
    }

    @Override
    public void updateDetails(ArrayDesignDetailsDto details) {
        adfDataProxy.updateDetails(details);
    }

    private void loadAsync() {
        adfDataProxy.getDetailsAsync(
                new ReportingAsyncCallback<ArrayDesignDetailsDto>(FailureMessage.UNABLE_TO_LOAD_ADF_DATA) {
                    @Override
                    public void onSuccess(ArrayDesignDetailsDto details) {
                        view.setDetails(details);
                    }
                }
        );
    }
}