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
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.DataFileUploadView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFileUploadActivity extends AbstractActivity {

    private final DataFileUploadView view;

    @Inject
    public DataFileUploadActivity(DataFileUploadView view) {
        this.view = view;
    }

    public Activity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);

        List<DataFileRow> rows = new ArrayList<DataFileRow>();
        rows.add(new DataFileRow(1,
                "data.raw.1.zip",
                "424896a587b9a879c9a66c52bfa76424",
                "10Mb",
                new Date()));
        rows.add(new DataFileRow(2,
                "data.raw.2.zip",
                "424896a587b9a879c9a66c52bfa76424",
                "10Mb",
                new Date()));
        rows.add(new DataFileRow(3,
                "data.raw.3.zip",
                "424896a587b9a879c9a66c52bfa76424",
                "10Mb",
                new Date()));
        view.setRows(rows);
    }
}
