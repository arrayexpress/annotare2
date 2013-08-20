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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.FtpFileInfo;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DeleteEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DeleteEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class FtpFileRegistrationForm extends Composite {

    interface Binder extends UiBinder<Widget, FtpFileRegistrationForm> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    FlowPanel form;

    @UiField
    HTMLPanel formControls;

    @UiField
    Anchor addMore;

    @UiField
    Button submit;

    private Presenter presenter;
    private final SuccessMessage successMessage;

    public FtpFileRegistrationForm() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        form.add(addWidget());
        successMessage = new SuccessMessage("All files were successfully submitted.", "Continue");
        successMessage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hideSuccessMessage();
            }
        });
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("addMore")
    void addMoreClicked(ClickEvent event) {
        form.add(addWidget());
    }

    @UiHandler("submit")
    void submitClicked(ClickEvent event) {
        if (!isFormValid() || presenter == null) {
            return;
        }

        List<FtpFileInfo> list = new ArrayList<FtpFileInfo>();
        for (int i = 0; i < form.getWidgetCount(); i++) {
            FtpFileDetails details = (FtpFileDetails) form.getWidget(i);
            list.add(new FtpFileInfo(details.getFileName(), details.getMd5()));
        }

        if (list.isEmpty()) {
            return;
        }

        setFormEnabled(false);
        presenter.onFtpRegistrationFormSubmit(list, new AsyncCallback<Map<Integer, String>>() {
            @Override
            public void onFailure(Throwable caught) {
                setFormEnabled(true);
                Window.alert("server error: can't submit the data");
            }

            @Override
            public void onSuccess(Map<Integer, String> result) {
                setFormEnabled(true);
                setFormErrors(result);
            }
        });
    }

    private void setFormEnabled(boolean enabled) {
        for (int i = 0; i < form.getWidgetCount(); i++) {
            FtpFileDetails details = (FtpFileDetails) form.getWidget(i);
            details.setEnabled(enabled);
        }
        submit.setEnabled(enabled);
    }

    private void setFormErrors(Map<Integer, String> errors) {
        for (int i = form.getWidgetCount() - 1; i>=0; i--) {
            FtpFileDetails details = (FtpFileDetails) form.getWidget(i);
            if (errors.containsKey(i)) {
                details.setError(errors.get(i));
            } else {
                form.remove(i);
            }
        }
        if (errors.isEmpty()) {
            showSuccessMessage();
        }
    }

    private void showSuccessMessage() {
        if (form.getWidgetCount() == 0) {
            form.add(successMessage);
            formControls.setVisible(false);
        }
    }

    private void hideSuccessMessage() {
        form.clear();
        form.add(addWidget());
        formControls.setVisible(true);
    }

    private boolean isFormValid() {
        for (int i = 0; i < form.getWidgetCount(); i++) {
            FtpFileDetails details = (FtpFileDetails) form.getWidget(i);
            if (!details.isValid()) {
                return false;
            }
        }
        return true;
    }

    private Widget addWidget() {
        final int index = form.getWidgetCount();
        FtpFileDetails details = new FtpFileDetails();
        details.addDeleteEventHandler(new DeleteEventHandler() {
            @Override
            public void onDelete(DeleteEvent event) {
                removeWidget(index);
            }
        });
        return details;
    }

    private void removeWidget(int index) {
        form.remove(index);
    }


    public interface Presenter {

        void onFtpRegistrationFormSubmit(List<FtpFileInfo> details, AsyncCallback<Map<Integer, String>> callback);
    }
}
