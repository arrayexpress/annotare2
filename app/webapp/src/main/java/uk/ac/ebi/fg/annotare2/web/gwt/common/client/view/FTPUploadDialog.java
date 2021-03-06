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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ApplicationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FTPUploadDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, FTPUploadDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    InlineLabel ftpUsername;

    @UiField
    InlineLabel ftpPassword;

    @UiField
    Anchor ftpUrl;

    @UiField
    TextArea values;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private Presenter presenter;

    private String ftpBaseUrl;

    private String ftpBasePath;

    public FTPUploadDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("FTP Upload");

        setWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setApplicationProperties(ApplicationProperties properties) {
        ftpBasePath = properties.getFtpHostname() + properties.getFtpPath();
        ftpBaseUrl = "ftp://" + properties.getFtpUsername() + ":" + properties.getFtpPassword() + "@" + ftpBasePath;

        ftpUsername.setText(properties.getFtpUsername());
        ftpPassword.setText(properties.getFtpPassword());
        ftpUrl.setText(ftpBasePath);
        ftpUrl.setHref(ftpBaseUrl);
    }

    public void setSubmissionDirectory(String ftpDirectory) {
        ftpUrl.setText(ftpBasePath + ftpDirectory + "/");
        ftpUrl.setHref(ftpBaseUrl + ftpDirectory + "/");
    }

    @Override
    public void show() {
        values.setValue("");
        okButton.setEnabled(true);
        super.show();
        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                values.setFocus(true);
            }
        });
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        List<String> pastedData = getPastedData();
        if (!pastedData.isEmpty() && null != presenter && checkPastedData(pastedData)) {
            okButton.setEnabled(false);
            final PopupPanel w = new WaitingPopup();
            w.center();
            presenter.uploadFtpFiles(pastedData,
                    new ReportingAsyncCallback<String>(FailureMessage.UNABLE_TO_UPLOAD_FILES) {
                        @Override
                        public void onFailure(Throwable caught) {
                            super.onFailure(caught);
                            w.hide();
                            okButton.setEnabled(true);
                        }

                        @Override
                        public void onSuccess(String result) {
                            w.hide();
                            if (null != result && !result.isEmpty()) {
                                NotificationPopupPanel.error("Unable to process FTP files:<br><br>" + result.replaceAll("\n", "<br>"), false, false);
                                okButton.setEnabled(true);
                            } else {
                                hide();
                            }
                        }
                    });
        }
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
        hide();
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                hide();
            }
        }
    }

    private Boolean checkPastedData(List<String> pastedData) {
        String[] result;
        for (String data : pastedData) {
            result = data.split(":|\\\\",5);
            if(result.length > 1) {
                NotificationPopupPanel.error(
                        "FTP/Aspera file path contains illegal characters." +
                                " Please correct them before uploading.", false, false);
                return false;
            }
        }
        return true;
    }

    private List<String> getPastedData() {
        String pastedRows = values.getValue();
        List<String> result = new ArrayList<>();
        if (null != pastedRows && !pastedRows.isEmpty()) {
            result.addAll(Arrays.asList(pastedRows.split("\\r\\n|[\\r\\n]")));
        }
        return result;
    }

    public interface Presenter {
        void uploadFtpFiles(List<String> data, AsyncCallback<String> callback);
    }
}
