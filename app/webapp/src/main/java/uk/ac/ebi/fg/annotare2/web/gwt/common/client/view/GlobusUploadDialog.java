package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;

import java.util.ArrayList;
import java.util.List;

public class GlobusUploadDialog  extends DialogBox {
    interface GlobusUploadDialogUiBinder extends UiBinder<Widget, GlobusUploadDialog> {}

    private static GlobusUploadDialogUiBinder uiBinder = GWT.create(GlobusUploadDialogUiBinder.class);

    private FTPUploadDialog.Presenter presenter;

    @UiField
    VerticalPanel mainPanel;

    @UiField
    HTMLPanel reactContainer;

    @UiField
    Button closeButton;

    Long submissionId;

    String globusTransferAPIURL;

    String contextPath;

    public GlobusUploadDialog(long submissionId, String globusTransferAPIURL, String contextPath, DataFilesUploadView.Presenter presenter) {
        setModal(true);
        setGlassEnabled(true);
        setText("Globus Upload");
        setWidget(uiBinder.createAndBindUi(this));
        addStyleName("globusUploadDialog");
        this.submissionId = submissionId;
        this.globusTransferAPIURL = globusTransferAPIURL;
        this.contextPath = contextPath;
        this.presenter = presenter;
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

        reactWebComponent.addEventListener("button-clicked", evt -> {
            elemental2.dom.CustomEvent event = (elemental2.dom.CustomEvent) evt;
            DomGlobal.console.log("Button clicked event received", event);
            if (event.detail != null) {
                if (event.detail instanceof elemental2.core.JsArray) {
                    elemental2.core.JsArray<String> jsArray = (elemental2.core.JsArray<String>) event.detail;
                    List<String> fileInfos = new ArrayList<>();
                    for (int i = 0; i < jsArray.length; i++) {
                        fileInfos.add(jsArray.getAt(i));
                    }
                    processFileNames(fileInfos);
                }
            }
        });

        reactWebComponent.setsubmissionId(submissionId);
        reactWebComponent.setapiBaseUrl(contextPath + "/api");
        DomGlobal.document.getElementById("globus-upload-container").appendChild(reactWebComponent);
    }


    private native void showReactComponent(String containerId) /*-{
        var submissionId = this.@uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.GlobusUploadDialog::submissionId;
        $wnd.document.getElementById(containerId).innerHTML = "<globus-transfer-dialog submission_id=\"" + submissionId + "\"></globus-transfer-dialog>";
    }-*/;

    /**
     * Process the list of file names received from the React component
     * @param fileNamesList List of file names as strings
     */
    private void processFileNames(List<String> fileNamesList) {
        // Log the received file names
        DomGlobal.console.log("Processing file names: " + fileNamesList.size() + " files");
        if (!fileNamesList.isEmpty() && null != presenter && checkPastedData(fileNamesList)) {
            final PopupPanel w = new WaitingPopup();
            w.center();
            presenter.uploadFtpFiles(fileNamesList,
                    new ReportingAsyncCallback<String>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_UPLOAD_FILES) {
                        @Override
                        public void onFailure(Throwable caught) {
                            super.onFailure(caught);
                            w.hide();
                        }

                        @Override
                        public void onSuccess(String result) {
                            w.hide();
                            if (null != result && !result.isEmpty()) {
                                NotificationPopupPanel.error("Unable to process FTP files:<br><br>" + result.replaceAll("\n", "<br>"), false, false);
                            } else {
                                hide();
                            }
                        }
                    });
        }
        // Process each file name in the list
        for (String fileName : fileNamesList) {
            DomGlobal.console.log("Processing file: " + fileName);
        }

        // TODO: Implement the actual file processing logic here
    }

    private boolean checkPastedData(List<String> fileInfos) {
        String[] result;
        for (String data : fileInfos) {
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
