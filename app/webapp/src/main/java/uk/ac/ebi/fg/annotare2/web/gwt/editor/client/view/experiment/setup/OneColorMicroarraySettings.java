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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ArrayDesignSuggestOracle;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromSafeConstant;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.ONE_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.ValidationUtils.integerValuesOnly;

/**
 * @author Olga Melnichuk
 */
public class OneColorMicroarraySettings extends Composite implements HasSubmissionSettings {

    @UiField
    HTML description;

    @UiField(provided = true)
    SuggestBox arrayDesign;

    @UiField
    TextBox numberOfHybs;

    @UiField
    TextBox label;

    final SetupExpSubmissionView view;

    interface Binder extends UiBinder<Widget, OneColorMicroarraySettings> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public OneColorMicroarraySettings(SetupExpSubmissionView view) {
        this.view = view;
        this.arrayDesign = new UpperCaseSuggestBox(new ArrayDesignSuggestOracle(view));
        initWidget(Binder.BINDER.createAndBindUi(this));
        this.description.setHTML(fromSafeConstant(
                "<p>One hybridization is where one labeled sample is hybridized on an array chip</p>" +
                        "<p>An example is <a target='_blank' href='http://www.ebi.ac.uk/arrayexpress/experiments/E-MTAB-641/'>" +
                        "E-MTAB-641</a>, <a target='_blank' href='http://europepmc.org/abstract/MED/21980142'>" +
                        "Europe PMC 21980142</a>. A one colour experiment uses one dye or label. For " +
                        "example experiments using an Affymetrix array use the label biotin. For " +
                        "one colour data one row in the SDRF (Sample and Data Relationship " +
                        "Format) file is equal to one assay.</p>"
        ));
        integerValuesOnly(numberOfHybs);
    }

    @Override
    public ExperimentSetupSettings getSettings() {
        ExperimentSetupSettings settings = new ExperimentSetupSettings(ONE_COLOR_MICROARRAY);
        settings.setArrayDesign(arrayDesign.getValue());
        settings.setNumberOfHybs(intValue(numberOfHybs.getValue()));
        settings.setLabel(label.getValue());
        return settings;
    }

    @Override
    public boolean areValid() {
        String validationErrors = "";
        if (null == label.getValue() || label.getValue().isEmpty()) {
            validationErrors = " - a non-empty label must be used<br>";
        }
        if (0 == intValue(numberOfHybs.getValue())) {
            validationErrors += " - a number of hybridizations must be greater than zero<br>";
        } else if (1000 < intValue(numberOfHybs.getValue())) {
            validationErrors += " - this submission does not support more than a 1000 hybridizations<br>";
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
