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

package uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.ImageResourceRenderer;

/**
 * @author Olga Melnichuk
 */
@Deprecated
public class ClickableImageResourceCell extends AbstractCell<ImageResource> {

    private static ImageResourceRenderer renderer;

    interface Template extends SafeHtmlTemplates {
        @Template("<span style=\"cursor:pointer\">{0}</span>")
        SafeHtml span(SafeHtml content);
    }

    private static Template template;

    public ClickableImageResourceCell() {
        super("click", "keydown");

        if (renderer == null) {
            renderer = new ImageResourceRenderer();
        }

        if (template == null) {
            template = GWT.create(Template.class);
        }
    }

    @Override
    public void render(Context context, ImageResource value, SafeHtmlBuilder sb) {
        if (value != null) {
            sb.append(template.span(renderer.render(value)));
        }
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, ImageResource value,
                               NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        if ("click".equals(event.getType())) {
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
    protected void onEnterKeyDown(Cell.Context context, Element parent, ImageResource value,
                                  NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
    }

}
