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

package uk.ac.ebi.fg.annotare2.web.server.services.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.junit.Test;
import uk.ac.ebi.fg.annotare2.services.efo.EfoTerm;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class EfoGraphTest {

    @Test
    public void test() {

        EfoTerm A = new EfoTerm("A", "A", Collections.<String>emptyList());
        EfoTerm B = new EfoTerm("B", "B", Collections.<String>emptyList());
        EfoTerm C = new EfoTerm("C", "C", Collections.<String>emptyList());
        EfoTerm D = new EfoTerm("D", "D", Collections.<String>emptyList());
        EfoTerm E = new EfoTerm("E", "E", Collections.<String>emptyList());

        SetMultimap<String, String> parents = HashMultimap.create();
        parents.putAll("B", asList("A"));
        parents.putAll("C", asList("A"));
        parents.putAll("D", asList("A", "B", "C"));
        parents.putAll("E", asList("A", "C"));

        Collection<EfoGraph.Node> roots = new EfoGraph(asList(A, B, C, D, E)).build(parents).getRoots();
        assertEquals(1, roots.size());

        EfoGraph.Node root = roots.iterator().next();
        assertEquals("A", root.getId());
        assertTrue(root.getParents().isEmpty());

        Set<EfoGraph.Node> childrenA = root.getChildren();
        assertEquals(2, childrenA.size());
        assertTrue(childrenA.contains(new EfoGraph.Node(B)));
        assertTrue(childrenA.contains(new EfoGraph.Node(C)));

        for (EfoGraph.Node node : childrenA) {
            assertEquals(1, node.getParents().size());
            assertEquals("A", node.getParents().iterator().next().getId());

            if ("B".equals(node.getId())) {
                assertEquals(1, node.getChildren().size());

                EfoGraph.Node nodeD = node.getChildren().iterator().next();
                assertEquals("D", nodeD.getId());
                assertEquals(2, nodeD.getParents().size());
                assertTrue(nodeD.getChildren().isEmpty());
            } else if ("C".equals(node.getId())) {
                Set<EfoGraph.Node> childrenC = node.getChildren();
                assertEquals(2, childrenC.size());
                assertTrue(childrenC.contains(new EfoGraph.Node(D)));
                assertTrue(childrenC.contains(new EfoGraph.Node(E)));

                Iterator<EfoGraph.Node> iter = childrenC.iterator();
                EfoGraph.Node nodeE = iter.next();
                if (!"E".equals(node.getId())) {
                    nodeE = iter.next();
                }
                assertEquals("E", nodeE.getId());
                assertEquals(1, nodeE.getParents().size());
                assertTrue(nodeE.getChildren().isEmpty());
            } else {
                fail("A has only two children: B and C");
            }
        }
    }

}

