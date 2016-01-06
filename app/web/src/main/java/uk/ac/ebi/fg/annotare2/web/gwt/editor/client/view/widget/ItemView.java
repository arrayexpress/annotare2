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

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ItemView<T> extends DisclosurePanelContent {

    private final List<EditableField<T, ?>> fields = new ArrayList<EditableField<T, ?>>();

    private final List<HasValue<?>> header = new ArrayList<HasValue<?>>();

    private final ValueChangeHandler<String> headerChangeHandler = new ValueChangeHandler<String>() {
        @Override
        public void onValueChange(ValueChangeEvent event) {
            fireHeaderChangeEvent();
        }
    };

    private T item;

    public void setItem(T item) {
        this.item = item;
        for (EditableField<T, ?> f : fields) {
            f.readValueFrom(item);
        }
        fireHeaderChangeEvent();
    }

    protected T getItem() {
        return item;
    }

    protected <S> void addField(final EditableField<T, S> field) {
        fields.add(field);
        field.addValueChangeHandler(new ValueChangeHandler<S>() {
            @Override
            public void onValueChange(ValueChangeEvent<S> event) {
                field.saveValueTo(item);
                fireItemChangeEvent();
            }
        });
    }

    protected void addHeaderField(HasValue<String> field) {
        header.add(field);
        field.addValueChangeHandler(headerChangeHandler);
    }

    private void fireHeaderChangeEvent() {
        fireItemHeaderChangeEvent(getHeaderText());
    }

    @Override
    public String getHeaderText() {
        StringBuilder sb = new StringBuilder();
        int i = header.size();
        for (HasValue<?> w : header) {
            String value = w.getValue().toString();
            sb.append(value).append(--i > 0 ? " " : "");
        }
        return sb.toString();
    }
}
