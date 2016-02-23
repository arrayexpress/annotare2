/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.addAll;

/**
 * @author Olga Melnichuk
 */
class RowSet {

    private final List<RowTag> tags = new ArrayList<RowTag>();

    private Map<RowTag, TaggedRow> map;

    public RowSet(RowTag... tags) {
        addAll(this.tags, tags);
    }

    public RowSet from(Table table) {
        this.map = new HashMap<RowTag, TaggedRow>();

        for (RowTag tag : tags) {
            map.put(tag, new TaggedRow(table, tag));
        }
        return this;
    }

    public int getWidth() {
        int res = 0;
        for (TaggedRow r : map.values()) {
            int size = r.getSize();
            res = res < size ? size : res;
        }
        return res;
    }

    public Map<RowTag, String> getColumn(int i) {
        HashMap<RowTag, String> column = new HashMap<RowTag, String>();
        for (RowTag tag : tags) {
            column.put(tag, map.get(tag).getValue(i));
        }
        return column;
    }
}
