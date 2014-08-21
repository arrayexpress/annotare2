/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ArrayDesignSuggestOracle;

/**
 * @author Olga Melnichuk
 */
public class OneColorMicroarraySettingsEditor extends Composite implements Editor<ExperimentSettings> {

    interface Binder extends UiBinder<Widget, OneColorMicroarraySettingsEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField(provided = true)
    SuggestBox arrayDesign;

    @UiField
    TextBox label;

    private final ExperimentSettingsPanel panel;

    public OneColorMicroarraySettingsEditor(ExperimentSettingsPanel panel) {
        this.panel = panel;
        this.arrayDesign = new SuggestBox(new ArrayDesignSuggestOracle(panel));
        initWidget(Binder.BINDER.createAndBindUi(this));
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
            validationErrors = " - a non-empty label must be used\n";
        }

        String ad = arrayDesign.getValue();
        if (null == ad || ad.isEmpty()) {
            validationErrors += " - a non-empty array design must be used\n";
        } else if (!panel.isArrayDesignPresent(ad)) {
            validationErrors += " - array design with accession '" + ad + "' must be available in ArrayExpress\n";
        }

        if (!validationErrors.isEmpty()) {
            Window.alert("Please correct the following:\n\n" + validationErrors);
            return false;
        }
        return true;
    }

    @Override
    public ExperimentSettings getValues() {
        ExperimentSettings settings = new ExperimentSettings(ExperimentProfileType.ONE_COLOR_MICROARRAY);
        settings.setArrayDesign(arrayDesign.getValue().trim());
        settings.setLabel(label.getValue().trim());
        return settings;
    }
}
