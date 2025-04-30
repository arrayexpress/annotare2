package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GlobusUploadDialog  extends DialogBox {
    interface GlobusUploadDialogUiBinder extends UiBinder<Widget, GlobusUploadDialog> {}
    private static GlobusUploadDialogUiBinder uiBinder = GWT.create(GlobusUploadDialogUiBinder.class);

    @UiField
    VerticalPanel mainPanel;

    @UiField
    HTMLPanel reactContainer;

    @UiField
    Button closeButton;

    public GlobusUploadDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("Globus Upload");
        setWidget(uiBinder.createAndBindUi(this));

        // Set the ID for React component container
        reactContainer.getElement().setId("globus-upload-container");

        // Close button action
        closeButton.addClickHandler(event -> this.hide());
    }

    public void showDialog() {
        center();
        show();

        // Delay execution to ensure dialog is rendered before inserting React component
        new com.google.gwt.user.client.Timer() {
            @Override
            public void run() {
                showReactComponent("globus-upload-container");
            }
        }.schedule(10);
    }

    private native void showReactComponent(String containerId) /*-{
        $wnd.document.getElementById(containerId).innerHTML = "<globus-transfer-dialog></globus-transfer-dialog>";
    }-*/;
}
