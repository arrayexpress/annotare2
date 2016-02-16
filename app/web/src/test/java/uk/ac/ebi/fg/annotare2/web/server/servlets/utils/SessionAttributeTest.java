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

import org.junit.Test;

import javax.servlet.http.HttpSession;

import static org.easymock.EasyMock.*;

/**
 * @author Olga Melnichuk
 */
public class SessionAttributeTest {

    @Test
    public void test() {
        final String name = "attr";
        final Object value = new Object();

        HttpSession session = createMock(HttpSession.class);
        session.setAttribute(name, value);
        expectLastCall().once();

        expect(session.getAttribute(name)).andReturn(value).once();

        session.removeAttribute(name);
        expectLastCall().once();

        replay(session);

        SessionAttribute attribute = new SessionAttribute(name);
        attribute.set(session, value);
        attribute.get(session);
        attribute.remove(session);
    }
}
