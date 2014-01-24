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
public class DialogCloseEvent<T> extends GwtEvent<DialogCloseHandler<T>> {

    private static Type<DialogCloseHandler<?>> TYPE = new Type<DialogCloseHandler<?>>();

    private final T target;

    private final boolean hasResult;

    private DialogCloseEvent(T target, boolean hasResult) {
        this.target = target;
        this.hasResult = hasResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Type<DialogCloseHandler<T>> getAssociatedType() {
        return (Type)TYPE;
    }

    @Override
    protected void dispatch(DialogCloseHandler<T> handler) {
        handler.onDialogClose(this);
    }

    public static Type<DialogCloseHandler<?>> getType() {
        return TYPE;
    }

    public boolean hasResult() {
        return hasResult;
    }

    public T getTarget() {
        return target;
    }

    public static <T> void fire(HasDialogCloseHandlers<T> source, T target, boolean hasResult) {
        if (TYPE != null) {
            DialogCloseEvent<T> event = new DialogCloseEvent<T>(target, hasResult);
            source.fireEvent(event);
        }
    }
}
