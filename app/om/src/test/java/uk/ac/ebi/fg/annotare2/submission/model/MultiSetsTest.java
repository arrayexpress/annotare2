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

package uk.ac.ebi.fg.annotare2.submission.model;

import org.junit.Test;

import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class MultiSetsTest {

    @Test
    public void returnEmptyCollectionForNonExistedKey() {
        MultiSets<Integer, Integer> test = new MultiSets<Integer, Integer>();
        assertNotNull(test.get(0));
        assertTrue(test.get(0).isEmpty());
    }

    @Test
    public void elementsCanBeAddedByOneOrByChunk() {
        MultiSets<Integer, Integer> test = new MultiSets<Integer, Integer>();
        test.put(0, 1);
        assertEquals(1, test.get(0).size());

        test.put(0, 2);
        assertEquals(2, test.get(0).size());

        test.putAll(0, asList(3, 4));
        assertEquals(4, test.get(0).size());
    }

    @Test
    public void sameElementsAddedOnlyOnce() {
        MultiSets<Integer, Integer> test = new MultiSets<Integer, Integer>();
        test.put(0, 1);
        test.put(0, 1);
        assertEquals(1, test.get(0).size());
    }

    @Test
    public void elementsCanBeRemovedByKey() {
        MultiSets<Integer, Integer> test = new MultiSets<Integer, Integer>();
        test.put(0, 1);
        assertEquals(1, test.get(0).size());

        Set<Integer>removed = test.remove(0);
        assertTrue(test.get(0).isEmpty());
        assertEquals(1, removed.size());
    }
}
