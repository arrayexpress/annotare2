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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ArrayDesignSuggestOracle;

import static uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType.ONE_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.ValidationUtils.integerValuesOnly;

/**
 * @author Olga Melnichuk
 */
public class OneColorMicroarraySettings extends Composite implements HasSubmissionSettings {

    @UiField
    HTML description;

    @UiField(provided = true)
    SuggestBox arrayDesignList;

    @UiField
    TextBox numberOfHybs;

    @UiField
    TextBox label;

    interface Binder extends UiBinder<Widget, OneColorMicroarraySettings> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public OneColorMicroarraySettings(SubmissionSettingsDataSource dataSource) {
        arrayDesignList = new SuggestBox(new ArrayDesignSuggestOracle(dataSource));
        initWidget(Binder.BINDER.createAndBindUi(this));
        description.setHTML(SafeHtmlUtils.fromSafeConstant(
                "An example is <a target='_blank' href='http://www.ebi.ac.uk/arrayexpress/experiments/E-MTAB-641/'>" +
                        "E-MTAB-641</a>, <a target='_blank' href='http://europepmc.org/abstract/MED/21980142'>" +
                        "Europe PMC 21980142</a>. A one colour experiment uses one dye or label. For " +
                        "example experiments using an Affymetrix array use the label biotin. For " +
                        "one colour data one row in the SDRF (Sample and Data Relationship " +
                        "Format) file is equal to one assay."
        ));
        integerValuesOnly(numberOfHybs);
    }

    @Override
    public ExperimentSetupSettings getSettings() {
        return new ExperimentSetupSettings.Builder()
                .setExperimentType(ONE_COLOR_MICROARRAY)
                .setArrayDesign(arrayDesignList.getValue())
                .setNumberOfHybritisations(intValue(numberOfHybs.getValue()))
                .setLabel(label.getValue())
                .build();
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
