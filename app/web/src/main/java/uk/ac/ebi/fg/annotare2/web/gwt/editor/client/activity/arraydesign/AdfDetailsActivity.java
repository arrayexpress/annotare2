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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.PrintingProtocolDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ArrayDesignData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.EfoTermData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.AdHeaderPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header.AdfDetailsView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AdfDetailsActivity extends AbstractActivity implements AdfDetailsView.Presenter {

    private AdfDetailsView view;
    private final ArrayDesignData adfData;
    private final EfoTermData efoTerms;

    @Inject
    public AdfDetailsActivity(AdfDetailsView view,
                              ArrayDesignData adfData,
                              EfoTermData efoTerms) {
        this.view = view;
        this.adfData = adfData;
        this.efoTerms = efoTerms;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        // TODO load from database
        List<PrintingProtocolDto> protocols = new ArrayList<PrintingProtocolDto>();
        protocols.add(new PrintingProtocolDto(1, "protocol-1", "protocol description-1"));
        protocols.add(new PrintingProtocolDto(2, "protocol-2", "<em>protocol</em> description-2 looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"));
        view.setPrintingProtocols(protocols);

        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
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
        adfData.updateDetails(details);
    }

    private void loadAsync() {
        adfData.getDetailsAsync(new AsyncCallback<ArrayDesignDetailsDto>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load ADF Data.");
            }

            @Override
            public void onSuccess(ArrayDesignDetailsDto details) {
                view.setDetails(details);
            }
        });
    }
}