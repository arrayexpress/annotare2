package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
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

    String globusTransferAPIURL;

    public GlobusUploadDialog(long submissionId, String globusTransferAPIURL) {
        setModal(true);
        setGlassEnabled(true);
        setText("Globus Upload");
        setWidget(uiBinder.createAndBindUi(this));
        addStyleName("globusUploadDialog");
        this.submissionId = submissionId;
        this.globusTransferAPIURL = globusTransferAPIURL;
        // Set the ID for React component container
        reactContainer.getElement().setId("globus-upload-container");
        // Close button action
        closeButton.addClickHandler(event -> this.hide());
    }

    public void showDialog() {
        // Initially hide the dialog element but position it
        getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        getElement().getStyle().setOpacity(0);
        // Show the dialog without making it visible yet
        show();
        // Set initial center position
        center();
        // Add the React component to the DOM
        addReactWebComponent();
        // After React component is loaded, make the dialog visible with a fade-in effect
        new com.google.gwt.user.client.Timer() {
            @Override
            public void run() {
                // Calculate center position again with the proper content
                center();
                // Show the dialog with a smooth transition
                getElement().getStyle().setProperty("transition", "opacity 0.3s ease-in-out");
                getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);
                getElement().getStyle().setOpacity(1);
            }
        }.schedule(10);
    }

    private void addReactWebComponent() {
        GlobusUploadWebComponent reactWebComponent = (GlobusUploadWebComponent) DomGlobal.document.createElement("globus-transfer-dialog");
        reactWebComponent.setsubmissionId(submissionId);
        reactWebComponent.setapiBaseUrl(globusTransferAPIURL);
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
    @JsProperty(name = "api_base_url")
    public native void setapiBaseUrl(String apiBaseUrl);
    @JsProperty(name = "api_base_url")
    public native String getapiBaseUrl();
}
