package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * @author Olga Melnichuk
 */
public class SubmissionCreateDialog extends DialogBox {

    public SubmissionCreateDialog() {

        setText("New Submission: select a template");
        setModal(true);
        setGlassEnabled(true);

        SubmissionCreateDialogContent2 content = new SubmissionCreateDialogContent2();
        content.addCloseHandler(new CloseHandler<SubmissionCreateDialogContent2>() {
            public void onClose(CloseEvent<SubmissionCreateDialogContent2> event) {
                SubmissionCreateDialog.this.hide();
            }
        });
        setWidget(content);
        center();
    }


}
