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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author Olga Melnichuk
 */
public class ListBoxValueIndex implements HasValue<Integer> {

    private final ListBox box;
    private final HandlerManager handlerManager;

    public ListBoxValueIndex(ListBox box) {
        this.box = box;
        this.handlerManager = new HandlerManager(this);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Integer> handler) {
        final HandlerRegistration changeHandler = box.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                ValueChangeEvent.fire(ListBoxValueIndex.this, getValue());
            }
        });

        final HandlerRegistration valueChangeHandler = handlerManager.addHandler(ValueChangeEvent.getType(), handler);

        return new HandlerRegistration() {
            @Override
            public void removeHandler() {
                changeHandler.removeHandler();
                valueChangeHandler.removeHandler();
            }
        };
    }

    @Override
    public Integer getValue() {
        return box.getSelectedIndex();
    }

    @Override
    public void setValue(Integer value, boolean fireEvents) {
        box.setSelectedIndex(value);
        if (fireEvents) {
            DomEvent.fireNativeEvent(Document.get().createChangeEvent(), box);
        }
    }

    @Override
    public void setValue(Integer value) {
        setValue(value, false);
    }

    @Override
    public void fireEvent(GwtEvent<?> gwtEvent) {
        handlerManager.fireEvent(gwtEvent);
    }
}
