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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DataFilesUploadView;

public class DataUploadAndAssignmentViewImpl extends Composite implements DataUploadAndAssignmentView, RequiresResize {

    @UiField
    DataFilesUploadView dataFilesUploadView;

    @UiField
    DataAssignmentView dataAssignmentView;

    interface Binder extends UiBinder<Widget, DataUploadAndAssignmentViewImpl> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public DataUploadAndAssignmentViewImpl() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public DataFilesUploadView getUploadView() {
        return dataFilesUploadView;
    }

    @Override
    public DataAssignmentView getAssignmentView() {
        return dataAssignmentView;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        dataFilesUploadView.setPresenter(presenter);
        dataAssignmentView.setPresenter(presenter);
    }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
