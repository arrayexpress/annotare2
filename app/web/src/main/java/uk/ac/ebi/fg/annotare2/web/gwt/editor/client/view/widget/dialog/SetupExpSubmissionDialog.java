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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.WaitingPanel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class SetupExpSubmissionDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, SetupExpSubmissionDialog> {
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

    public SetupExpSubmissionDialog() {
        this(true);
    }

    public SetupExpSubmissionDialog(boolean cancelable) {
        setModal(true);
        setGlassEnabled(true);
        setText("New Experiment Submission");

        setWidget(Binder.BINDER.createAndBindUi(this));

        cancelButton.setVisible(cancelable);
        templateBox.addItem("One-color microarray", ONE_COLOR);
        templateBox.addItem("Two-color microarray", TWO_COLOR);
        templateBox.addItem("High-throughput sequencing", SEQ);
        templateBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showDetails(templateBox.getValue(templateBox.getSelectedIndex()));
            }
        });
        center();

        selectFirstTemplate();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick(ClickEvent event) {
        hide();
    }

    @UiHandler("okButton")
    public void onOkButtonClick(ClickEvent event) {
        final WaitingPanel w = new WaitingPanel("Creating new submission, please wait...");
        setWidget(w);
        center();
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
