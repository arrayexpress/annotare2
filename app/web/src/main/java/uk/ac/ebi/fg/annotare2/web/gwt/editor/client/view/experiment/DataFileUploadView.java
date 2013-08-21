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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment;

import com.google.gwt.user.client.ui.IsWidget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFileFtpUploadView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFileHttpUploadView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DataFileListView;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface DataFileUploadView extends IsWidget {

    void setRows(List<DataFileRow> rows);

    void setPresenter(Presenter presenter);

    void setFtpProperties(String url, String username, String password);

    interface Presenter extends DataFileHttpUploadView.Presenter, DataFileFtpUploadView.Presenter, DataFileListView.Presenter {
    }
}
