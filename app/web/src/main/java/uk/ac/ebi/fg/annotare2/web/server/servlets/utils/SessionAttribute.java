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

import javax.servlet.http.HttpSession;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Olga Melnichuk
 */
public class SessionAttribute {

    private final String name;

    public SessionAttribute(String name) {
        this.name = name;
    }

    public boolean exists(HttpSession session) {
        return session.getAttribute(name) != null;
    }

    public void set(HttpSession session, Object value) {
        checkNotNull(value);
        session.setAttribute(name, value);
    }

    public Object get(HttpSession session) {
        return session.getAttribute(name);
    }

    public void remove(HttpSession session) {
        session.removeAttribute(name);
    }
}
