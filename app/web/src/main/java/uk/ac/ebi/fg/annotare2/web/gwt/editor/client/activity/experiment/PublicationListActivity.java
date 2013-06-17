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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.PublicationDto;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CriticalUpdateEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpInfoPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.info.PublicationListView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class PublicationListActivity extends AbstractActivity implements PublicationListView.Presenter {

    private final PublicationListView view;
    private final ExperimentData experimentData;
    private HandlerRegistration criticalUpdateHandler;

    @Inject
    public PublicationListActivity(PublicationListView view,
                                   ExperimentData experimentData) {
        this.view = view;
        this.experimentData = experimentData;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.setPresenter(this);
        containerWidget.setWidget(view.asWidget());
        loadAsync();
        criticalUpdateHandler = eventBus.addHandler(CriticalUpdateEvent.getType(), new CriticalUpdateEventHandler() {
            @Override
            public void criticalUpdateStarted(CriticalUpdateEvent event) {
            }

            @Override
            public void criticalUpdateFinished(CriticalUpdateEvent event) {
                loadAsync();
            }
        });
    }

    @Override
    public void onStop() {
        experimentData.updatePublications(view.getPublications());
        criticalUpdateHandler.removeHandler();
        super.onStop();
    }

    public PublicationListActivity withPlace(ExpInfoPlace place) {
        return this;
    }

    @Override
    public void updatePublication(PublicationDto publication) {
        experimentData.updatePublication(publication);
    }

    @Override
    public void createPublication() {
        experimentData.createPublication();
    }

    @Override
    public void removePublications(List<PublicationDto> publications) {
        experimentData.removePublications(publications);
    }

    private void loadAsync() {
        experimentData.getPublicationsAsync(new AsyncCallback<List<PublicationDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert("Can't load publication list.");
            }

            @Override
            public void onSuccess(List<PublicationDto> result) {
                view.setPublications(result);
            }
        });
    }

}
