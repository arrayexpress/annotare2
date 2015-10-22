/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.WaitingPopup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class SetupExpSubmissionView extends Composite implements SuggestService<ArrayDesignRef> {

    interface Binder extends UiBinder<Widget, SetupExpSubmissionView> {
        Binder BINDER = GWT.create(Binder.class);
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

    private final Map<ExperimentProfileType, HasSubmissionSettings> widgets = new HashMap<>();

    private final Set<String> arrayDesignAccessions = new HashSet<>();

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

        for (ExperimentProfileType type : ExperimentProfileType.values()) {
            templateBox.addItem(type.getTitle(), type.name());
        }

        templateBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showDetails(ExperimentProfileType.valueOf(getSelectedSettingsTemplate()));
            }
        });
        selectFirstTemplate(templateBox);
    }

    public void setArrayDesignList(List<ArrayDesignRef> arrayDesigns) {
        arrayDesignAccessions.clear();
        for (ArrayDesignRef ad : arrayDesigns) {
            arrayDesignAccessions.add(ad.getAccession().toLowerCase());
        }
    }

    @UiHandler("okButton")
    public void onOkButtonClick(ClickEvent event) {
        if (presenter == null) {
            return;
        }
        HasSubmissionSettings settings = (HasSubmissionSettings) templateDetails.getWidget();
        if (settings.areValid()) {
            okButton.setEnabled(false);
            final WaitingPopup w = new WaitingPopup();
            w.center();
            presenter.setupNewSubmission(settings.getSettings(),
                    new ReportingAsyncCallback<Void>(FailureMessage.UNABLE_TO_CREATE_SUBMISSION) {
                        @Override
                        public void onFailure(Throwable caught) {
                            w.hide();
                            super.onFailure(caught);
                        }

                        @Override
                        public void onSuccess(Void result) {
                            w.hide();
                            Window.Location.reload();
                        }
                    });
        }
    }

    @Override
    public void suggest(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> callback) {
        if (null != presenter) {
            presenter.getArrayDesigns(query, limit, callback);
        }
    }

    public boolean isArrayDesignPresent(String accession) {
        return (null != accession) && arrayDesignAccessions.contains(accession.toLowerCase());
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private String getSelectedSettingsTemplate() {
        return templateBox.getValue(templateBox.getSelectedIndex());
    }

    private void showDetails(ExperimentProfileType type) {
        HasSubmissionSettings w = widgets.get(type);
        if (w == null) {
            w = createWidget(type);
            widgets.put(type, w);
        }
        templateDetails.setWidget(w);
    }

    private HasSubmissionSettings createWidget(ExperimentProfileType type) {
        switch (type) {
            case ONE_COLOR_MICROARRAY:
                return new OneColorMicroarraySettings(this);
            case TWO_COLOR_MICROARRAY:
                return new TwoColorMicroarraySettings(this);
            case SEQUENCING:
                return new HighThroughputSeqSettings();
            default:
                throw new IllegalArgumentException("Unknown experiment type: " + type);
        }
    }

    private static void selectFirstTemplate(ListBox listBox) {
        listBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
    }

    public interface Presenter {

        void setupNewSubmission(ExperimentSetupSettings settings, AsyncCallback<Void> callback);

        void getArrayDesigns(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> callback);
    }
}
