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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.servlets.utils;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class RequestParam {

    private final String name;
    private final String[] values;

    public RequestParam(String name, String[] values) {
        this.name = name;
        this.values = values;
    }

    public static RequestParam from(HttpServletRequest request, String name) {
        return new RequestParam(name, trim(request.getParameterValues(name)));
    }

    public boolean isEmpty() {
        return values == null || values.length == 0 || isNullOrEmpty(on("").join(values));
    }

    public String getValue() {
        return values == null || values.length == 0 ? null : values[0];
    }

    public String getName() {
        return name;
    }

    private static String[] trim(String[] values) {
        if (values == null) {
            return values;
        }

        String[] arr = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            arr[i] = values[i].trim();
        }
        return arr;
    }
}
