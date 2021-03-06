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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @author Olga Melnichuk
 */
public class RichTextAreaExtended extends RichTextArea implements HasValueChangeHandlers<String>, HasValue<String> {

    private boolean hasHandlers;

    @Override
    public String getValue() {
        return getHTML();
    }

    @Override
    public void setValue(String value) {
        setValue(value, true);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        setHTML(value);
        if (fireEvents) {
            fireValueChangeEvent();
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        if (!hasHandlers) {
             addFocusHandler(new FocusHandler() {
                 @Override
                 public void onFocus(FocusEvent event) {
                     fireValueChangeEvent();
                 }
             });
            addBlurHandler(new BlurHandler() {
                @Override
                public void onBlur(BlurEvent event) {
                    fireValueChangeEvent();
                }
            });
            addKeyDownHandler(new KeyDownHandler() {
                @Override
                public void onKeyDown(KeyDownEvent event) {
                    fireValueChangeEvent();
                }
            });
            hasHandlers = true;
        }
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void fireValueChangeEvent() {
        ValueChangeEvent.fire(this, getValue());
    }

    private boolean enabled;
    private boolean firstFocus = true;

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(enabled);
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getType().equals("focus") && firstFocus) {
            firstFocus = false;
            setEnabled(enabled);
        }
    }
}
