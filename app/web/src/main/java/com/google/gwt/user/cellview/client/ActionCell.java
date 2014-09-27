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

package com.google.gwt.user.cellview.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

public abstract class ActionCell<T> extends AbstractCell<T> {

    private final String title;
    private final ImageResource imageResource;
    private static ImageResourceRenderer renderer;

    public ActionCell(String title, ImageResource imageResource) {
        super(CLICK, KEYDOWN);
        this.title = title;
        this.imageResource = imageResource;
        renderer = new ImageResourceRenderer();
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, T value,
                               NativeEvent event, ValueUpdater<T> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if (!isActivated(value) && CLICK.equals(event.getType())) {
            EventTarget eventTarget = event.getEventTarget();
            if (!Element.is(eventTarget)) {
                return;
            }
            if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
                // Ignore clicks that occur outside of the main element.
                onEnterKeyDown(context, parent, value, event, valueUpdater);
            }
        }
    }

    @Override
    public void render(Context context, T value, SafeHtmlBuilder sb) {
        if (isActivated(value)) {
            sb.append(renderer.render(imageResource));
        } else {
            sb.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\">");
            sb.appendEscaped(title);
            sb.appendHtmlConstant("</button>");
        }
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, T value,
                                  NativeEvent event, ValueUpdater<T> valueUpdater) {
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
    }

    public abstract boolean isActivated(T value);
}