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

/**
 * @author Olga Melnichuk
 */
public enum FileType {

    RAW_FILE("Raw"),

    RAW_MATRIX_FILE("Raw Matrix"),

    PROCESSED_FILE("Processed"),

    PROCESSED_MATRIX_FILE("Processed Matrix");

    private final String title;

    FileType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unused")
    public boolean isRaw() {
        return RAW_FILE == this || RAW_MATRIX_FILE == this;
    }

    @SuppressWarnings("unused")
    public boolean isProcessed() {
        return PROCESSED_FILE == this || PROCESSED_MATRIX_FILE == this;
    }

    @SuppressWarnings("unused")
    public boolean isFGEM() {
        return RAW_MATRIX_FILE == this || PROCESSED_MATRIX_FILE == this;
    }
}
