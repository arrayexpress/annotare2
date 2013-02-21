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

package uk.ac.ebi.fg.annotare2.magetab.rowbased;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.magetab.rowbased.format.JseTextFormatter;
import uk.ac.ebi.fg.annotare2.magetab.table.Cell;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class AdfHeaderTest {

    static {
        JseTextFormatter.init();
    }

    @Test
    public void parseTest() throws IOException {

        AdfHeader adf = AdfHeaderParser.parse(AdfHeaderTest.class.getResourceAsStream("/A-MEXP-2196_part.adf.txt"));

        assertEquals("LSTM_An.gambiae_s.s._AGAM15K_V1.0", adf.getArrayDesignName().getValue());
        assertEquals("1.0", adf.getVersion().getValue());
        assertEquals("Sara Mitchell (snmitche@hsph.harvard.edu)", adf.getProvider().getValue());
        assertEquals("LSTM Agilent 15K array protocol: Arrays:&lt;br&gt;&lt;br&gt;" +
                "http://www.genomics.agilent.com/CollectionSubpage.aspx?PageType=Product&amp;SubPageType=ProductDetail&amp;PageID=1511" +
                "&lt;br&gt;&lt;br&gt;&lt;br&gt;&lt;br&gt;Labelling:&lt;br&gt;&lt;br&gt;" +
                "http://www.chem.agilent.com/en-US/Search/Library/_layouts/Agilent/PublicationSummary.aspx?whid=48835",
                adf.getPrintingProtocol().getValue());
        assertEquals("The entire coding transcriptome from An. gambiae s.s. Ensembl version AgamP3.5 (2009) was employed",
                adf.getDescription(false).getValue());
        assertEquals("2012-04-25", new JseTextFormatter().formatDate(adf.getArrayExpressReleaseDate(false).getValue()));


        Term technologyType = adf.getTechnologyType(false);
        assertNotNull(technologyType);
        assertEquals("in_situ_oligo_features", technologyType.getName().getValue());

        Term surfaceType = adf.getSurfaceType(false);
        assertNotNull(surfaceType);
        assertEquals("unknown_surface_type", surfaceType.getName().getValue());

        Term substrateType = adf.getSubstrateType(false);
        assertNotNull(substrateType);
        assertEquals("glass", substrateType.getName().getValue());

        assertNull(adf.getSequencePolymerType(false));

        List<Cell<String>> comments = adf.getComments("Description", false);
        assertEquals(1, comments.size());
        assertEquals("The entire coding transcriptome from An. gambiae s.s. Ensembl version AgamP3.5 (2009) was employed",
                comments.get(0).getValue());

        List<Cell<String>> dummyComments = adf.getComments("dummy", true);
        assertEquals(1, dummyComments.size());
        assertTrue(isNullOrEmpty(dummyComments.get(0).getValue()));

        List<Cell<String>> moreDummyComments = adf.getComments("more dummy", false);
        assertTrue(moreDummyComments.isEmpty());
    }

    @Test
    public void emptyTableTest() {
        Table table = new Table();
        AdfHeader adf = new AdfHeader(table);

        assertTrue(adf.getArrayDesignName().isEmpty());
        assertTrue(adf.getVersion().isEmpty());
        assertTrue(adf.getProvider().isEmpty());
        assertTrue(adf.getPrintingProtocol().isEmpty());

        assertNull(adf.getTechnologyType(false));
        assertNull(adf.getSurfaceType(false));
        assertNull(adf.getSubstrateType(false));
        assertNull(adf.getSequencePolymerType(false));
    }
}
