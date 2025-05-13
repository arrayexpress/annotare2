package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

public class GlobusUploadDialog  extends DialogBox {
    interface GlobusUploadDialogUiBinder extends UiBinder<Widget, GlobusUploadDialog> {}
    private static GlobusUploadDialogUiBinder uiBinder = GWT.create(GlobusUploadDialogUiBinder.class);

    @UiField
    VerticalPanel mainPanel;

    @UiField
    HTMLPanel reactContainer;

    @UiField
    Button closeButton;
    
    Long submissionId;

    public GlobusUploadDialog(long submissionId) {
        setModal(true);
        setGlassEnabled(true);
        setText("Globus Upload");
        setWidget(uiBinder.createAndBindUi(this));
        this.submissionId = submissionId;
        // Set the ID for React component container
        reactContainer.getElement().setId("globus-upload-container");

        // Close button action
        closeButton.addClickHandler(event -> this.hide());
    }

    public void showDialog() {
        center();
        show();

        // Delay execution to ensure dialog is rendered before inserting React component
//        new com.google.gwt.user.client.Timer() {
//            @Override
//            public void run() {
//                showReactComponent("globus-upload-container");
//            }
//        }.schedule(10);
        addReactWebComponent();
    }

    private void addReactWebComponent() {
        GlobusUploadWebComponent reactWebComponent = (GlobusUploadWebComponent) DomGlobal.document.createElement("globus-transfer-dialog");
        reactWebComponent.setsubmissionId(submissionId);
        DomGlobal.document.getElementById("globus-upload-container").appendChild(reactWebComponent);
    }


    private native void showReactComponent(String containerId) /*-{
        var submissionId = this.@uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.GlobusUploadDialog::submissionId;
        $wnd.document.getElementById(containerId).innerHTML = "<globus-transfer-dialog submission_id=\"" + submissionId + "\"></globus-transfer-dialog>";
    }-*/;
}

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
class GlobusUploadWebComponent extends HTMLElement {
    @JsProperty(name = "submission_id")
    public native void setsubmissionId(Long submissionId);
    @JsProperty(name = "submission_id")
    public native String getsubmissionId();
}
