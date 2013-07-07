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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
public class EfoGraphDto implements IsSerializable {

    private List<Node> roots;

    EfoGraphDto() {
        /* used by GWT serialization */
    }

    public EfoGraphDto(List<Node> roots) {
        this.roots = new ArrayList<Node>(roots);
    }

    public List<Node> getRoots() {
        return unmodifiableList(roots);
    }

    public static class Node implements IsSerializable {
        private OntologyTerm term;
        private List<Node> children;

        Node() {
            /* used by GWT serialization */
        }

        public Node(OntologyTerm term, List<Node> children) {
            this.term = term;
            this.children = new ArrayList<Node>(children);
        }

        public OntologyTerm getTerm() {
            return term;
        }

        public List<Node> getChildren() {
            return unmodifiableList(children);
        }
    }
}
