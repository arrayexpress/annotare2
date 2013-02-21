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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.AdfData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.AdHeaderPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header.AdfGeneralInfoView;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class AdfGeneralInfoActivity extends AbstractActivity implements AdfGeneralInfoView.Presenter {

    private AdfGeneralInfoView view;

    private final AdfData adfData;

    private AdfHeader adfHeader;

    @Inject
    public AdfGeneralInfoActivity(AdfGeneralInfoView view,
                                  AdfData adfData) {
        this.view = view;
        this.adfData = adfData;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        //TODO load from an ontotlogy
        List<String> techTypes = new ArrayList<String>();
        techTypes.addAll(asList(new String[]{
                "in_situ_oligo_features",
                "spotted_antibody_features",
                "spotted_colony_features",
                "spotted_ds_DNA_features",
                "spotted_protein_features",
                "spotted_ss_PCR_amplicon_features",
                "spotted_ss_oligo_features"
        }));

        List<String> surfTypes = new ArrayList<String>();
        surfTypes.addAll(asList(new String[]{
                "aminosilane",
                "polylysine",
                "unknown_surface_type"
        }));

        List<String> subTypes = new ArrayList<String>();
        subTypes.addAll(asList(new String[]{
                "glass",
                "nitrocellulose",
                "nylon",
                "silicon",
                "unknown_substrate_type"
        }));

        List<String> species = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            species.add("specie-" + i);
        }

        view.setTechnologyTypes(techTypes);
        view.setSurfaceTypes(surfTypes);
        view.setSubstrateTypes(subTypes);
        view.setSpecies(species);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
    }

    public AdfGeneralInfoActivity withPlace(AdHeaderPlace place) {
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
                    view.setTechnologyType(header.getTechnologyType(true).getName());
                    view.setSurfaceType(header.getSurfaceType(true).getName());
                    view.setSubstrateType(header.getSubstrateType(true).getName());
                }
            }
        }.wrap());
    }



}
