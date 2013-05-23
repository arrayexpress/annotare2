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
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.AdfHeader;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UIPrintingProtocol;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.AdfData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.AdHeaderPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header.AdfDetailsView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class AdfDetailsActivity extends AbstractActivity {

    private AdfDetailsView view;

    private final AdfData adfData;

    private AdfHeader adfHeader;

    @Inject
    public AdfDetailsActivity(AdfDetailsView view,
                              AdfData adfData) {
        this.view = view;
        this.adfData = adfData;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        // TODO load from database
        List<UIPrintingProtocol> protocols = new ArrayList<UIPrintingProtocol>();
        protocols.add(new UIPrintingProtocol("protocol-1", "protocol description-1"));
        protocols.add(new UIPrintingProtocol("protocol-2", "<em>protocol</em> description-2 looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"));

        //TODO load from an ontotlogy
        List<String> species = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            species.add("specie-" + i);
        }

        view.setSpecies(species);
        view.setPrintingProtocols(protocols);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
    }

    public AdfDetailsActivity withPlace(AdHeaderPlace place) {
        return this;
    }

    private void loadAsync() {
        adfData.getHeader(new AsyncCallbackWrapper<AdfHeader>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load ADF Data.");
            }

            @Override
            public void onSuccess(AdfHeader header) {
                if (header != null) {
                    adfHeader = header;
                    view.setArrayDesignName(header.getArrayDesignName());
                    view.setVersion(header.getVersion());
                    view.setDescription(header.getDescription(true));
                    view.setReleaseDate(header.getArrayExpressReleaseDate(true));
                    view.setOrganism(header.getOrganism(true));
                    view.setPrintingProtocol(header.getPrintingProtocol());
                }
            }
        }.wrap());
    }


}
