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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.magetab.base.Table;
import uk.ac.ebi.fg.annotare2.magetab.idf.Investigation;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.InvestigationData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf.IdfSheetModeView;

/**
 * @author Olga Melnichuk
 */
public class IdfSheetModeActivity extends AbstractActivity {

    private final IdfSheetModeView view;

    private final PlaceController placeController;

    private final InvestigationData investigationData;

    @Inject
    public IdfSheetModeActivity(IdfSheetModeView view,
                                PlaceController placeController,
                                InvestigationData investigationData) {
        this.view = view;
        this.placeController = placeController;
        this.investigationData = investigationData;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        //TODO view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        initAsync();
    }

    public IdfSheetModeActivity withPlace(Place place) {
        //this.token = place.getPlaceName();
        return this;
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void initAsync() {
        investigationData.getTable(new AsyncCallbackWrapper<Table>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load IDF table: " + caught.getMessage());
            }

            @Override
            public void onSuccess(Table result) {
                view.setTable(result);
            }
        }.wrap());
    }
}
