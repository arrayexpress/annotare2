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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface SamplesView extends IsWidget {

    void setData(List<SampleRow> rows, List<SampleColumn> columns);

    void setPresenter(Presenter presenter);

    public interface Presenter {

        SampleAttributeEfoSuggest getEfoTerms();

        void updateColumns(List<SampleColumn> newColumns);

        void updateRow(SampleRow row);

        void createSample();

        void removeSamples(List<SampleRow> rows);

        void getMaterialTypesAsync(AsyncCallback<List<String>> callback);
    }
}
