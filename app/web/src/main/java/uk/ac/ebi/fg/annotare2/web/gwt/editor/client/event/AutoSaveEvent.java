/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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
public class AutoSaveEvent extends GwtEvent<AutoSaveEventHandler> {

    private static Type<AutoSaveEventHandler> TYPE = new Type<AutoSaveEventHandler>();

    private boolean isStart;

    private String errorMessage;

    private AutoSaveEvent() {
    }

    @Override
    protected void dispatch(AutoSaveEventHandler handler) {
        if (isStart) {
            handler.autoSaveStarted(this);
        } else if (errorMessage != null) {
            handler.autoSaveFailed(this);
        } else {
            handler.autoSaveStopped(this);
        }
    }

    @Override
    public Type<AutoSaveEventHandler> getAssociatedType() {
        return TYPE;
    }

    public static Type<AutoSaveEventHandler> getType() {
        return TYPE;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static AutoSaveEvent autoSaveStarted() {
        AutoSaveEvent event = new AutoSaveEvent();
        event.isStart = true;
        return event;
    }

    public static AutoSaveEvent autoSaveStopped(String errorMessage) {
        AutoSaveEvent event = new AutoSaveEvent();
        event.errorMessage = errorMessage;
        return event;
    }
}
