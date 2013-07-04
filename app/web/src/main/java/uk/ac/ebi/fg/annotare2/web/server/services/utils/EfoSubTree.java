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
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import uk.ac.ebi.fg.annotare2.services.efo.EfoTerm;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
public class EfoSubTree {

    private final Map<String, EfoTerm> map = newHashMap();

    public EfoSubTree(Collection<EfoTerm> terms) {
        for (EfoTerm term : terms) {
            map.put(term.getAccession(), term);
        }
    }

    public EfoSubTree build(SetMultimap<String, String> parents) {
        List<String> sorted = sort(parents);
        //TODO
        return null;
    }

    /**
     * topological sort; for details see:
     * http://en.wikipedia.org/wiki/Topological_sorting
     *
     * @param parents a map: child -> [parent1, parent2,...]
     * @return ordered list of graph nodes
     */
    private List<String> sort(SetMultimap<String, String> parents) {
        List<String> sorted = newArrayList();
        Queue<String> current = new ArrayDeque<String>();
        Edges edges = new Edges(parents);
        current.addAll(edges.getRoots());


        while (!current.isEmpty()) {
            String node = current.poll();
            sorted.add(node);
            for (String child : edges.getChildren(node)) {
                edges.remove(node, child);
                if (!edges.incomingEdgesExist(child)) {
                    current.add(child);
                }
            }
        }
        // TODO if edges exist show error
        return sorted;
    }

    private static class Edges {

        private SetMultimap<String, String> parents;
        private SetMultimap<String, String> children;

        public Edges(SetMultimap<String, String> parentsMap) {
            parents = HashMultimap.create(parentsMap);
            children = HashMultimap.create();
            Multimaps.invertFrom(parents, children);
        }

        public Collection<String> getRoots() {
            List<String> roots = newArrayList();
            for (String node : parents.keySet()) {
                if (parents.get(node).isEmpty()) {
                    roots.add(node);
                }
            }
            return roots;
        }

        public Collection<String> getChildren(String node) {
            return newArrayList(children.get(node));
        }

        public void remove(String node, String child) {
            children.remove(node, child);
            for (String key : parents.keySet()) {
                 parents.remove(key, node);
            }
        }

        public boolean incomingEdgesExist(String child) {
            return parents.get(child).isEmpty();
        }
    }

}
