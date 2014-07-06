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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class FTPUploadDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, FTPUploadDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    InlineLabel ftpUsername;

    @UiField
    InlineLabel ftpPassword;

    @UiField
    InlineLabel ftpUrl;

    @UiField
    FtpFileRegistrationForm fileRegistrationForm;

    private final Presenter presenter;

    public FTPUploadDialog(Presenter presenter) {
        this.presenter = presenter;

        setModal(true);
        setGlassEnabled(true);
        setText("FTP Upload");

        setWidget(Binder.BINDER.createAndBindUi(this));
        center();

        fileRegistrationForm.setPresenter(presenter);
    }

    public void setFtpProperties(String url, String username, String password) {
        ftpUrl.setText(url);
        ftpUsername.setText(username);
        ftpPassword.setText(password);
    }

    public interface Presenter extends FtpFileRegistrationForm.Presenter {
    }

}
