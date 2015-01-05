/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class RemoveColumnsDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, RemoveColumnsDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField(provided = true)
    ListBox listBox;

    private final DialogCallback<List<Integer>> callback;

    public RemoveColumnsDialog(DialogCallback<List<Integer>> callback, List<String> columns) {
        this.callback = callback;

        listBox = new ListBox(true);
        for (String column : columns) {
            listBox.addItem(column);
        }

        setModal(true);
        setGlassEnabled(true);
        setText("Remove Columns");

        setWidget(Binder.BINDER.createAndBindUi(this));

        center();
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        List<Integer> selection = getSelection();
        if (!selection.isEmpty()) {
            hide();
            if (null != callback) {
                callback.onOkay(getSelection());
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

    private List<Integer> getSelection() {
        List<Integer> selectedValues = new ArrayList<Integer>();
        for (int i = 0, l = listBox.getItemCount(); i < l; i++) {
            if (listBox.isItemSelected(i)) {
                selectedValues.add(i);
            }
        }
        return selectedValues;
    }
}
