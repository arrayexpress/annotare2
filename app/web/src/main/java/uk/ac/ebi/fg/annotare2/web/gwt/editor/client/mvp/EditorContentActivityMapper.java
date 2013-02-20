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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.IdfContentActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.SdrfContentActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign.AdfGeneralInfoActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.AdHeaderPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.IdfPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.SdrfPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.header.AdfSection;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.idf.IdfSection;

/**
 * @author Olga Melnichuk
 */
public class EditorContentActivityMapper implements ActivityMapper {

    private final Provider<IdfContentActivity> idfContentActivityProvider;
    private final Provider<IdfSheetModeActivity> idfSheetModeActivityProvider;
    private final Provider<IdfGeneralInfoActivity> idfGeneralInfoActivityProvider;
    private final Provider<IdfContactListActivity> idfContactListActivityProvider;
    private final Provider<IdfTermSourceListActivity> idfTermSourceListActivityProvider;
    private final Provider<IdfExperimentalDesignListActivity> idfExperimentalDesignListActivityProvider;


    private final Provider<SdrfSheetModeActivity> sdrfSheetModeActivityProvider;
    private final Provider<SdrfContentActivity> sdrfContentActivityProvider;

    private final Provider<AdfGeneralInfoActivity> adfGeneralInfoActivityProvider;

    @Inject
    public EditorContentActivityMapper(Provider<IdfContentActivity> idfContentActivityProvider,
                                       Provider<IdfSheetModeActivity> idfSheetModeActivityProvider,
                                       Provider<IdfGeneralInfoActivity> idfGeneralInfoActivityProvider,
                                       Provider<IdfContactListActivity> idfContactListActivityProvider,
                                       Provider<IdfTermSourceListActivity> idfTermSourceListActivityProvider,
                                       Provider<IdfExperimentalDesignListActivity> idfExperimentalDesignListActivityProvider,
                                       Provider<SdrfSheetModeActivity> sdrfSheetModeActivityProvider,
                                       Provider<SdrfContentActivity> sdrfContentActivityProvider,
                                       Provider<AdfGeneralInfoActivity> adfGeneralInfoActivityProvider) {
        this.idfContentActivityProvider = idfContentActivityProvider;
        this.idfSheetModeActivityProvider = idfSheetModeActivityProvider;
        this.idfGeneralInfoActivityProvider = idfGeneralInfoActivityProvider;
        this.idfContactListActivityProvider = idfContactListActivityProvider;
        this.idfTermSourceListActivityProvider = idfTermSourceListActivityProvider;
        this.idfExperimentalDesignListActivityProvider = idfExperimentalDesignListActivityProvider;

        this.sdrfSheetModeActivityProvider = sdrfSheetModeActivityProvider;
        this.sdrfContentActivityProvider = sdrfContentActivityProvider;

        this.adfGeneralInfoActivityProvider = adfGeneralInfoActivityProvider;
    }

    public Activity getActivity(Place place) {
        if (place instanceof IdfPlace) {
            IdfPlace idfPlace = (IdfPlace) place;
            if (idfPlace.isSheetModeOn()) {
                return (idfSheetModeActivityProvider.get()).withPlace(place);
            }
            IdfSection section = idfPlace.getIdfSection();
            switch (section) {
                case GENERAL_INFO:
                    return (idfGeneralInfoActivityProvider.get()).withPlace(place);
                case CONTACTS:
                    return (idfContactListActivityProvider.get()).withPlace(place);
                case EXP_DESIGNS:
                    return (idfExperimentalDesignListActivityProvider.get()).withPlace(place);
                case TERM_DEF_SOURCES:
                    return (idfTermSourceListActivityProvider.get()).withPlace(place);
                default:
                    return (idfContentActivityProvider.get()).withPlace(place);
            }
        } else if (place instanceof SdrfPlace) {
            SdrfPlace sdrfPlace = (SdrfPlace) place;
            if (sdrfPlace.isSheetModeOn()) {
                return (sdrfSheetModeActivityProvider.get()).withPlace(place);
            }
            return (sdrfContentActivityProvider.get()).withPlace(place);

        } else if (place instanceof AdHeaderPlace) {
            AdHeaderPlace adHeaderPlace = (AdHeaderPlace) place;
            AdfSection section = adHeaderPlace.getSection();
            switch (section) {
                case GENERAL_INFO:
                    return (adfGeneralInfoActivityProvider.get()).withPlace(adHeaderPlace);
            }
        }
        //TODO
        return null;
    }
}
