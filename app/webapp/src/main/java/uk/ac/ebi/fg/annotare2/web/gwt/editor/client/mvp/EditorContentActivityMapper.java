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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign.AdfDetailsActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign.AdfTablePreviewActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExpDesignSection;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ExpInfoSection;

/**
 * @author Olga Melnichuk
 */
public class EditorContentActivityMapper implements ActivityMapper {

    private final Provider<ExperimentDetailsActivity> expDetailsActivityProvider;
    private final Provider<ContactListActivity> contactsActivityProvider;
    private final Provider<PublicationListActivity> publicationsActivityProvider;

    private final Provider<SamplesActivity> samplesActivityProvider;
    private final Provider<ExtractAttributesActivity> extractAttributesActivityProvider;
    private final Provider<SingleCellExtractAttributesActivity> singleCellExtractAttributesActivityProvider;
    private final Provider<LabeledExtractsActivity> labeledExtractsActivityProvider;
    private final Provider<DataFileAssignmentActivity> dataFileAssignmentActivityProvider;
    private final Provider<ProtocolsActivity> protocolsActivityProvider;

    private final Provider<SdrfPreviewActivity> sdrfPreviewActivityProvider;
    private final Provider<IdfPreviewActivity> idfPreviewActivityProvider;

    private final Provider<AdfDetailsActivity> adfDetailsActivityProvider;
    private final Provider<AdfTablePreviewActivity> adfTablePreviewActivityProvider;

    @Inject
    public EditorContentActivityMapper(
            Provider<ExperimentDetailsActivity> expDetailsActivityProvider,
            Provider<ContactListActivity> contactsActivityProvider,
            Provider<PublicationListActivity> publicationsActivityProvider,
            Provider<SamplesActivity> samplesActivityProvider,
            Provider<ExtractAttributesActivity> extractAttributesActivityProvider,
            Provider<SingleCellExtractAttributesActivity> singleCellExtractAttributesActivityProvider,
            Provider<LabeledExtractsActivity> labeledExtractsActivityProvider,
            Provider<DataFileAssignmentActivity> dataFileAssignmentActivityProvider,
            Provider<ProtocolsActivity> protocolsActivityProvider,
            Provider<SdrfPreviewActivity> sdrfPreviewActivityProvider,
            Provider<IdfPreviewActivity> idfPreviewActivityProvider,
            Provider<AdfDetailsActivity> adfDetailsActivityProvider,
            Provider<AdfTablePreviewActivity> adfTablePreviewActivityProvider) {
        this.expDetailsActivityProvider = expDetailsActivityProvider;
        this.contactsActivityProvider = contactsActivityProvider;
        this.publicationsActivityProvider = publicationsActivityProvider;

        this.samplesActivityProvider = samplesActivityProvider;
        this.extractAttributesActivityProvider = extractAttributesActivityProvider;
        this.singleCellExtractAttributesActivityProvider = singleCellExtractAttributesActivityProvider;
        this.labeledExtractsActivityProvider = labeledExtractsActivityProvider;
        this.dataFileAssignmentActivityProvider = dataFileAssignmentActivityProvider;
        this.protocolsActivityProvider = protocolsActivityProvider;

        this.sdrfPreviewActivityProvider = sdrfPreviewActivityProvider;
        this.idfPreviewActivityProvider = idfPreviewActivityProvider;

        this.adfDetailsActivityProvider = adfDetailsActivityProvider;
        this.adfTablePreviewActivityProvider = adfTablePreviewActivityProvider;
    }

    public Activity getActivity(Place place) {
        if (place instanceof ExpInfoPlace) {
            ExpInfoPlace infoPlace = (ExpInfoPlace) place;
            ExpInfoSection section = infoPlace.getExpInfoSection();
            switch (section) {
                case GENERAL_INFO:
                    return (expDetailsActivityProvider.get()).withPlace(infoPlace);
                case CONTACTS:
                    return (contactsActivityProvider.get()).withPlace(infoPlace);
                case PUBLICATIONS:
                    return (publicationsActivityProvider.get()).withPlace(infoPlace);
            }
        } else if (place instanceof ExpDesignPlace) {
            ExpDesignPlace designPlace = (ExpDesignPlace) place;
            ExpDesignSection section = designPlace.getExpDesignSection();
            switch (section) {
                case GENERAL_INFO:
                    return (expDetailsActivityProvider.get()).withPlace(designPlace);
                case CONTACTS:
                    return (contactsActivityProvider.get()).withPlace(designPlace);
                case PUBLICATIONS:
                    return (publicationsActivityProvider.get()).withPlace(designPlace);
                case SAMPLES:
                    return (samplesActivityProvider.get()).withPlace(designPlace);
                case EXTRACTS_LIBRARY_INFO:
                    return (extractAttributesActivityProvider.get()).withPlace(designPlace);
                case SINGLE_CELL_LIBRARY_INFO:
                    return (singleCellExtractAttributesActivityProvider.get()).withPlace(designPlace);
                case LABELED_EXTRACTS:
                    return (labeledExtractsActivityProvider.get()).withPlace(designPlace);
                case FILES:
                    return (dataFileAssignmentActivityProvider.get()).withPlace(designPlace);
                case PROTOCOLS:
                    return (protocolsActivityProvider.get()).withPlace(designPlace);
            }
        } else if (place instanceof IdfPreviewPlace) {
            return (idfPreviewActivityProvider.get()).withPlace((IdfPreviewPlace) place);
        } else if (place instanceof SdrfPreviewPlace) {
            return (sdrfPreviewActivityProvider.get()).withPlace((SdrfPreviewPlace) place);
        } else if (place instanceof AdHeaderPlace) {
            AdHeaderPlace adHeaderPlace = (AdHeaderPlace) place;
            return (adfDetailsActivityProvider.get()).withPlace(adHeaderPlace);
        } else if (place instanceof AdTablePlace) {
            return (adfTablePreviewActivityProvider.get()).withPlace((AdTablePlace) place);
        }
        return null;
    }
}
