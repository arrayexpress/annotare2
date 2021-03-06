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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromSafeConstant;
import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromTrustedString;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSettingsPanel extends Composite implements SuggestService<ArrayDesignRef> {

    interface Templates extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<div>{0}</div>")
        SafeHtml div(SafeHtml value);
    }

    private static Templates templates = GWT.create(Templates.class);

    interface Binder extends UiBinder<Widget, ExperimentSettingsPanel> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    HTML summary;

    @UiField
    Anchor changeLink;

    private ExperimentSettings settings;
    private final Set<String> arrayDesignAccessions = new HashSet<String>();
    private Presenter presenter;

    public ExperimentSettingsPanel(ExperimentSettings settings) {
        this.settings = settings;

        if (settings == null) {
            initWidget(new SimplePanel());
        } else {
            initWidget(Binder.BINDER.createAndBindUi(this));
            update(settings);
        }
    }

    public void update(ExperimentSettings settings) {
        switch (settings.getExperimentType()) {
            case ONE_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()) +
                                "<br/> - label: " + valueOrNone(settings.getLabel()))));
                changeLink.setVisible(true);
                break;
            case HUMAN_ONE_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()) +
                                "<br/> - label: " + valueOrNone(settings.getLabel()))));
                changeLink.setVisible(true);
                break;
            case ANIMAL_ONE_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()) +
                                "<br/> - label: " + valueOrNone(settings.getLabel()))));
                changeLink.setVisible(true);
                break;
            case CELL_LINE_ONE_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()) +
                                "<br/> - label: " + valueOrNone(settings.getLabel()))));
                changeLink.setVisible(true);
                break;
            case PLANT_ONE_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()) +
                                "<br/> - label: " + valueOrNone(settings.getLabel()))));
                changeLink.setVisible(true);
                break;
            case TWO_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case METHYLATION_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case ANIMAL_METHYLATION_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case CELL_LINE_METHYLATION_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case HUMAN_METHYLATION_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case PLANT_METHYLATION_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case HUMAN_TWO_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case ANIMAL_TWO_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case CELL_LINE_TWO_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            case PLANT_TWO_COLOR_MICROARRAY:
                summary.setHTML(templates.div(fromTrustedString(
                        settings.getExperimentType().getTitle() +
                                "<br/> - array design: " + aeArrayLinkOrNone(settings.getArrayDesign()))));
                changeLink.setVisible(true);
                break;
            default:
                summary.setHTML(templates.div(fromSafeConstant(settings.getExperimentType().getTitle())));
                changeLink.setVisible(false);
                break;
        }
    }

    private static String valueOrNone(String value) {
        return value == null || value.isEmpty() ? "none" : value;
    }

    private static String aeArrayLinkOrNone(String value) {
        return value == null || value.isEmpty() ? "none" : "<a href=\"http://www.ebi.ac.uk/arrayexpress/arrays/" + value + "\" target=\"_blank\">" + value + "</a>";
    }

    private Editor<ExperimentSettings> createEditor(ExperimentSettings settings) {
        switch (settings.getExperimentType()) {
            case ONE_COLOR_MICROARRAY:
                return new OneColorMicroarraySettingsEditor(this);
            case PLANT_ONE_COLOR_MICROARRAY:
                return new PlantOneColorMicroarraySettingsEditor(this);
            case HUMAN_ONE_COLOR_MICROARRAY:
                return new HumanOneColorMicroarraySettingsEditor(this);
            case ANIMAL_ONE_COLOR_MICROARRAY:
                return new AnimalOneColorMicroarraySettingsEditor(this);
            case CELL_LINE_ONE_COLOR_MICROARRAY:
                return new CellLineOneColorMicroarraySettingsEditor(this);
            case TWO_COLOR_MICROARRAY:
                return new TwoColorMicroarraySettingsEditor(this);
            case PLANT_TWO_COLOR_MICROARRAY:
                return new PlantTwoColorMicroarraySettingsEditor(this);
            case HUMAN_TWO_COLOR_MICROARRAY:
                return new HumanTwoColorMicroarraySettingsEditor(this);
            case ANIMAL_TWO_COLOR_MICROARRAY:
                return new AnimalTwoColorMicroarraySettingsEditor(this);
            case CELL_LINE_TWO_COLOR_MICROARRAY:
                return new CellLineTwoColorMicroarraySettingsEditor(this);
            case METHYLATION_MICROARRAY:
                return new MethylationMicroarraySettingsEditor(this);
            case ANIMAL_METHYLATION_MICROARRAY:
                return new AnimalMethylationMicroarraySettingsEditor(this);
            case CELL_LINE_METHYLATION_MICROARRAY:
                return new CellLineMethylationMicroarraySettingsEditor(this);
            case HUMAN_METHYLATION_MICROARRAY:
                return new HumanMethylationMicroarraySettingsEditor(this);
            case PLANT_METHYLATION_MICROARRAY:
                return new PlantMethylationMicroarraySettingsEditor(this);
            default:
                return new DummySettingsEditor();
        }
    }

    @UiHandler("changeLink")
    void changeLinkClicked(ClickEvent event) {
        new ExperimentSettingsDialog(
                createEditor(settings),
                settings,
                new DialogCallback<ExperimentSettings>() {
                    @Override
                    public boolean onOk(ExperimentSettings settings) {
                        if (presenter != null) {
                            presenter.saveSettings(settings);
                        }
                        return true;
                    }
                }).show();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void suggest(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> callback) {
        if (presenter != null) {
            presenter.getArrayDesigns(query, limit, callback);
        }
    }

    public void setArrayDesignList(List<ArrayDesignRef> arrayDesigns) {
        arrayDesignAccessions.clear();
        for (ArrayDesignRef ad : arrayDesigns) {
            arrayDesignAccessions.add(ad.getAccession().toLowerCase());
        }
    }

    public boolean isArrayDesignPresent(String accession) {
        return (null != accession) && arrayDesignAccessions.contains(accession.toLowerCase());
    }

    public interface Presenter {

        void getArrayDesigns(String query, int limit, AsyncCallback<ArrayList<ArrayDesignRef>> callback);

        void saveSettings(ExperimentSettings settings);
    }
}
