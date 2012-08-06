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

package uk.ac.ebi.fg.annotare2.magetab.parser.table;

/**
 * @author Olga Melnichuk
 */
public class IdfRow {

    private final String tag;

    private final String attribute;

    public IdfRow(String tag) {
        this.tag = tag;
        this.attribute = null;
    }

    public IdfRow(String tag, String attribute) {
        this.tag = tag;
        this.attribute = attribute;
    }

    public String getTag() {
        return tag;
    }

    public String getAttribute() {
        return attribute;
    }

    public boolean identifies(String tag) {
        return this.tag.equals(tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdfRow)) return false;

        IdfRow idfRow = (IdfRow) o;

        if (attribute != null ? !attribute.equals(idfRow.attribute) : idfRow.attribute != null) return false;
        if (tag != null ? !tag.equals(idfRow.tag) : idfRow.tag != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tag != null ? tag.hashCode() : 0;
        result = 31 * result + (attribute != null ? attribute.hashCode() : 0);
        return result;
    }
}
