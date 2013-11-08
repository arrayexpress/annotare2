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

package uk.ac.ebi.fg.annotare2.submission.model;

import com.google.common.annotations.GwtCompatible;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProtocolAssignment implements Serializable {

    @JsonProperty("ids")
    private Set<Integer> protocolIds;

    public ProtocolAssignment() {
        protocolIds = new HashSet<Integer>();
    }

    public boolean contains(Protocol protocol) {
        return protocolIds.contains(protocol.getId());
    }

    public void set(Protocol protocol, boolean assigned) {
        if (assigned) {
            protocolIds.add(protocol.getId());
            return;
        }
        if (contains(protocol)) {
            protocolIds.remove(protocol.getId());
        }
    }
}
