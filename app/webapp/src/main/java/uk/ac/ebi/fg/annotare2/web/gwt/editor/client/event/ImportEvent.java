/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Olga Melnichuk
 */
public class ImportEvent extends GwtEvent<ImportEventHandler> {

    private static Type<ImportEventHandler> TYPE = new Type<ImportEventHandler>();

    private AsyncCallback<Void> callback;

    private ImportEvent(AsyncCallback<Void> callback) {
        this.callback = callback;
    }

    @Override
    public Type<ImportEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ImportEventHandler handler) {
        handler.onImport(callback);
    }

    public static Type<ImportEventHandler> getType() {
        return TYPE;
    }

    public static void fire(HasHandlers source, AsyncCallback<Void> callback) {
        source.fireEvent(new ImportEvent(callback));
    }
}
