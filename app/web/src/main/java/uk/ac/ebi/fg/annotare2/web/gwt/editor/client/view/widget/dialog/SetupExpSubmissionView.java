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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.WaitingPopup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class SetupExpSubmissionView extends Composite implements SubmissionSettingsDataSource {

    interface Binder extends UiBinder<Widget, SetupExpSubmissionView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private enum Settings {
        ONE_COLOR("One-color microarray") {
            @Override
            public HasSubmissionSettings createWidget(SubmissionSettingsDataSource source) {
                return new OneColorMicroarraySettings(source);
            }
        },
        TWO_COLOR("Two-color microarray") {
            @Override
            public HasSubmissionSettings createWidget(SubmissionSettingsDataSource source) {
                return  new TwoColorMicroarraySettings(source);
            }
        },
        SEQ("High-throughput sequencing") {
            @Override
            public HasSubmissionSettings createWidget(SubmissionSettingsDataSource source) {
                return new HighThroughputSeqSettings();
            }
        };

        private String title;

        private Settings(String title) {
            this.title = title;
        }

        private String getTitle() {
            return title;
        }

        public abstract HasSubmissionSettings createWidget(SubmissionSettingsDataSource source);
    }

    @UiField
    ScrollPanel templateDetails;

    @UiField
    ListBox templateBox;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private Presenter presenter;

    private final Map<Settings, HasSubmissionSettings> widgets = new HashMap<Settings, HasSubmissionSettings>();

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

        for (Settings s : Settings.values()) {
            templateBox.addItem(s.getTitle(), s.name());
        }

        templateBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showDetails(Settings.valueOf(getSelectedSettingsTemplate()));
            }
        });
        selectFirstTemplate(templateBox);
    }

    @UiHandler("okButton")
    public void onOkButtonClick(ClickEvent event) {
        if (presenter == null) {
            return;
        }
        okButton.setEnabled(false);
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
                        w.showSuccess("New submission has been created. Loading the content...");
                        Window.Location.reload();
                    }
                });
    }

    @Override
    public void getArrayDesigns(String query, AsyncCallback<List<ArrayDesignRef>> callback) {
        if (presenter != null) {
            presenter.getArrayDesigns(query, callback);
        }
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }


    private String getSelectedSettingsTemplate() {
        return templateBox.getValue(templateBox.getSelectedIndex());
    }

    private void showDetails(Settings key) {
        HasSubmissionSettings w = widgets.get(key);
        if (w == null) {
            w = key.createWidget(this);
            widgets.put(key, w);
        }
        templateDetails.setWidget(w);
    }

    private static void selectFirstTemplate(ListBox listBox) {
        listBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
    }

    public interface Presenter {

        void setupNewSubmission(Map<String, String> properties, AsyncCallback<Void> callback);

        void getArrayDesigns(String query, AsyncCallback<List<ArrayDesignRef>> list);
    }
}
