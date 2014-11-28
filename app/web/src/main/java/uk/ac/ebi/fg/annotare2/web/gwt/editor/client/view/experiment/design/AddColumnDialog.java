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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class AddColumnDialog<T> extends DialogBox {

    interface Binder extends UiBinder<Widget, AddColumnDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    private DialogCallback<T> callback;

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @UiField
    ListBox columnListBox;

    private Map<String, T> map = new HashMap<String, T>();

    public AddColumnDialog(DialogCallback<T> callback, Collection<T> values) {
        this.callback = callback;
        setModal(true);
        setGlassEnabled(true);
        setText("Add Column");

        setWidget(Binder.BINDER.createAndBindUi(this));

        for (T t : values) {
            columnListBox.addItem(t.toString());
            map.put(t.toString(), t);
        }

        center();
    }

    @UiHandler("okButton")
    void okButtonClicked(ClickEvent event) {
        T selection = getSelection();
        if (null == selection) {
            return;
        }

        hide();
        if (null != callback) {
            callback.onOkay(selection);
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

    private T getSelection() {
        int index = columnListBox.getSelectedIndex();
        return index >= 0 ? map.get(columnListBox.getValue(index)) : null;
    }
}
