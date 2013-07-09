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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import uk.ac.ebi.fg.annotare2.services.efo.EfoTerm;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.intersection;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSet;

/**
 * @author Olga Melnichuk
 */
public class EfoGraph {

    private final Map<String, EfoTerm> map = newHashMap();
    private final List<Node> roots = newArrayList();

    public EfoGraph(Collection<EfoTerm> terms) {
        for (EfoTerm term : terms) {
            map.put(term.getAccession(), term);
        }
    }

    public EfoGraph(List<Node> roots) {
       this.roots.addAll(roots);
    }

    public EfoGraph build(SetMultimap<String, String> parents) {
        if (map.isEmpty()) {
            return this;
        }

        List<String> sorted = sort(parents);
        final Map<String, Node> created = newHashMap();
        for (String id : sorted) {
            Node node = new Node(map.get(id));
            created.put(node.getId(), node);
            Set<Node> parentNodes = newHashSet(
                    transform(parents.get(id),
                            new Function<String, Node>() {
                                @Nullable
                                @Override
                                public Node apply(@Nullable String input) {
                                    return created.get(input);
                                }
                            }));

            if (parentNodes.isEmpty()) {
                roots.add(node);
                continue;
            }

            for (Node parent : parentNodes) {
                Set<Node> childNodes = parent.getChildren();
                if (intersection(parentNodes, childNodes).isEmpty()) {
                    parent.addChild(node);
                    node.addParent(parent);
                }
            }
        }
        return this;
    }

    public Collection<Node> getRoots() {
        return unmodifiableCollection(roots);
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
        for (String id : map.keySet()) {
            if (edges.getParents(id).isEmpty()) {
                current.add(id);
            }
        }

        while (!current.isEmpty()) {
            String node = current.poll();
            sorted.add(node);
            for (String child : newArrayList(edges.getChildren(node))) {
                edges.remove(node, child);
                if (edges.getParents(child).isEmpty()) {
                    current.add(child);
                }
            }
        }

        if (!edges.isEmpty()) {
            throw new IllegalStateException("Graph has at least one cycle");
        }
        return sorted;
    }

    public EfoGraph filter(Predicate<EfoGraph.Node> predicate) {
        List<Node> newRoots = newArrayList();
        for (Node node : roots) {
            Node filtered = filter(node, predicate);
            if (filtered!= null) {
                newRoots.add(filtered);
            }
        }
        return new EfoGraph(newRoots);
    }

    private Node filter(Node node, Predicate<Node>predicate) {
        if (!predicate.apply(node)) {
            return null;
        }
        Node newNode = new Node(node.getTerm());
        for(Node child : node.getChildren()) {
            Node filtered = filter(child, predicate);
            if (filtered!= null) {
                newNode.addChild(filtered);
                filtered.addParent(newNode);
            }
        }
        return newNode;
    }

    private static class Edges {
        private SetMultimap<String, String> parents;
        private SetMultimap<String, String> children;

        public Edges(SetMultimap<String, String> parentsMap) {
            parents = HashMultimap.create(parentsMap);
            children = HashMultimap.create();
            Multimaps.invertFrom(parents, children);
        }

        public Collection<String> getChildren(String node) {
            return unmodifiableCollection(children.get(node));
        }

        public Collection<String> getParents(String child) {
            return unmodifiableCollection(parents.get(child));
        }

        public void remove(String node, String child) {
            children.remove(node, child);
            for (String key : newArrayList(parents.keySet())) {
                parents.remove(key, node);
            }
        }

        public boolean isEmpty() {
            return parents.isEmpty() && children.isEmpty();
        }
    }

    public static class Node {
        private final EfoTerm term;
        private final Set<Node> parents = newHashSet();
        private final Set<Node> children = newHashSet();

        public Node(EfoTerm term) {
            this.term = term;
        }

        public void addChild(Node child) {
            children.add(child);
        }

        public void addParent(Node parent) {
            parents.add(parent);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (!getId().equals(node.getId())) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return getId().hashCode();
        }

        @Override
        public String toString() {
            return getId();
        }

        public String getId() {
            return term.getAccession();
        }

        public Set<Node> getChildren() {
            return unmodifiableSet(children);
        }

        public Set<Node> getParents() {
            return unmodifiableSet(parents);
        }

        public EfoTerm getTerm() {
            return term;
        }
    }
}
