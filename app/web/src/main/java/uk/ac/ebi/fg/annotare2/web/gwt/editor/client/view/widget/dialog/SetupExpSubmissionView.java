package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.WaitingPopup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class SetupExpSubmissionView extends Composite {


    interface Binder extends UiBinder<Widget, SetupExpSubmissionView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private static final String ONE_COLOR = "1-color";
    private static final String TWO_COLOR = "2-color";
    private static final String SEQ = "seq";

    @UiField
    ScrollPanel templateDetails;

    @UiField
    ListBox templateBox;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private Presenter presenter;

    private final Map<String, HasSubmissionSettings> details = new HashMap<String, HasSubmissionSettings>();

    public SetupExpSubmissionView() {
        this(null);
    }

    public SetupExpSubmissionView(ClickHandler cancelClick) {

        initWidget(Binder.BINDER.createAndBindUi(this));

        if (cancelClick == null) {
            cancelButton.setVisible(false);
        } else {
            cancelButton.addClickHandler(cancelClick);
        }

        templateBox.addItem("One-color microarray", ONE_COLOR);
        templateBox.addItem("Two-color microarray", TWO_COLOR);
        templateBox.addItem("High-throughput sequencing", SEQ);
        templateBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showDetails(templateBox.getValue(templateBox.getSelectedIndex()));
            }
        });

        selectFirstTemplate();
    }

    @UiHandler("okButton")
    public void onOkButtonClick(ClickEvent event) {
        final WaitingPopup w = new WaitingPopup("Creating new submission, please wait...");
        w.showRelativeTo(okButton);
        presenter.setupNewSubmission(((HasSubmissionSettings) templateDetails.getWidget()).getSettings(),
                new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        w.showError(caught);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        w.showSuccess("New submission has been created. Loading new content...");
                        Window.Location.reload();
                    }
                });
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void showDetails(String key) {
        HasSubmissionSettings w = details.get(key);
        if (w == null) {
            w = createDetails(key);
            details.put(key, w);
        }
        templateDetails.setWidget(w);
    }

    private HasSubmissionSettings createDetails(String key) {
        if (ONE_COLOR.equals(key)) {
            return new OneColorMicroarraySettings();
        } else if (TWO_COLOR.equals(key)) {
            return new TwoColorMicroarraySettings();
        } else if (SEQ.equals(key)) {
            return new HighThroughputSeqSettings();
        } else {
            throw new IllegalArgumentException("Unknown key: " + key);
        }
    }

    private void selectFirstTemplate() {
        templateBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), templateBox);
    }

    public interface HasSubmissionSettings extends IsWidget {
        Map<String, String> getSettings();
    }

    public interface Presenter {
        void setupNewSubmission(Map<String, String> properties, AsyncCallback<Void> callback);
    }
}
