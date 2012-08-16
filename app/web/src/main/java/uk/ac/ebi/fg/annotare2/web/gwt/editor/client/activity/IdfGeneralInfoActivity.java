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
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UIGeneralInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.IdfContentView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf.IdfGeneralInfoView;

/**
 * @author Olga Melnichuk
 */
public class IdfGeneralInfoActivity extends AbstractActivity {

    private final IdfGeneralInfoView view;

    private final PlaceController placeController;

    private final IdfServiceAsync idfService;

    @Inject
    public IdfGeneralInfoActivity(IdfGeneralInfoView view,
                                  PlaceController placeController,
                                  IdfServiceAsync idfService) {
        this.view = view;
        this.placeController = placeController;
        this.idfService = idfService;
    }

    public IdfGeneralInfoActivity withPlace(Place place) {
        //this.token = place.getPlaceName();
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        //TODO view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
    }

    @Override
    public void onStop() {
        //TODO save changes here
        super.onStop();
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void loadAsync() {
        final int submissionId = EditorUtils.getSubmissionId();
        idfService.loadInvestigation(submissionId, new AsyncCallbackWrapper<Table>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load Investigation Data.");
            }

            @Override
            public void onSuccess(Table result) {
                if (result != null) {
                    Investigation inv = new Investigation(result);

                    view.setTitle(inv.getTitle().getValue());
                    view.setDescription(inv.getDescription().getValue());
                    //view.setDateOfExperiment(inv.getDateOfExperiment().getValue());
                    //view.setDateOfPublicRelease(inv.getDateOfPublicRelease().getValue());
                }
            }
        }.wrap());

        /*idfService.getGeneralInfo(submissionId, new AsyncCallbackWrapper<UIGeneralInfo>() {
            public void onFailure(Throwable caught) {
                // TODO proper error handling
                Window.alert("Can't load idf general info for sid: " + submissionId);
            }

            public void onSuccess(UIGeneralInfo result) {
                if (result != null) {
                    view.setTitle(result.getTitle());
                    view.setDescription(result.getDescription());
                    view.setDateOfExperiment(result.getDateOfExperiment());
                    view.setDateOfPublicRelease(result.getDateOfPublicRelease());
                }
            }
        }.wrap());*/
    }
}

