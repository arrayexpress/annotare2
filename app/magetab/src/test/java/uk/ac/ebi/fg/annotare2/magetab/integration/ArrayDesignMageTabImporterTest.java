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

package uk.ac.ebi.fg.annotare2.magetab.integration;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.configmodel.ArrayDesignHeader;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.configmodel.JsonCodec;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.AdfHeader;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.AdfParser;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.format.JseTextFormatter;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.Closeables.close;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignMageTabImporterTest {

    static {
        JseTextFormatter.init();
    }

    @Test
    public void mageTab2ArrayDesignHeaderTest() throws IOException, DataSerializationException {
        ArrayDesignHeader header = createArrayDesignHeader("/A-MEXP-2196.adf.header.txt");
        assertNotNull(header);
        assertEquals("LSTM_An.gambiae_s.s._AGAM15K_V1.0", header.getName());
    }

    private ArrayDesignHeader createArrayDesignHeader(String file) throws IOException, DataSerializationException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(file);
            AdfHeader adHeader = new AdfHeader(new AdfParser().parseHeader(in));
            ArrayDesignHeader header = new ArrayDesignMageTabImporter().importFrom(adHeader);
            System.out.println(JsonCodec.toJsonString(header));
            return header;
        } finally {
            close(in, true);
        }
    }
}
