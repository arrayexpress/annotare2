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

package uk.ac.ebi.fg.annotare2.magetab.idf;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Olga Melnichuk
 */
public class ExperimentalDesign {

    private final Term name;

    public ExperimentalDesign(Term name) {
        checkArgument(name != null, "Experimental Design name could not be null");
        this.name = name;
    }

    public Term getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ExperimentalDesign{" +
                "name=" + name +
                '}';
    }
}
