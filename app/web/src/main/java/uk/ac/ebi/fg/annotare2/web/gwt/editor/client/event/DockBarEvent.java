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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Olga Melnichuk
 */
public class DockBarEvent extends GwtEvent<DockBarEventHandler> {

    private static Type<DockBarEventHandler> TYPE = new Type<DockBarEventHandler>();

    private boolean ensureOpen = false;

    private DockBarEvent() {
    }

    @Override
    public Type<DockBarEventHandler> getAssociatedType() {
        return TYPE;
    }

    public static Type<DockBarEventHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DockBarEventHandler handler) {
        if (ensureOpen) {
            handler.onOpenDockBar();
            return;
        }
        handler.onToggleDockBar();
    }

    public static DockBarEvent toggleDockBarEvent() {
        return new DockBarEvent();
    }

    public static DockBarEvent openDockBarEvent() {
        return new DockBarEvent().ensureOpen();
    }

    private DockBarEvent ensureOpen() {
        ensureOpen = true;
        return this;
    }
}
