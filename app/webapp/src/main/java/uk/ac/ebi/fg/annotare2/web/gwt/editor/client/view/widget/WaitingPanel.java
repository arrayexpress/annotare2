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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.DataImportException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;

/**
 * @author Olga Melnichuk
 */
public class WaitingPanel extends Composite {

    interface Binder extends UiBinder<Widget, WaitingPanel> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Label label;

    public WaitingPanel(String message) {
        initWidget(Binder.BINDER.createAndBindUi(this));
        label.setText(message);
    }

    public void showSuccess(String msg) {
        label.setText(msg);
    }

    public void showError(Throwable caught) {
        String msg;
        if (caught instanceof NoPermissionException) {
            msg = "Sorry, you do not have permission to change this submission";
        } else if (caught instanceof ResourceNotFoundException) {
            msg = "Sorry, submission you are trying to change doesn't exist";
        } else if (caught instanceof DataImportException) {
            msg = caught.getMessage();
        } else {
            msg = "Unexpected error happened. Please try again later.";
        }
        label.setText(msg);
    }
}
