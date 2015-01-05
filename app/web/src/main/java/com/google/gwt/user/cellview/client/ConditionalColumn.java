/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package com.google.gwt.user.cellview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public abstract class ConditionalColumn<T> extends Column<T, String> {

    private final SafeHtmlRenderer<String> renderer;

    public ConditionalColumn(Cell<String> cell) {
        super(cell);
        renderer = SimpleSafeHtmlRenderer.getInstance();
    }

    public abstract boolean isEditable(T object);


    @Override
    public void onBrowserEvent(Cell.Context context, Element elem, final T object, NativeEvent event) {
        if (isEditable(object)) {
            super.onBrowserEvent(context, elem, object, event);
        } else {
            event.preventDefault();
            event.stopPropagation();
        }
    }

    @Override
    public void render(Cell.Context context, T object, SafeHtmlBuilder sb) {
        if (isEditable(object)) {
            super.render(context, object, sb);
        } else {
            String value = getValue(object);
            if (value != null && value.trim().length() > 0) {

                sb.append(renderer.render(value));
            } else {
                sb.appendHtmlConstant("\u00A0");
            }
        }
    }

    public String getCellStyleNames(Cell.Context context, T object) {
        String styleNames = super.getCellStyleNames(context, object);
        if (!isEditable(object)) {
            return ((null != styleNames && !styleNames.isEmpty()) ? styleNames + " " : "") + "col-NoEdit";
        }
        return styleNames;
    }
}
