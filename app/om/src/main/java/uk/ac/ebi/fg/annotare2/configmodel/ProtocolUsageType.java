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

package uk.ac.ebi.fg.annotare2.configmodel;

import com.google.common.annotations.GwtCompatible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public enum ProtocolUsageType {
    SAMPLE_AND_EXTRACT,
    EXTRACT_AND_LABELED_EXTRACT,
    EXTRACT_AND_ASSAY,
    LABELED_EXTRACT_AND_ASSAY,
    ASSAY_AND_FILE,
    FILE_AND_FILE;

    public Collection<Protocol> filter(Collection<Protocol> protocols) {
        List<Protocol> filtered = new ArrayList<Protocol>();
        for(Protocol protocol : protocols) {
            if (protocol.getUsage() == this) {
                filtered.add(protocol);
            }
        }
        return filtered;
    }
}
