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
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class ValidationResult implements Serializable {

    private List<String> errors = new ArrayList<String>();

    private List<String> warnings = new ArrayList<String>();

    private List<String> failures = new ArrayList<String>();

    public ValidationResult() {
    }

    public ValidationResult(Throwable throwable) {
        failures.add(throwable.getMessage());
    }

    public ValidationResult(List<String> errors, List<String> warnings) {
        this.errors.addAll(errors);
        this.warnings.addAll(warnings);
    }

    public ValidationResult(List<String> errors, List<String> warnings, List<String> failures) {
        this.errors.addAll(errors);
        this.warnings.addAll(warnings);
        this.failures.addAll(failures);
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<String> getFailures() {
        return failures;
    }
}
