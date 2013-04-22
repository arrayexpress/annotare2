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

package uk.ac.ebi.fg.annotare2.submissionmodel;

import com.google.common.collect.Maps;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSerializationTest {

    @Test
    public void emptyExperimentTest() throws DataSerializationExcepetion {
        Experiment exp1 = new Experiment(Maps.<String, String>newHashMap());
        Experiment exp2 = Experiment.fromJsonString(exp1.toJsonString());
        assertTrue(exp2.getSamples().isEmpty());
        assertTrue(exp2.getExtracts().isEmpty());
        assertTrue(exp2.getLabeledExtracts().isEmpty());
        assertTrue(exp2.getAssays().isEmpty());
        assertTrue(exp2.getArrayDataFiles().isEmpty());
    }
}
