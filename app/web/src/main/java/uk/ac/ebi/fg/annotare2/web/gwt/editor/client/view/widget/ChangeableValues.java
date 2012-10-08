/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextBoxBase;

/**
 * @author Olga Melnichuk
 */
public class ChangeableValues {
    public static HasChangeableValue<String> hasChangeableValue(final TextBoxBase box) {
        return new HasChangeableValue<String>() {
            @Override
            public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
                return box.addChangeHandler(changeHandler);
            }

            @Override
            public String getValue() {
                return box.getValue();
            }

            @Override
            public void setValue(String value) {
                box.setValue(value);
            }

            @Override
            public void setValue(String value, boolean fireEvents) {
                box.setValue(value, fireEvents);
            }

            @Override
            public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> stringValueChangeHandler) {
                return box.addValueChangeHandler(stringValueChangeHandler);
            }

            @Override
            public void fireEvent(GwtEvent<?> gwtEvent) {
                box.fireEvent(gwtEvent);
            }
        };
    }
}
