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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign.AdfTableSheetModeActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.ExpInfoSection;

/**
 * @author Olga Melnichuk
 */
public class EditorContentActivityMapper implements ActivityMapper {

    private final Provider<IdfContentActivity> idfContentActivityProvider;
    private final Provider<IdfSheetModeActivity> idfSheetModeActivityProvider;
    private final Provider<InfoGeneralStuffActivity> idfGeneralInfoActivityProvider;
    private final Provider<InfoContactListActivity> idfContactListActivityProvider;

    private final Provider<SdrfSheetModeActivity> sdrfSheetModeActivityProvider;
    private final Provider<SdrfContentActivity> sdrfContentActivityProvider;

    private final Provider<AdfGeneralInfoActivity> adfGeneralInfoActivityProvider;
    private final Provider<AdfTableSheetModeActivity> adfTableSheetModeActivityProvider;

    @Inject
    public EditorContentActivityMapper(Provider<IdfContentActivity> idfContentActivityProvider,
                                       Provider<IdfSheetModeActivity> idfSheetModeActivityProvider,
                                       Provider<InfoGeneralStuffActivity> idfGeneralInfoActivityProvider,
                                       Provider<InfoContactListActivity> idfContactListActivityProvider,
                                       Provider<SdrfSheetModeActivity> sdrfSheetModeActivityProvider,
                                       Provider<SdrfContentActivity> sdrfContentActivityProvider,
                                       Provider<AdfGeneralInfoActivity> adfGeneralInfoActivityProvider,
                                       Provider<AdfTableSheetModeActivity> adfTableSheetModeActivityProvider) {
        this.idfContentActivityProvider = idfContentActivityProvider;
        this.idfSheetModeActivityProvider = idfSheetModeActivityProvider;
        this.idfGeneralInfoActivityProvider = idfGeneralInfoActivityProvider;
        this.idfContactListActivityProvider = idfContactListActivityProvider;

        this.sdrfSheetModeActivityProvider = sdrfSheetModeActivityProvider;
        this.sdrfContentActivityProvider = sdrfContentActivityProvider;

        this.adfGeneralInfoActivityProvider = adfGeneralInfoActivityProvider;
        this.adfTableSheetModeActivityProvider = adfTableSheetModeActivityProvider;
    }

    public Activity getActivity(Place place) {
        if (place instanceof ExpInfoPlace) {
            ExpInfoPlace descrPlace = (ExpInfoPlace) place;
            ExpInfoSection section = descrPlace.getExpInfoSection();
            switch (section) {
                case GENERAL_INFO:
                    return (idfGeneralInfoActivityProvider.get()).withPlace(descrPlace);
                case CONTACTS:
                    return (idfContactListActivityProvider.get()).withPlace(descrPlace);
                default:
                    return (idfContentActivityProvider.get()).withPlace(place);
            }
        } else if (place instanceof ExpDesignPlace) {
            return (sdrfContentActivityProvider.get()).withPlace((ExpDesignPlace) place);
        } else if (place instanceof IdfPreviewPlace) {
            return (idfSheetModeActivityProvider.get()).withPlace((IdfPreviewPlace)place);
        } else if (place instanceof SdrfPreviewPlace) {
            return (sdrfSheetModeActivityProvider.get()).withPlace(place);
        } else if (place instanceof AdHeaderPlace) {
            AdHeaderPlace adHeaderPlace = (AdHeaderPlace) place;
            return (adfGeneralInfoActivityProvider.get()).withPlace(adHeaderPlace);
        } else if (place instanceof AdTablePlace) {
            return (adfTableSheetModeActivityProvider.get()).withPlace((AdTablePlace) place);
        }
        //TODO
        return null;
    }
}
