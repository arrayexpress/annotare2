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
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign.AdfTabToolBarActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.IdfTabToolBarActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment.SdrfTabToolBarActivity;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ArrayDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.IdfPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.SdrfPlace;

/**
 * @author Olga Melnichuk
 */
public class EditorTabToolBarActivityMapper implements ActivityMapper {

    private final Provider<IdfTabToolBarActivity> idfToolBarActivityProvider;
    private final Provider<SdrfTabToolBarActivity> sdrfToolBarActivityProvider;
    private final Provider<AdfTabToolBarActivity> adfTabToolBarActivityProvider;

    @Inject
    public EditorTabToolBarActivityMapper(Provider<IdfTabToolBarActivity> idfActivityProvider,
                                          Provider<SdrfTabToolBarActivity> sdrfActivityProvider,
                                          Provider<AdfTabToolBarActivity> adfTabToolBarActivityProvider) {
        this.idfToolBarActivityProvider = idfActivityProvider;
        this.sdrfToolBarActivityProvider = sdrfActivityProvider;
        this.adfTabToolBarActivityProvider = adfTabToolBarActivityProvider;
    }

    public Activity getActivity(Place place) {
        /*if (place instanceof IdfPlace) {
            return (idfToolBarActivityProvider.get()).withPlace(place);
        } else if (place instanceof SdrfPlace) {
            return (sdrfToolBarActivityProvider.get()).withPlace(place);
        } else if (place instanceof ArrayDesignPlace) {
            return (adfTabToolBarActivityProvider.get()).withPlace((ArrayDesignPlace)place);
        }*/
        //TODO
        return null;
    }
}
