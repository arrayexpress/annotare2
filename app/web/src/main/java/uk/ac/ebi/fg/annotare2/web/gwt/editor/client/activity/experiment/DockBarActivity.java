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
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.DockBarView;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DockBarEvent.toggleDockBarEvent;

/**
 * @author Olga Melnichuk
 */
public class DockBarActivity extends AbstractActivity {

    private final DockBarView view;

    @Inject
    public DockBarActivity(DockBarView view) {
        this.view = view;
    }

    @Override
    public void start(AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(view);
        view.setPresenter(new DockBarView.Presenter() {
            @Override
            public void fireDockBarToggleEvent() {
                eventBus.fireEvent(toggleDockBarEvent());
            }
        });
    }

    public Activity withPlace(Place place) {
        return this;
    }

}
