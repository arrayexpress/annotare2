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
public class Normalization {

    private final Term type;

    public Normalization(Term type) {
        checkArgument(type != null, "Normalization Type could not be null");
        this.type = type;
    }

    public Term getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Normalization{" +
                "type=" + type +
                '}';
    }
}
