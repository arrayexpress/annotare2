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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * @author Olga Melnichuk
 */
public class DataUpdateEvent<R> extends GwtEvent<DataUpdateEventHandler<R>> {

    private static Type<DataUpdateEventHandler<?>> TYPE = new Type<DataUpdateEventHandler<?>>();

    private final R updates;

    private DataUpdateEvent(R updates) {
        this.updates = updates;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Type<DataUpdateEventHandler<R>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(DataUpdateEventHandler<R> handler) {
        handler.onDataUpdate(this);
    }

    public R getUpdates() {
        return updates;
    }

    public static Type<DataUpdateEventHandler<?>> getType() {
        return TYPE;
    }

    public static <T> void fire(HasHandlers source, T result) {
        source.fireEvent(new DataUpdateEvent<T>(result));
    }
}
