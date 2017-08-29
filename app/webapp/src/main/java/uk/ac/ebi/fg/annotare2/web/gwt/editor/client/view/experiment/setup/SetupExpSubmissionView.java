/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.SelectionChangeEvent;
import uk.ac.ebi.fg.annotare2.submission.model.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.WaitingPopup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.OntologyTermGroup;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDesignType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ContactUsDialog;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SelectableLabel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.*;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromSafeConstant;

/**
 * @author Olga Melnichuk
 */
public class SetupExpSubmissionView extends Composite implements SuggestService<ArrayDesignRef> {

    interface Binder extends UiBinder<Widget, SetupExpSubmissionView> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    SimplePanel templateDetails;

    @UiField
    ListBox templateBox;

    @UiField
    ScrollPanel experimentDesignPanel;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField
    Label templateDetailsLabel;

    @UiField
    Label experimentDesignPanelLabel;

    @UiField
    Button nextButton;

    @UiField
    Button prevButton;

    @UiField
    Label selectSubmissionTypeLabel;

    @UiField
    HTMLPanel oneColorDetails;

    @UiField
    HTMLPanel twoColorDetails;

    @UiField
    HTMLPanel highSeqDetails;

    @UiField
    HTMLPanel plantOneColorDetails;

    @UiField
    HTMLPanel plantTwoColorDetails;

    @UiField
    HTMLPanel plantHighSeqDetails;

    @UiField
    HTMLPanel warningMsg;

    @UiField
    HTMLPanel expDesignInfo;

    @UiField
    VerticalPanel expSetupPanel;

    @UiField
    VerticalPanel plantExpDetails;

    @UiField
    VerticalPanel genericExpDetails;

    private Presenter presenter;

    private final Map<ExperimentProfileType, HasSubmissionSettings> widgets = new HashMap<>();

    private final Set<String> arrayDesignAccessions = new HashSet<>();

    private List<OntologyTermGroup> experimentalDesigns;

    private List<OntologyTerm> experimentalDesignTerms;

    private Boolean oneClrDetail, twoClrDetail, highSeqDetail, plantDetail;

    public SetupExpSubmissionView() {
        this(null);
    }

    public SetupExpSubmissionView(ClickHandler cancelClick) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        experimentalDesigns = new ArrayList<>();
        experimentalDesignTerms = new ArrayList<>();

        if (cancelClick == null) {
            cancelButton.setVisible(false);
        } else {
            cancelButton.addClickHandler(cancelClick);
        }

        okButton.setVisible(false);
        nextButton.setVisible(true);
        prevButton.setVisible(false);
        expDesignInfo.setVisible(false);

        OptGroupElement nonPlantGroup = Document.get().createOptGroupElement();
        nonPlantGroup.setLabel("Non-Plant");
        OptGroupElement plantGroup = Document.get().createOptGroupElement();
        plantGroup.setLabel("Plant");
        templateBox.getElement().appendChild(nonPlantGroup);
        for (ExperimentProfileType type : ExperimentProfileType.values()) {
            OptionElement optElement = Document.get().createOptionElement();
            optElement.setInnerText(type.getTitle());
            optElement.setValue(type.name());
            if (type.name().toLowerCase().startsWith("plant")) {
                plantGroup.appendChild(optElement);
            } else {
                nonPlantGroup.appendChild(optElement);
            }
        }
        templateBox.getElement().appendChild(nonPlantGroup);
        templateBox.getElement().appendChild(plantGroup);

        templateBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                showDetails(ExperimentProfileType.valueOf(getSelectedSettingsTemplate()));
            }
        });
        selectFirstTemplate(templateBox);
        getExpDesigns();

        nextButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                templateDetails.setVisible(true);
                templateDetailsLabel.setVisible(true);
                experimentDesignPanel.setVisible(true);
                experimentDesignPanelLabel.setVisible(true);
                expDesignInfo.setVisible(true);
                prevButton.setVisible(true);
                okButton.setVisible(true);

                selectSubmissionTypeLabel.setVisible(false);
                warningMsg.setVisible(false);
                templateBox.setVisible(false);
                nextButton.setVisible(false);
                expSetupPanel.setVisible(false);
            }
        });

        prevButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                templateDetails.setVisible(false);
                templateDetailsLabel.setVisible(false);
                experimentDesignPanel.setVisible(false);
                experimentDesignPanelLabel.setVisible(false);
                expDesignInfo.setVisible(false);
                prevButton.setVisible(false);
                okButton.setVisible(false);

                selectSubmissionTypeLabel.setVisible(true);
                warningMsg.setVisible(true);
                expSetupPanel.setVisible(true);
                templateBox.setVisible(true);
                nextButton.setVisible(true);

            }
        });
        //getExpDesigns();

        /*Button btn = new Button();
        experimentDesignPanel.add(btn);
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                getExpDesigns();
            }
        });*/


        templateDetails.setVisible(false);
        templateDetailsLabel.setVisible(false);
        experimentDesignPanel.setVisible(false);
        experimentDesignPanelLabel.setVisible(false);
    }

    private void getExpDesigns()
    {
        /*presenter.getExperimentalDesigns(new ReportingAsyncCallback<List<OntologyTermGroup>>() {
            @Override
            public void onSuccess(List<OntologyTermGroup> ontologyTermGroups) {
                experimentDesignPanel.add(createContent(ontologyTermGroups));
            }
        });*/

        experimentDesignPanel.add(createContent());
    }

    private Widget createContent() {
        VerticalPanel stackPanel = new VerticalPanel();//Style.Unit.PX);
        stackPanel.setWidth("100%");
        for (ExperimentDesignType type : ExperimentDesignType.values()) {
            OntologyTerm term = new OntologyTerm(type.getAccession(),type.getLabel());
            stackPanel.add(createSectionContent(term));//, fromSafeConstant(term.getName()), 25);
        }
        return stackPanel;
    }

    private Widget createSectionContent(OntologyTerm term) {
       // HorizontalPanel panel = new HorizontalPanel();
        //panel.setWidth("100%");
        //panel.setSpacing(4);

            final SelectableLabel<OntologyTerm> label = new SelectableLabel<>(term.getLabel(), term);
            label.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                @Override
                public void onSelectionChange(SelectionChangeEvent event) {
                    updateSelection(label.getValue(), label.isSelected());
                }
            });
            //String definition = group.getDefinition(term);
            //if (definition != null && !definition.isEmpty()) {
            //    tooltip.attach(label.info(), definition);
            //}
          //  panel.add(label);

/*        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.add(panel);*/
        return label;
    }

    private void updateSelection(OntologyTerm term, boolean selected) {
        if (selected) {
            experimentalDesignTerms.add(term);
        } else if (experimentalDesignTerms.contains(term)) {
            experimentalDesignTerms.remove(term);
        }
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
            presenter.setupNewSubmission(settings.getSettings(),experimentalDesignTerms,
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
        HasSubmissionSettings w = createWidget(type);
        templateDetails.setWidget(w);
    }

    private HasSubmissionSettings createWidget(ExperimentProfileType type) {
        switch (type) {
            case ONE_COLOR_MICROARRAY:
                genericExpDetails.setVisible(true);
                twoColorDetails.setVisible(false);
                highSeqDetails.setVisible(false);
                oneColorDetails.setVisible(true);
                plantExpDetails.setVisible(false);
                return new OneColorMicroarraySettings(this);

            case TWO_COLOR_MICROARRAY:
                genericExpDetails.setVisible(true);
                twoColorDetails.setVisible(true);
                oneColorDetails.setVisible(false);
                highSeqDetails.setVisible(false);
                plantExpDetails.setVisible(false);
                return new TwoColorMicroarraySettings(this);

            case SEQUENCING:
                genericExpDetails.setVisible(true);
                twoColorDetails.setVisible(false);
                oneColorDetails.setVisible(false);
                highSeqDetails.setVisible(true);
                plantExpDetails.setVisible(false);
                return new HighThroughputSeqSettings();

            case PLANT_SEQUENCING:
                genericExpDetails.setVisible(false);
                plantExpDetails.setVisible(true);
                plantTwoColorDetails.setVisible(false);
                plantHighSeqDetails.setVisible(true);
                plantOneColorDetails.setVisible(false);
                return new HighThroughputSeqSettings();

            case PLANT_ONE_COLOR_MICROARRAY:
                genericExpDetails.setVisible(false);
                plantExpDetails.setVisible(true);
                plantTwoColorDetails.setVisible(false);
                plantHighSeqDetails.setVisible(false);
                plantOneColorDetails.setVisible(true);
                return new OneColorMicroarraySettings(this);

            case PLANT_TWO_COLOR_MICROARRAY:
                genericExpDetails.setVisible(false);
                plantExpDetails.setVisible(true);
                plantTwoColorDetails.setVisible(true);
                plantHighSeqDetails.setVisible(false);
                plantOneColorDetails.setVisible(false);
                return new TwoColorMicroarraySettings(this);

            default:
                throw new IllegalArgumentException("Unknown experiment type: " + type);
        }
    }

    private static void selectFirstTemplate(ListBox listBox) {
        listBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
    }

    public interface Presenter extends ContactUsDialog.Presenter {

        void setupNewSubmission(ExperimentSetupSettings settings, List<OntologyTerm> experimentDesigns, AsyncCallback<Void> callback);

        void getArrayDesigns(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> callback);

        //void setExperimentalDesigns(List<OntologyTerm> experimentalDesigns); // needed if want to get experiment design terms from EFO
        // currently its a hard coded list for setup screen
    }
}
