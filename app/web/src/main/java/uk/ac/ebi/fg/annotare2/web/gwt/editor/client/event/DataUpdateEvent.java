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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update.UpdateResult;

/**
 * @author Olga Melnichuk
 */
public class DataUpdateEvent extends GwtEvent<DataUpdateEventHandler> {

    private static Type<DataUpdateEventHandler> TYPE = new Type<DataUpdateEventHandler>();

    private final UpdateResult updates;

    private DataUpdateEvent(UpdateResult updates) {
        this.updates = updates;
    }

    @Override
    public Type<DataUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DataUpdateEventHandler handler) {
        handler.onDataUpdate(this);
    }

    public UpdateResult getUpdates() {
        return updates;
    }

    public static Type<DataUpdateEventHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, UpdateResult result) {
        source.fireEvent(new DataUpdateEvent(result));
    }
}
