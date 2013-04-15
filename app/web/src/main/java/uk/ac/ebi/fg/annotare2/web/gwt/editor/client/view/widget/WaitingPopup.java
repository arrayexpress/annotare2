package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Olga Melnichuk
 */
public class WaitingPopup extends PopupPanel {

    private WaitingPanel panel;

    public WaitingPopup(String message) {
        super(false, true);
        panel = new WaitingPanel(message);
        setWidget(panel);
    }

    public void showError(Throwable caught) {
        panel.showError(caught);
    }

    public void showSuccess(String message) {
        panel.showSuccess(message);
    }
}
