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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;

import java.util.ArrayList;
import java.util.List;

public class ImportValuesDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, ImportValuesDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextArea values;

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    private final DialogCallback<List<String>> callback;

    public ImportValuesDialog(DialogCallback<List<String>> callback) {
        this.callback = callback;

        setModal(true);
        setGlassEnabled(true);
        setText("Paste into Column");

        setWidget(Binder.BINDER.createAndBindUi(this));

        center();
    }

    @Override
    public void show() {
        super.show();

        Scheduler.get().scheduleDeferred(new Command() {
            public void execute() {
                values.setFocus(true);
            }
        });
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        List<String> importedValues = getImportedValues();
        if (!importedValues.isEmpty() && null != callback) {
            if (callback.onOk(importedValues)) {
                hide();
            }
        }
    }

    @UiHandler("cancelButton")
    void cancelButtonClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onCancel();
        }
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                hide();
                if (null != callback) {
                    callback.onCancel();
                }
            }
        }
    }

    private List<String> getImportedValues() {
        String pastedRows = values.getValue();
        List<String> result = new ArrayList<String>();
        if (null != pastedRows && !pastedRows.isEmpty()) {
            for (String row : pastedRows.split("\\r\\n|[\\r\\n]")) {
                String[] cols = row.split("\\t");
                result.add(cols.length > 0 ?
                        cols[0].replaceAll("^[\"]([^\"]*)[\"]$", "$1").trim() :
                        ""
                );
            }
        }
        return result;
    }
}
