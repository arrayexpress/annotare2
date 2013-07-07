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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.EfoGraphDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.OntologyData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ProtocolsView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ProtocolsActivity extends AbstractActivity implements ProtocolsView.Presenter{

    private final ProtocolsView view;
    private final OntologyData ontologyData;

    @Inject
    public ProtocolsActivity(ProtocolsView view, OntologyData ontologyData) {
        this.view = view;
        this.ontologyData = ontologyData;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        view.setData(new ArrayList<ProtocolRow>());
        panel.setWidget(view);
    }


    public ProtocolsActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    @Override
    public void getProtocolTypes(AsyncCallback<EfoGraphDto> callback) {
        ontologyData.getProtocolTypes(callback);
    }

    @Override
    public void getProtocols(OntologyTerm protocolType, AsyncCallback<List<OntologyTerm>> callback) {
        ontologyData.getProtocols(protocolType, callback);
    }
}
