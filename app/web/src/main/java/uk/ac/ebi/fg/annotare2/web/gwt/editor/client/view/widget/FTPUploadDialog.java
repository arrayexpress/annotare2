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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    TextArea values;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private Presenter presenter;

    public FTPUploadDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("FTP Upload");

        setWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public void setFtpProperties(String url, String username, String password) {
        ftpUrl.setText(url);
        ftpUsername.setText(username);
        ftpPassword.setText(password);
    }

    @Override
    public void show() {
        values.setValue("");
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
        if (!pastedData.isEmpty() && null != presenter) {
            presenter.onFtpDataSubmit(pastedData, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    Window.alert("Unable to send file information, please try again");
                }

                @Override
                public void onSuccess(String result) {
                    if (null != result && !result.isEmpty()) {
                        Window.alert("Unable to process FTP files:\n" + result);
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
    private List<String> getPastedData() {
        String pastedRows = values.getValue();
        List<String> result = new ArrayList<String>();
        if (null != pastedRows && !pastedRows.isEmpty()) {
            result.addAll(Arrays.asList(pastedRows.split("\\r\\n|[\\r\\n]")));
        }
        return result;
    }
    public interface Presenter {
        void onFtpDataSubmit(List<String> data, AsyncCallback<String> callback);
    }
}
