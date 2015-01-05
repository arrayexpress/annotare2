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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.place;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Olga Melnichuk
 */
public class TokenReaderTest {

    @Test
    public void validOrderTest() throws TokenReaderException {
        String token = new TokenBuilder()
                .add("SomeString")
                .add(100)
                .add(false)
                .toString();

        TokenReader reader = new TokenReader(token);
        assertEquals("SomeString", reader.nextString());
        assertEquals(100, reader.nextInt());
        assertEquals(false, reader.nextBoolean());
    }

    @Test
    public void invalidOrderTest() throws TokenReaderException {
        String token = new TokenBuilder()
                .add("SomeString")
                .add(100)
                .add(false)
                .toString();

        TokenReader reader = new TokenReader(token);
        try {
            reader.nextInt();
            fail("An exception should be thrown because of non-integer value parsed");
        } catch (TokenReaderException e) {
            // OK
        }

        try {
            reader.nextBoolean();
            fail("An exception should be thrown because of non-boolean value parsed");
        } catch (TokenReaderException e) {
            // OK
        }

        reader.nextString();
        reader.nextString();
        reader.nextString();
    }
}
