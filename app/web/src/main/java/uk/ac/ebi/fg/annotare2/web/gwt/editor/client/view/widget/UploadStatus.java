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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwtupload.client.IUploadStatus;

import java.util.List;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class UploadStatus extends Composite implements IUploadStatus {

    interface Binder extends UiBinder<Widget, UploadStatus> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    ProgressBar progressBar;

    @UiField
    Label fileName;

    @UiField
    Label progressLabel;

    @UiField
    Label statusLabel;

    @UiField
    Anchor cancelIcon;

    @UiField
    HTMLPanel panel;

    private IUploadStatus.Status status = Status.UNINITIALIZED;

    protected Set<CancelBehavior> cancelCfg = DEFAULT_CANCEL_CFG;

    private UploadStatusChangedHandler onUploadStatusChangedHandler = null;

    private UploadStatusConstants i18nStrs = /*GWT.create(UploadStatusConstants.class);*/

            new IUploadStatus.UploadStatusConstants() {
                @Override
                public String uploadLabelCancel() {
                    return " ";
                }

                @Override
                public String uploadStatusCanceled() {
                    return "canceled";
                }

                @Override
                public String uploadStatusCanceling() {
                    return "canceling...";
                }

                @Override
                public String uploadStatusDeleted() {
                    return "deleted";
                }

                @Override
                public String uploadStatusError() {
                    return "error";
                }

                @Override
                public String uploadStatusInProgress() {
                    return " ";
                }

                @Override
                public String uploadStatusQueued() {
                    return "queued";
                }

                @Override
                public String uploadStatusSubmitting() {
                    return "submitting...";
                }

                @Override
                public String uploadStatusSuccess() {
                    return " ";
                }
            };

    public UploadStatus() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public HandlerRegistration addCancelHandler(final UploadCancelHandler handler) {
        return cancelIcon.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                event.preventDefault();
                handler.onCancel();
            }
        });
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public IUploadStatus newInstance() {
        UploadStatus newInstance = new UploadStatus();
        newInstance.setCancelConfiguration(cancelCfg);
        return newInstance;
    }

    @Override
    public void setCancelConfiguration(Set<CancelBehavior> config) {
        cancelCfg = config;
    }

    @Override
    public void setError(String error) {
        setStatus(Status.ERROR);
        Window.alert(error.replaceAll("\\\\n", "\\n"));
    }

    @Override
    public void setFileNames(List<String> name) {
        fileName.setText(name.get(0));
    }

    @Override
    public void setI18Constants(UploadStatusConstants strs) {
        /*if (strs == null) {
            throw new NullPointerException("Upload status constants can't be null");
        }
        i18nStrs = strs;*/
    }

    @Override
    public void setStatus(Status stat) {
        String statusName = status.toString().toLowerCase();
        statusLabel.removeStyleDependentName(statusName);
        statusLabel.addStyleDependentName(statusName);
        switch (stat) {
            case CHANGED:
            case QUEUED:
                updateStatusLabel(i18nStrs.uploadStatusQueued());
                break;
            case SUBMITING:
                updateStatusLabel(i18nStrs.uploadStatusSubmitting());
                break;
            case INPROGRESS:
                updateStatusLabel(i18nStrs.uploadStatusInProgress());
                if (!cancelCfg.contains(CancelBehavior.STOP_CURRENT)) {
                    cancelIcon.setVisible(false);
                }
                break;
            case SUCCESS:
                setProgress(1.0);
            case REPEATED:
                updateStatusLabel(i18nStrs.uploadStatusSuccess());
                if (!cancelCfg.contains(CancelBehavior.REMOVE_REMOTE)) {
                    cancelIcon.setVisible(false);
                }
                break;
            case INVALID:
                getWidget().removeFromParent();
                break;
            case CANCELING:
                updateStatusLabel(i18nStrs.uploadStatusCanceling());
                break;
            case CANCELED:
                updateStatusLabel(i18nStrs.uploadStatusCanceled());
                if (cancelCfg.contains(CancelBehavior.REMOVE_CANCELLED_FROM_LIST)) {
                    getWidget().removeFromParent();
                }
                break;
            case ERROR:
                updateStatusLabel(i18nStrs.uploadStatusError());
                break;
            case DELETED:
                updateStatusLabel(i18nStrs.uploadStatusDeleted());
                getWidget().removeFromParent();
                break;
        }
        if (status != stat && onUploadStatusChangedHandler != null) {
            status = stat;
            onUploadStatusChangedHandler.onStatusChanged(this);
        }
        status = stat;
    }

    @Override
    public void setStatusChangedHandler(UploadStatusChangedHandler handler) {
        onUploadStatusChangedHandler = handler;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    @Override
    public void setProgress(long done, long total) {
        double p = total == 0 ? 0 : 1.0 * done / total;
        setProgress(p);
    }

    private void setProgress(double v) {
        progressBar.setProgress(v);
        progressLabel.setText(Math.round(v * 100) + "%");
    }

    private void updateStatusLabel(String msg) {
        statusLabel.setText(msg);
    }
}
