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

package uk.ac.ebi.fg.annotare2.db.model.enums;

/**
 * @author Olga Melnichuk
 */
public enum SubmissionStatus {
    IN_PROGRESS("In Progress"),
    SUBMITTED("Submitted"),
    IN_CURATION("In Curation"),
    PRIVATE_IN_AE("Private in ArrayExpress"),
    PUBLIC_IN_AE("Public in ArrayExpress");

    private final String title;

    private SubmissionStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
