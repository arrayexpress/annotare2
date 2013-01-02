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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class ValidationResult implements Serializable {

    private final ArrayList<String> errors = new ArrayList<String>();

    private final ArrayList<String> warnings = new ArrayList<String>();

    private final ArrayList<String> failures = new ArrayList<String>();

    public ValidationResult() {
    }

    public ValidationResult(Throwable throwable) {

    }

    public ValidationResult(ArrayList<String> errors, ArrayList<String> warnings) {
        this.errors.addAll(errors);
        this.warnings.addAll(warnings);
    }

    public boolean hasErrors() {
        return errors.isEmpty();
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public ArrayList<String> getWarnings() {
        return warnings;
    }
}
