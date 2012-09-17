/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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
public class ImportFileEvent extends GwtEvent<ImportFileEventHandler> {

    public static Type<ImportFileEventHandler> TYPE = new Type<ImportFileEventHandler>();

    private String fileName;

    private boolean cancelled;

    @Override
    public Type<ImportFileEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ImportFileEventHandler handler) {
        if (cancelled) {
            handler.onCancel();
        } else {
            handler.onImport(fileName);
        }
    }

    public static ImportFileEvent importCancelled() {
        ImportFileEvent event = new ImportFileEvent();
        event.cancelled = true;
        return event;
    }

    public static ImportFileEvent importFile(String fileName) {
        ImportFileEvent event = new ImportFileEvent();
        event.fileName = fileName;
        return event;
    }
}
