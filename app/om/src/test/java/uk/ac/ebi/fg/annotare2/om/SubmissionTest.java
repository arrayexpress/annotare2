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

package uk.ac.ebi.fg.annotare2.om;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class SubmissionTest {

    @Test
    public void defaultInitializationTest() throws IOException {
        User user = new User(1, "email", "password");
        Acl acl = new Acl(1, AclType.SUBMISSION);
        ExperimentSubmission submission = new ExperimentSubmission(user, acl);

        assertEquals(0, submission.getId());
        assertNull(submission.getAccession());
        assertNull(submission.getTitle());

        assertNotNull(submission.getCreated());

        assertEquals("", CharStreams.toString(new InputStreamReader(submission.getInvestigation(), Charsets.UTF_8)));
    }
}
