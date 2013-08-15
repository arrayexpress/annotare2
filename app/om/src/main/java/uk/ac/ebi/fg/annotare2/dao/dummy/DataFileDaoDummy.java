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

package uk.ac.ebi.fg.annotare2.dao.dummy;

import uk.ac.ebi.fg.annotare2.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.om.DataFile;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Olga Melnichuk
 */
public class DataFileDaoDummy implements DataFileDao {

    private final ConcurrentMap<Integer, DataFile> items = new ConcurrentHashMap<Integer, DataFile>();
    private AtomicInteger id = new AtomicInteger(0);

    @Override
    public DataFile get(int id) {
        return items.get(id);
    }

    @Override
    public DataFile create(String fileName) {
        DataFile file = new DataFile(fileName, id.incrementAndGet());
        items.putIfAbsent(file.getId(), file);
        return file;
    }
}
