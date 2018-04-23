package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.PLANT_SEQUENCING;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.ValidationUtils.integerValuesOnly;

/**
 * Created by haideri on 09/10/2017.
 */
public class PlantHighThroughputSeqSettings extends Composite implements HasSubmissionSettings {

    @UiField
    TextBox numberOfSamples;

    interface Binder extends UiBinder<Widget, PlantHighThroughputSeqSettings> {
        PlantHighThroughputSeqSettings.Binder BINDER = GWT.create(PlantHighThroughputSeqSettings.Binder.class);
    }

    public PlantHighThroughputSeqSettings() {
        initWidget(PlantHighThroughputSeqSettings.Binder.BINDER.createAndBindUi(this));
        integerValuesOnly(numberOfSamples);
    }

    @Override
    public ExperimentSetupSettings getSettings() {
        ExperimentSetupSettings settings = new ExperimentSetupSettings(PLANT_SEQUENCING);
        settings.setNumberOfHybs(intValue(numberOfSamples.getValue()));
        return settings;
    }

    @Override
    public boolean areValid() {
        String validationErrors = "";
        if (0 == intValue(numberOfSamples.getValue())) {
            validationErrors += " - a number of samples must be greater than zero<br>";
        } else if (1000 < intValue(numberOfSamples.getValue())) {
            validationErrors += " - this submission does not support more than 1000 samples<br>";
        }
        if (!validationErrors.isEmpty()) {
            NotificationPopupPanel.error("Please correct the following:<br><br>" + validationErrors, false, false);
            return false;
        }
        return true;
    }


    private int intValue(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
