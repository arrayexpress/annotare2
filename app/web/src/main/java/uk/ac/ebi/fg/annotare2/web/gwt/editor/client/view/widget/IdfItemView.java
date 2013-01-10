/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;


import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class IdfItemView<T> extends DisclosurePanelContent {

    private final ArrayList<EditableField<T, ?>> fields = new ArrayList<EditableField<T, ?>>();

    private final ArrayList<HasChangeableValue<?>> title = new ArrayList<HasChangeableValue<?>>();

    private final ChangeHandler titleChangeHandler = new ChangeHandler() {
        @Override
        public void onChange(ChangeEvent changeEvent) {
            fireTitleChangedEvent();
        }
    };

    private T item;

    protected void setItem(T item) {
        this.item = item;
    }

    protected void addField(final EditableField<T, ?> field) {
        fields.add(field);
        field.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                field.saveValueTo(item);
            }
        });
    }

    protected void addTitleField(HasChangeableValue<String> field) {
        title.add(field);
        field.addChangeHandler(titleChangeHandler);
    }

    protected void fireTitleChangedEvent() {
        StringBuilder sb = new StringBuilder();
        int i = title.size();
        for (HasChangeableValue<?> w : title) {
            String value = w.getValue().toString();
            sb.append(value).append(--i > 0 ? " " : "");
        }
        fireRecordChangeEvent(sb.toString());
    }

}
