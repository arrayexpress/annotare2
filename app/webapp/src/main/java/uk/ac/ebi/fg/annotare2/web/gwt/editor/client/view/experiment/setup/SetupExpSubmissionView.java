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
    HTMLPanel techTypePanel;

    @UiField
    RadioGroup techType;

    @UiField
    HTMLPanel materialTypePanel;

    @UiField
    RadioGroup materialType;

    @UiField
    HTMLPanel expDesignInfo;

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

                nextButton.setVisible(false);
                techTypePanel.setVisible(false);
                materialTypePanel.setVisible(false);

                setDetails(techType,materialType);
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

                techTypePanel.setVisible(true);
                materialTypePanel.setVisible(true);
                nextButton.setVisible(true);

            }
        });

        templateDetails.setVisible(false);
        templateDetailsLabel.setVisible(false);
        experimentDesignPanel.setVisible(false);
        experimentDesignPanelLabel.setVisible(false);
    }

    private void setDetails(RadioGroup techType, RadioGroup materialType) {
        showDetails(getExperimentType(techType, materialType));
    }

    private ExperimentProfileType getExperimentType(RadioGroup techType, RadioGroup materialType){
        if(!materialType.getValue().equalsIgnoreCase("other")){
            String expTitle = materialType.getValue() + " - " +techType.getValue();
            return getExpProfileType(expTitle);
        }
        else {
            String expTitle = techType.getValue();
            return getExpProfileType(expTitle);
        }
    }

    private ExperimentProfileType getExpProfileType(String experimentType) {
        for (ExperimentProfileType expProfileType:
                ExperimentProfileType.values()) {
            if(expProfileType.getTitle().equalsIgnoreCase(experimentType)){
                return expProfileType;
            }
        }
        return null;
    }

    private void getExpDesigns()
    {
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
            final SelectableLabel<OntologyTerm> label = new SelectableLabel<>(term.getLabel(), term);
            label.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                @Override
                public void onSelectionChange(SelectionChangeEvent event) {
                    updateSelection(label.getValue(), label.isSelected());
                }
            });
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

    private void showDetails(ExperimentProfileType type) {
        HasSubmissionSettings w = createWidget(type);
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

            case PLANT_SEQUENCING:
                return new PlantHighThroughputSeqSettings();

            case PLANT_ONE_COLOR_MICROARRAY:
                return new PlantOneColorMicroarraySettings(this);

            case PLANT_TWO_COLOR_MICROARRAY:
                return new PlantTwoColorMicroarraySettings(this);

            case HUMAN_ONE_COLOR_MICROARRAY:
                return new HumanOneColorMicroarraySettings(this);

            case HUMAN_TWO_COLOR_MICROARRAY:
                return new HumanTwoColorMicroarraySettings(this);

            case HUMAN_SEQUENCING:
                return new HumanHighThroughputSeqSettings();

            case ANIMAL_ONE_COLOR_MICROARRAY:
                return new AnimalOneColorMicroarraySettings(this);

            case ANIMAL_TWO_COLOR_MICROARRAY:
                return new AnimalTwoColorMicroarraySettings(this);

            case ANIMAL_SEQUENCING:
                return new AnimalHighThroughputSeqSettings();

            case CELL_LINE_ONE_COLOR_MICROARRAY:
                return new CellLineOneColorMicroarraySettings(this);

            case CELL_LINE_TWO_COLOR_MICROARRAY:
                return new CellLineTwoColorMicroarraySettings(this);

            case CELL_LINE_SEQUENCING:
                return new CellLineHighThroughputSeqSettings();

            case SINGLE_CELL_HUMAN_SEQUENCING:
                return new SingleCellHumanHighThroughputSeqSettings();

            case SINGLE_CELL_PLANT_SEQUENCING:
                return new SingleCellPlantHighThroughputSeqSettings();

            case SINGLE_CELL_CELL_LINE_SEQUENCING:
                return new SCCellLineHighThroughputSeqSettings();

            case SINGLE_CELL_ANIMAL_SEQUENCING:
                return new SingleCellAnimalHighThroughputSeqSettings();

            case SINGLE_CELL_SEQUENCING:
                return new SingleCellHighThroughputSeqSettings();

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
    }
}
