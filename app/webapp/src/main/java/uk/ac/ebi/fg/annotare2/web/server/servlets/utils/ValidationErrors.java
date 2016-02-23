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

package uk.ac.ebi.fg.annotare2.web.server.servlets.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.Serializable;
import java.util.Collection;

import static com.google.common.base.Joiner.on;

/**
 * @author Olga Melnichuk
 */
public class ValidationErrors implements Serializable {

    private final Multimap<String, String> errors = ArrayListMultimap.create();

    public void append(ValidationErrors moreErrors) {
        errors.putAll(moreErrors.errors);
    }

    public void append(String field, String message) {
        errors.put(field, message);
    }

    public void append(String message) {
        append("dummy", message);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public String getErrors() {
        return getErrors("dummy");
    }

    public String getErrors(String name) {
        Collection<String> err = errors.get(name);
        return err == null ? "" : on(". ").join(err);
    }

}
