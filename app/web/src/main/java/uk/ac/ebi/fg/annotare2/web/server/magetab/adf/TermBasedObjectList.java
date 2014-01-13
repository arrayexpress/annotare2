/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
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

/**
 * @author Olga Melnichuk
 */
class TermBasedObjectList extends ObjectList<Term> {

    public TermBasedObjectList(Table table,
                               final TermSourceList termSources,
                               final RowTag nameTag,
                               final RowTag accessionTag,
                               final RowTag sourceRefTag) {
        super(new RowSet(
                nameTag,
                accessionTag,
                sourceRefTag).from(table),
                new ObjectCreator<Term>() {
                    @Override
                    public Term create(Map<RowTag, String> map) {
                        return new Term(
                                map.get(nameTag),
                                map.get(accessionTag),
                                termSources.find(map.get(sourceRefTag)));
                    }
                });
    }
}