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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public abstract class DynamicList<T> {

    private final List<ChangeHandler> handlers = new ArrayList<ChangeHandler>();

    private List<T> values = null;

    public void fireUpdate() {
        values = loadValues();
        for (ChangeHandler ch : handlers) {
            ch.onChange();
        }
    }

    public void addChangeHandler(ChangeHandler handler) {
        handlers.add(handler);
    }

    protected abstract List<T> loadValues();

    public List<T> getValues() {
        if (values == null) {
            values = new ArrayList<T>();
            values.addAll(loadValues());
        }
        return values;
    }

    public interface ChangeHandler {
        void onChange();
    }
}
