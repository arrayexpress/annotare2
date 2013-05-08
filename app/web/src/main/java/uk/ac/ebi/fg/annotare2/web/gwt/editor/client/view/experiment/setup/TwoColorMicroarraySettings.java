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

import static uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType.TWO_COLOR_MICROARRAY;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils.ValidationUtils.integerValuesOnly;

/**
 * @author Olga Melnichuk
 */
public class TwoColorMicroarraySettings extends Composite implements HasSubmissionSettings {

    @UiField
    HTML description;

    @UiField(provided = true)
    SuggestBox arrayDesignList;

    @UiField
    TextBox numberOfHybs;

    interface Binder extends UiBinder<Widget, TwoColorMicroarraySettings> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public TwoColorMicroarraySettings(SubmissionSettingsDataSource dataSource) {
        arrayDesignList = new SuggestBox(new ArrayDesignSuggestOracle(dataSource));
        initWidget(Binder.BINDER.createAndBindUi(this));
        description.setHTML(SafeHtmlUtils.fromSafeConstant(
                "An example is <a target='_blank' href='http://www.ebi.ac.uk/arrayexpress/experiments/E-MEXP-3237/'>" +
                        "E-MEXP-3237</a>, <a target='_blank' href='http://europepmc.org/abstract/MED/22432704'>" +
                        "Europe PMC 22432704</a>. " +
                        "A two colour experiment uses two dyes, normally Cy3 " +
                        "and Cy5. For two colour data one row in the  SDRF (Sample and Data " +
                        "Relationship Format) file is equal to one colour channel."));
        integerValuesOnly(numberOfHybs);
    }

    @Override
    public ExperimentSetupSettings getSettings() {
        return new ExperimentSetupSettings.Builder()
                .setExperimentType(TWO_COLOR_MICROARRAY)
                .setArrayDesign(arrayDesignList.getValue())
                .setNumberOfHybritisations(intValue(numberOfHybs.getValue()))
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
