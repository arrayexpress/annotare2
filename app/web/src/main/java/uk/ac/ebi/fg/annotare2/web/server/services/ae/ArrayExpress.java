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

package uk.ac.ebi.fg.annotare2.web.server.services.ae;

/**
 * @author Olga Melnichuk
 */
public abstract class ArrayExpress {
    public static class ArrayDesign {
        private final int id;
        private final String name;
        private final String desription;

        public ArrayDesign(int id, String name, String desription) {
            this.id = id;
            this.name = name;
            this.desription = desription;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDesription() {
            return desription;
        }
    }

}
