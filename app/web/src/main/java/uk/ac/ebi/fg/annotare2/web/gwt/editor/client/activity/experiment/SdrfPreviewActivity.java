/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.AsyncCallbackWrapper;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionServiceAsync;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.SdrfPreviewPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.SdrfPreviewView;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

/**
 * @author Olga Melnichuk
 */
public class SdrfPreviewActivity extends AbstractActivity {

    private final SdrfPreviewView view;

    private final PlaceController placeController;

    private final SubmissionServiceAsync submissionService;

    @Inject
    public SdrfPreviewActivity(SdrfPreviewView view,
                               PlaceController placeController,
                               SubmissionServiceAsync submissionService) {
        this.view = view;
        this.placeController = placeController;
        this.submissionService = submissionService;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view.asWidget());
        initAsync();
    }

    public SdrfPreviewActivity withPlace(SdrfPreviewPlace place) {
        return this;
    }

    public void goTo(Place place) {
        placeController.goTo(place);
    }

    private void initAsync() {
        submissionService.getSdrfTable(getSubmissionId(), new AsyncCallbackWrapper<Table>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Unable to load SDRF Preview");
            }

            @Override
            public void onSuccess(Table result) {
                view.setTable(result, true);
            }
        }.wrap());
    }
}
