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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ArrayDesignRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ArrayDesignSuggestOracle;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.SuggestService;

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

    public OneColorMicroarraySettingsEditor(SuggestService<ArrayDesignRef> suggestService) {
        arrayDesign = new SuggestBox(new ArrayDesignSuggestOracle(suggestService));
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
    public ExperimentSettings getValues() {
        ExperimentSettings settings = new ExperimentSettings(ExperimentProfileType.ONE_COLOR_MICROARRAY);
        settings.setArrayDesign(arrayDesign.getValue().trim());
        settings.setLabel(label.getValue().trim());
        return settings;
    }
}
