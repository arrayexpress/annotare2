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

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class AdfHeaderTest {

    @Test
    public void parseTest() throws IOException {

        AdfHeader adf = new AdfHeader(new AdfParser().parseHeader(AdfHeaderTest.class.getResourceAsStream("/A-MEXP-2196_part.adf.txt")));

        assertEquals("LSTM_An.gambiae_s.s._AGAM15K_V1.0", adf.getArrayDesignName());
        assertEquals("1.0", adf.getVersion());
        assertEquals("Sara Mitchell (snmitche@hsph.harvard.edu)", adf.getProvider());
        assertEquals("LSTM Agilent 15K array protocol: Arrays:&lt;br&gt;&lt;br&gt;" +
                "http://www.genomics.agilent.com/CollectionSubpage.aspx?PageType=Product&amp;SubPageType=ProductDetail&amp;PageID=1511" +
                "&lt;br&gt;&lt;br&gt;&lt;br&gt;&lt;br&gt;Labelling:&lt;br&gt;&lt;br&gt;" +
                "http://www.chem.agilent.com/en-US/Search/Library/_layouts/Agilent/PublicationSummary.aspx?whid=48835",
                adf.getPrintingProtocol());
        assertEquals("The entire coding transcriptome from An. gambiae s.s. Ensembl version AgamP3.5 (2009) was employed",
                adf.getDescription());
        assertEquals("2012-04-25", formatDate(adf.getArrayExpressReleaseDate()));


        Term technologyType = adf.getTechnologyType();
        assertNotNull(technologyType);
        assertEquals("in_situ_oligo_features", technologyType.getName());

        Term surfaceType = adf.getSurfaceType();
        assertNotNull(surfaceType);
        assertEquals("unknown_surface_type", surfaceType.getName());

        Term substrateType = adf.getSubstrateType();
        assertNotNull(substrateType);
        assertEquals("glass", substrateType.getName());

        assertNull(adf.getSequencePolymerType());

        List<String> comments = adf.getComments("Description", false);
        assertEquals(1, comments.size());
        assertEquals("The entire coding transcriptome from An. gambiae s.s. Ensembl version AgamP3.5 (2009) was employed",
                comments.get(0));

        List<String> dummyComments = adf.getComments("dummy", true);
        assertEquals(1, dummyComments.size());
        assertTrue(isNullOrEmpty(dummyComments.get(0)));

        List<String> moreDummyComments = adf.getComments("more dummy", false);
        assertTrue(moreDummyComments.isEmpty());
    }

    private String formatDate(Date date) {
        return AdfHeader.DATE_FORMAT.format(date);
    }

    @Test
    public void emptyTableTest() {
        Table table = new Table();
        AdfHeader adf = new AdfHeader(table);

        assertNull(adf.getArrayDesignName());
        assertNull(adf.getVersion());
        assertNull(adf.getProvider());
        assertNull(adf.getPrintingProtocol());

        assertNull(adf.getTechnologyType());
        assertNull(adf.getSurfaceType());
        assertNull(adf.getSubstrateType());
        assertNull(adf.getSequencePolymerType());
    }
}
