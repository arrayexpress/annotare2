package com.google.gwt.user.client.ui;

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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;

public class PasteArea extends TextArea {

    public interface PasteEventHandler extends EventHandler {
        public void onEvent(PasteEvent event);
    }

    public static class PasteEvent extends GwtEvent<PasteEventHandler> {

        public static Type<PasteEventHandler> TYPE = new Type<PasteEventHandler>();

        private String paste;

        public PasteEvent(String paste) {
            this.paste = paste;
        }

        @Override
        public Type<PasteEventHandler> getAssociatedType() {
            return TYPE;
        }

        @Override
        protected void dispatch(PasteEventHandler handler) {
            handler.onEvent(this);
        }

        public String getData() {
            return paste;
        }

    }

    private final TextAreaElement element;

    private Element focusElement;

    public PasteArea() {
        this.element = this.getElement().cast();
        this.element.getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
        this.element.getStyle().setZIndex(100);
        this.element.getStyle().setLeft(-1000, com.google.gwt.dom.client.Style.Unit.PX);

        sinkEvents(Event.ONPASTE);

        this.focusElement = null;

        RootPanel.get().add(this);
    }

    public void intercept(Element focusElement) {
        this.element.focus();
        this.focusElement = focusElement;
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        switch (event.getTypeInt()) {
            case Event.ONPASTE:
                event.stopPropagation();
                fireDelayed();
                break;
        }
    }

    private void fireDelayed() {
        Timer t = new Timer() {
            public void run() {
                if (null != focusElement) {
                    focusElement.focus();
                    focusElement = null;
                }
                String s = getValue();
                fireEvent(new PasteEvent(s));
                setValue("");
            }
        };
        t.schedule(5);
    }

    public HandlerRegistration addPasteHandler(PasteEventHandler handler) {
        return addHandler(handler, PasteEvent.TYPE);
    }
}
