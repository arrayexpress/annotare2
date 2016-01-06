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

package uk.ac.ebi.fg.annotare2.web.server.services.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static uk.ac.ebi.fg.annotare2.web.server.services.utils.DigestUtil.md5Hex;

/**
 * @author Olga Melnichuk
 */
public class DigestUtilTest {

    @Test
    public void testMd5Hex() {
        assertEquals("098f6bcd4621d373cade4e832627b4f6", md5Hex("test"));
    }
}
