/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public enum ProtocolSubjectType {
    SAMPLE("sample"),
    EXTRACT("extract"),
    LABELED_EXTRACT("labeled extract"),
    ASSAY("assay"),
    RAW_FILE("raw file"),
    PROCESSED_FILE("processed file");

    private final String title;

    ProtocolSubjectType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Collection<Protocol> filter(Collection<Protocol> protocols) {
        List<Protocol> filtered = new ArrayList<Protocol>();
        for (Protocol protocol : protocols) {
            if (protocol.getSubjectType() == this) {
                filtered.add(protocol);
            }
        }
        return filtered;
    }

    public boolean isSampleExtractOrLabeledExtract() {
        return this == SAMPLE || this == EXTRACT || this == LABELED_EXTRACT;
    }
}
