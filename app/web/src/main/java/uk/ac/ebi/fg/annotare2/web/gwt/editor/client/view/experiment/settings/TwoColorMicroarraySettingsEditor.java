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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ArrayDesignSuggestOracle;

/**
 * @author Olga Melnichuk
 */
public class TwoColorMicroarraySettingsEditor extends Composite implements Editor<ExperimentSettings> {

    interface Binder extends UiBinder<Widget, TwoColorMicroarraySettingsEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField(provided = true)
    SuggestBox arrayDesign;

    private final ExperimentSettingsPanel panel;

    public TwoColorMicroarraySettingsEditor(ExperimentSettingsPanel panel) {
        this.panel = panel;
        this.arrayDesign = new SuggestBox(new ArrayDesignSuggestOracle(panel));
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    @Override
    public void setValues(ExperimentSettings experimentSettings) {
        String ad = experimentSettings.getArrayDesign();
        arrayDesign.setValue(ad == null ? "" : ad);
    }

    @Override
    public boolean areValuesValid() {
        String validationErrors = "";

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
        ExperimentSettings settings = new ExperimentSettings(ExperimentProfileType.TWO_COLOR_MICROARRAY);
        settings.setArrayDesign(arrayDesign.getValue().trim());
        return settings;
    }
}
