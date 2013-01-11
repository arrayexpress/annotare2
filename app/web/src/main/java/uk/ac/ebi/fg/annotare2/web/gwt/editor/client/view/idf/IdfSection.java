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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

/**
 * @author Olga Melnichuk
 */
public enum IdfSection {
    GENERAL_INFO("General Information"),
    CONTACTS("Contacts"),
    PUBLICATIONS("Publications"),
    EXP_DESIGNS("Experimental Designs"),
    EXP_FACTORS("Experimental Factors"),
    QUALITY_CONTROLS("Quality Control Types"),
    PROTOCOLS("Protocols"),
    REPLICATES("Replicate Types"),
    NORMALIZATION_TYPES("Normalization Types"),
    COMMENTS("Comments"),
    TERM_DEF_SOURCES("Term Sources");

    private final String title;

    private IdfSection(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static IdfSection getIfPresent(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        for (IdfSection f : values()) {
            if (f.name().equals(token)) {
                return f;
            }
        }
        return null;
    }
}
