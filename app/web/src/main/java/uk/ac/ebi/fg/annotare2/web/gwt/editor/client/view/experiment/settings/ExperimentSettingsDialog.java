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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSettingsDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, ExperimentSettingsDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @UiField
    SimplePanel content;

    private final Editor<ExperimentSettings> editor;
    private final DialogCallback<ExperimentSettings> callback;

    public ExperimentSettingsDialog(Editor<ExperimentSettings> editor,
                                    ExperimentSettings settings,
                                    DialogCallback<ExperimentSettings> callback) {
        this.editor = editor;
        this.callback = callback;

        setModal(true);
        setGlassEnabled(true);
        setText(settings.getExperimentType().getTitle() + " settings");

        setWidget(Binder.BINDER.createAndBindUi(this));
        content.setWidget(editor);

        center();
    }

    @UiHandler("cancelButton")
    void cancelClicked(ClickEvent event) {
        hide();
    }

    @UiHandler("okButton")
    void okClicked(ClickEvent event) {
        if (editor.areValuesValid()) {
            callback.onOkay(editor.getValues());
            hide();
        }
    }
}

