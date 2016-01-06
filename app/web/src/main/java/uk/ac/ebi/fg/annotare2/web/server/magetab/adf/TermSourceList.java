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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.magetab.adf;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.table.Table;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
class TermSourceList extends ObjectList<TermSource> {

    public TermSourceList(Table table, final RowTag nameTag, final RowTag versionTag, final RowTag fileTag) {
        super(new RowSet(
                nameTag,
                versionTag,
                fileTag).from(table),
                new ObjectCreator<TermSource>() {
                    @Override
                    public TermSource create(Map<RowTag, String> map) {
                        return new TermSource(
                                map.get(nameTag),
                                map.get(versionTag),
                                map.get(fileTag));
                    }
                });
    }

    public TermSource find(String name) {
        if (!isNullOrEmpty(name)) {
            for (TermSource ts : getAll()) {
                String tsName = ts.getName();
                if (!isNullOrEmpty(tsName) && tsName.equalsIgnoreCase(name)) {
                    return ts;
                }
            }
        }
        return null;
    }
}