/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.submission.model.FileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.List;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public interface DataAssignmentView extends IsWidget {

    void setData(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows);

    void updateData(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows);

    void setExperimentType(ExperimentProfileType type);

    void setPresenter(Presenter presenter);

    void setDataFiles(List<DataFileRow> dataFiles);

    void setDeletedFiles(Set<DataFileRow> dataFiles);

    public static interface Presenter {

        void createColumn(FileType type);

        void removeColumns(List<Integer> indices);

        void updateColumn(DataAssignmentColumn column);
    }
}
