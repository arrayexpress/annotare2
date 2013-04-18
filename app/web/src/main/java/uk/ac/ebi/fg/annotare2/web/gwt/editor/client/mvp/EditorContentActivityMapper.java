/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

    private final Provider<ExpDetailsActivity> expInfoGeneralActivityProvider;
    private final Provider<ContactListActivity> expInfoContactsActivityProvider;

    private final Provider<SamplesActivity> expDesignSamplesActivityProvider;
    private final Provider<ExtractsActivity> expDesignExtractsActivityProvider;
    private final Provider<LabeledExtractsActivity> expDesignLabeledExtractsActivityProvider;
    private final Provider<RawFilesActivity> expDesignRawFilesActivityProvider;


    private final Provider<SdrfPreviewActivity> sdrfPreviewActivityProvider;
    private final Provider<IdfPreviewActivity> idfPreviewActivityProvider;

    private final Provider<AdfDetailsActivity> adfInfoGeneralActivityProvider;
    private final Provider<AdfTablePreviewActivity> adfTablePreviewActivityProvider;

    @Inject
    public EditorContentActivityMapper(
            Provider<ExpDetailsActivity> expInfoGeneralActivityProvider,
            Provider<ContactListActivity> expInfoContactsActivityProvider,
            Provider<SamplesActivity> expDesignSamplesActivityProvider,
            Provider<ExtractsActivity> expDesignExtractsActivityProvider,
            Provider<LabeledExtractsActivity> expDesignLabeledExtractsActivityProvider,
            Provider<RawFilesActivity> expDesignRawFilesActivityProvider,
            Provider<SdrfPreviewActivity> sdrfPreviewActivityProvider,
            Provider<IdfPreviewActivity> idfPreviewActivityProvider,
            Provider<AdfDetailsActivity> adfInfoGeneralActivityProvider,
            Provider<AdfTablePreviewActivity> adfTablePreviewActivityProvider) {
        this.expInfoGeneralActivityProvider = expInfoGeneralActivityProvider;
        this.expInfoContactsActivityProvider = expInfoContactsActivityProvider;

        this.expDesignSamplesActivityProvider = expDesignSamplesActivityProvider;
        this.expDesignExtractsActivityProvider = expDesignExtractsActivityProvider;
        this.expDesignLabeledExtractsActivityProvider = expDesignLabeledExtractsActivityProvider;
        this.expDesignRawFilesActivityProvider = expDesignRawFilesActivityProvider;

        this.sdrfPreviewActivityProvider = sdrfPreviewActivityProvider;
        this.idfPreviewActivityProvider = idfPreviewActivityProvider;

        this.adfInfoGeneralActivityProvider = adfInfoGeneralActivityProvider;
        this.adfTablePreviewActivityProvider = adfTablePreviewActivityProvider;
    }

    public Activity getActivity(Place place) {
        if (place instanceof ExpInfoPlace) {
            ExpInfoPlace infoPlace = (ExpInfoPlace) place;
            ExpInfoSection section = infoPlace.getExpInfoSection();
            switch (section) {
                case GENERAL_INFO:
                    return (expInfoGeneralActivityProvider.get()).withPlace(infoPlace);
                case CONTACTS:
                    return (expInfoContactsActivityProvider.get()).withPlace(infoPlace);
            }
        } else if (place instanceof ExpDesignPlace) {
            ExpDesignPlace designPlace = (ExpDesignPlace) place;
            ExpDesignSection section = designPlace.getExpDesignSection();
            switch (section) {
                case SAMPLES:
                    return (expDesignSamplesActivityProvider.get()).withPlace(designPlace);
                case EXTRACTS:
                    return (expDesignExtractsActivityProvider.get()).withPlace(designPlace);
                case LABELED_EXTRACTS:
                    return (expDesignLabeledExtractsActivityProvider.get()).withPlace(designPlace);
                case RAW_FILES:
                    return (expDesignRawFilesActivityProvider.get()).withPlace(designPlace);
            }
        } else if (place instanceof IdfPreviewPlace) {
            return (idfPreviewActivityProvider.get()).withPlace((IdfPreviewPlace) place);
        } else if (place instanceof SdrfPreviewPlace) {
            return (sdrfPreviewActivityProvider.get()).withPlace(place);
        } else if (place instanceof AdHeaderPlace) {
            AdHeaderPlace adHeaderPlace = (AdHeaderPlace) place;
            return (adfInfoGeneralActivityProvider.get()).withPlace(adHeaderPlace);
        } else if (place instanceof AdTablePlace) {
            return (adfTablePreviewActivityProvider.get()).withPlace((AdTablePlace) place);
        }
        return null;
    }
}
