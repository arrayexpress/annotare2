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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.CellPreviewEvent;

public class PasteArea<T> extends FocusWidget implements CellPreviewEvent.Handler<T> {

    public interface PasteEventHandler extends EventHandler {
        public void onPaste(PasteEvent event);
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
            handler.onPaste(this);
        }

        public String getData() {
            return paste;
        }

    }

    private Element restoreFocusElement;

    private final boolean areWeRunningOnMac;

    private final TextAreaElement element;

    public PasteArea() {
        super(Document.get().createTextAreaElement());
        this.element = getElement().cast();
        this.element.getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
        this.element.getStyle().setZIndex(100);
        this.element.getStyle().setLeft(-1000, com.google.gwt.dom.client.Style.Unit.PX);

        this.areWeRunningOnMac = Window.Navigator.getPlatform().contains("Mac");

        sinkEvents(Event.ONPASTE);

        this.restoreFocusElement = null;

        RootPanel.get().add(this);
    }

    @Override
    public void onCellPreview(CellPreviewEvent<T> event) {
        if (!event.isCellEditing()) {
            NativeEvent e = event.getNativeEvent();
            if (BrowserEvents.KEYDOWN.equals(e.getType())) {
                if (86 == e.getKeyCode() && ((e.getMetaKey() && areWeRunningOnMac) || (e.getCtrlKey() && !areWeRunningOnMac))) {
                    intercept(Element.as(e.getEventTarget()));
                }
            }
        }
    }

    private void intercept(Element focusElement) {
        this.element.setValue("");
        this.element.focus();
        this.restoreFocusElement = focusElement;
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        switch (event.getTypeInt()) {
            case Event.ONPASTE:
                event.stopPropagation();
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (null != restoreFocusElement) {
                            restoreFocusElement.focus();
                            restoreFocusElement = null;
                        }
                        String s = element.getValue();
                        fireEvent(new PasteEvent(s));
                    }
                });
                break;
        }
    }

    public HandlerRegistration addPasteHandler(PasteEventHandler handler) {
        return addHandler(handler, PasteEvent.TYPE);
    }
}
