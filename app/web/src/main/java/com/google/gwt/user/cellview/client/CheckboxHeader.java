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

package com.google.gwt.user.cellview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasValue;

public class CheckboxHeader extends Header<Boolean> implements HasValue<Boolean> {

    private boolean checked;
    private HandlerManager handlerManager;

    public CheckboxHeader() {
        super(new CheckboxCell());
        checked = false;
    }

    @Override
    public Boolean getValue() {
        return checked;
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element elem, NativeEvent nativeEvent) {
        int eventType = Event.as(nativeEvent).getTypeInt();
        if (eventType == Event.ONCHANGE) {
            nativeEvent.preventDefault();
            //use value setter to easily fire change event to handlers
            setValue(!checked, true);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return ensureHandlerManager().addHandler(ValueChangeEvent.getType(), handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        ensureHandlerManager().fireEvent(event);
    }

    @Override
    public void setValue(Boolean value) {
        checked = value;
    }

    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        checked = value;
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    private HandlerManager ensureHandlerManager() {
        if (handlerManager == null) {
            handlerManager = new HandlerManager(this);
        }
        return handlerManager;
    }
}
