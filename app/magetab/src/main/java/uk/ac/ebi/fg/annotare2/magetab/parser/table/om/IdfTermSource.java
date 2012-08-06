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

package uk.ac.ebi.fg.annotare2.magetab.parser.table.om;

import uk.ac.ebi.fg.annotare2.magetab.parser.table.IdfCell;

/**
 * @author Olga Melnichuk
 */
public class IdfTermSource {

    private IdfCell name;

    private IdfCell version;

    private IdfCell file;

    public IdfCell getName() {
        return name;
    }

    public void setName(IdfCell name) {
        this.name = name;
    }

    public IdfCell getVersion() {
        return version;
    }

    public void setVersion(IdfCell version) {
        this.version = version;
    }

    public IdfCell getFile() {
        return file;
    }

    public void setFile(IdfCell file) {
        this.file = file;
    }
}
