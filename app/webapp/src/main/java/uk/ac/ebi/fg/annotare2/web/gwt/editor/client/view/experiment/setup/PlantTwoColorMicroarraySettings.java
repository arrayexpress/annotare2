package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ArrayDesignSuggestOracle;

import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.PLANT_TWO_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.ValidationUtils.integerValuesOnly;

/**
 * Created by haideri on 09/10/2017.
 */
public class PlantTwoColorMicroarraySettings extends Composite implements HasSubmissionSettings {

    @UiField(provided = true)
    SuggestBox arrayDesign;

    @UiField
    TextBox numberOfHybs;

    final SetupExpSubmissionView view;

    interface Binder extends UiBinder<Widget, PlantTwoColorMicroarraySettings> {
        PlantTwoColorMicroarraySettings.Binder BINDER = GWT.create(PlantTwoColorMicroarraySettings.Binder.class);
    }

    public PlantTwoColorMicroarraySettings(SetupExpSubmissionView view) {

        this.view = view;
        this.arrayDesign = new UpperCaseSuggestBox(new ArrayDesignSuggestOracle(view));
        initWidget(PlantTwoColorMicroarraySettings.Binder.BINDER.createAndBindUi(this));
        integerValuesOnly(numberOfHybs);
    }

    @Override
    public ExperimentSetupSettings getSettings() {
        ExperimentSetupSettings settings = new ExperimentSetupSettings(PLANT_TWO_COLOR_MICROARRAY);
        settings.setArrayDesign(arrayDesign.getValue());
        settings.setNumberOfHybs(intValue(numberOfHybs.getValue()));
        return settings;
    }


    @Override
    public boolean areValid() {
        String validationErrors = "";
        if (0 == intValue(numberOfHybs.getValue())) {
            validationErrors += " - a number of hybridizations must be greater than zero<br>";
        } else if (500 < intValue(numberOfHybs.getValue())) {
            validationErrors += " - this submission does not support more than 500 hybridizations<br>";
        }

        String ad = arrayDesign.getValue();
        if (null == ad || ad.isEmpty()) {
            validationErrors += " - a non-empty array design must be used<br>";
        } else if (!view.isArrayDesignPresent(ad)) {
            validationErrors += " - array design with accession '" + ad + "' must be available in ArrayExpress<br>";
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
