package com.google.gwt.user.cellview.client;

/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;

public abstract class ConditonalColumn<T, C> extends Column<T, C> {

    public ConditonalColumn(Cell<C> cell) {
        super(cell);
    }

    public abstract boolean isEditable(T object);

    public void onBrowserEvent(Cell.Context context, Element elem, final T object, NativeEvent event) {
        if (isEditable(object)) {
            super.onBrowserEvent(context, elem, object, event);
        }
    }

    public String getCellStyleNames(Cell.Context context, T object) {
        String styleNames = super.getCellStyleNames(context, object);
        if (!isEditable(object)) {
            return ((null != styleNames && !styleNames.isEmpty()) ? styleNames + " " : "") + "non-editable";
        }
        return styleNames;
    }
}
