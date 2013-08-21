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

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import uk.ac.ebi.fg.annotare2.dao.DataFileDao;
import uk.ac.ebi.fg.annotare2.om.DataFile;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;

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

    @Override
    public void delete(DataFile file) {
        items.remove(file.getId(), file);
    }

    @Override
    public List<DataFile> getAllWithDigest(final String digest) {
        return newArrayList(Collections2.filter(items.values(), new Predicate<DataFile>() {
            @Override
            public boolean apply(@Nullable DataFile input) {
                return digest.equals(input.getDigest());
            }
        }));
    }
}
