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

package uk.ac.ebi.fg.annotare2.submission.transform;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;

import java.util.Date;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNotNull;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.ONE_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
public class SubmissionSerializationTest {

    @Test
    public void test() throws Exception {
        ExperimentProfile profileIn = new ExperimentProfile(ONE_COLOR_MICROARRAY);
        profileIn.setTitle(null);
        profileIn.setDescription("description");
        profileIn.setArrayDesign("AD-1234");
        profileIn.setAeExperimentType("AEExperimentType-123");
        profileIn.setExperimentDate(new Date());
        profileIn.setPublicReleaseDate(new Date());
        profileIn.setExperimentalDesigns(asList(new OntologyTerm("term1", "term1"), new OntologyTerm("term2", "term2")));
        String jsonString = JsonCodec.writeExperiment(profileIn);
        System.out.println(jsonString);
        assertNotNull(jsonString);

        ExperimentProfile profileOut = JsonCodec.readExperiment(jsonString);
        assertNotNull(profileOut.getExperimentDate());
    }
}
