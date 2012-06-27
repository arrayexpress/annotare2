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

package uk.ac.ebi.fg.annotare2.web.server.login.utils;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class RequestParamTest {

    @Test
    public void testEmptyParam() {
        final String paramName = "param";

        RequestParam param = RequestParam.from(mockRequest(paramName, null), paramName);
        assertTrue(param.isEmpty());
        assertEquals(paramName, param.getName());
        assertNull(param.getValue());
    }

    @Test
    public void testNonEmptyParam() {
        final String paramName = "param";
        final String[] paramValues = new String[]{"val1", "val2"};

        RequestParam param = RequestParam.from(mockRequest(paramName, paramValues), paramName);
        assertFalse(param.isEmpty());
        assertEquals(paramName, param.getName());
        assertEquals(paramValues[0], param.getValue());
    }

    private HttpServletRequest mockRequest(String paramName, String[] values) {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getParameterValues(paramName))
                .andReturn(values)
                .once();
        replay(request);
        return request;
    }
}
