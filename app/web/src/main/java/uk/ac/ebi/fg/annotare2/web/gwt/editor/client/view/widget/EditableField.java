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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author Olga Melnichuk
 */
public abstract class EditableField<T, S> {

    private final HasValue<S> field;

    protected EditableField(HasValue<S> field) {
        this.field = field;
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<S> changeHandler) {
        return field.addValueChangeHandler(changeHandler);
    }

    public void readValueFrom(T obj) {
        if (obj != null) {
            field.setValue(getValue(obj));
        }
    }

    public void saveValueTo(T obj) {
        if (obj != null) {
            setValue(obj, field.getValue());
        }
    }

    protected abstract S getValue(T obj);

    protected abstract void setValue(T obj, S value);
}