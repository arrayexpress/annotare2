/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.List;

public interface DataFilesUploadView extends IsWidget {

    void setDataFiles(List<DataFileRow> rows);

    void setExperimentType(ExperimentProfileType type);

    void setPresenter(Presenter presenter);

    void setFtpProperties(boolean isEnabled, String url, String username, String password);

    interface Presenter extends DataFilesUploadPanel.Presenter, FTPUploadDialog.Presenter, DataFileListPanel.Presenter {
    }
}
