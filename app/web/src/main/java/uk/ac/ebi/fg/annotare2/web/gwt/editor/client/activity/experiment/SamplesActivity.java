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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.data.ExperimentData;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SamplesView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SamplesActivity extends AbstractActivity {

    private final SamplesView view;
    private final ExperimentData expData;

    @Inject
    public SamplesActivity(SamplesView view,
                           ExperimentData expData) {
        this.view = view;
        this.expData = expData;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        loadSamples();
    }

    public SamplesActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    private void loadSamples() {
        expData.getSamplesAsync(new AsyncCallback<List<SampleRow>>() {
            @Override
            public void onFailure(Throwable caught) {
                //TODO
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(List<SampleRow> result) {
                //TODO load column list
                view.setData(result, new ArrayList<SampleColumn>());
            }
        });
    }
}
