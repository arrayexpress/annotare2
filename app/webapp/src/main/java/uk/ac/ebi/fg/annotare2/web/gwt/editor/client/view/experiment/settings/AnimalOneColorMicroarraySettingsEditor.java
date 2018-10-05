package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ArrayDesignSuggestOracle;

public class AnimalOneColorMicroarraySettingsEditor extends Composite implements Editor<ExperimentSettings> {
    interface Binder extends UiBinder<Widget, AnimalOneColorMicroarraySettingsEditor> {
        AnimalOneColorMicroarraySettingsEditor.Binder BINDER = GWT.create(AnimalOneColorMicroarraySettingsEditor.Binder.class);
    }

    @UiField(provided = true)
    SuggestBox arrayDesign;

    @UiField
    TextBox label;

    private final ExperimentSettingsPanel panel;

    public AnimalOneColorMicroarraySettingsEditor(ExperimentSettingsPanel panel) {
        this.panel = panel;
        this.arrayDesign = new UpperCaseSuggestBox(new ArrayDesignSuggestOracle(panel));
        initWidget(AnimalOneColorMicroarraySettingsEditor.Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void setValues(ExperimentSettings experimentSettings) {
        String ad = experimentSettings.getArrayDesign();
        arrayDesign.setValue(ad == null ? "" : ad);

        String lbl = experimentSettings.getLabel();
        label.setValue(lbl == null ? "" : lbl);
    }

    @Override
    public boolean areValuesValid() {
        String validationErrors = "";
        if (null == label.getValue() || label.getValue().isEmpty()) {
            validationErrors = " - a non-empty label must be used<br>";
        }

        String ad = arrayDesign.getValue();
        if (null == ad || ad.isEmpty()) {
            validationErrors += " - a non-empty array design must be used<br>";
        } else if (!panel.isArrayDesignPresent(ad)) {
            validationErrors += " - array design with accession '" + ad + "' must be available in ArrayExpress<br>";
        }

        if (!validationErrors.isEmpty()) {
            NotificationPopupPanel.error("Please correct the following:<br><br>" + validationErrors, false, false);
            return false;
        }
        return true;
    }

    @Override
    public ExperimentSettings getValues() {
        ExperimentSettings settings = new ExperimentSettings(ExperimentProfileType.ANIMAL_ONE_COLOR_MICROARRAY);
        settings.setArrayDesign(arrayDesign.getValue().trim());
        settings.setLabel(label.getValue().trim());
        return settings;
    }
}
