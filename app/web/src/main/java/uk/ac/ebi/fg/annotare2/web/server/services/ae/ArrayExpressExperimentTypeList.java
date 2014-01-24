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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.io.Closeables.close;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;

/**
 * @author Olga Melnichuk
 */
public class ArrayExpressExperimentTypeList {

    private static final Logger log = LoggerFactory.getLogger(ArrayExpressExperimentTypeList.class);

    private final List<String> experimentTypes = new ArrayList<String>();

    private ArrayExpressExperimentTypeList load() throws IOException {
        InputStream in = getClass().getResourceAsStream("/ArrayExpressExperimentTypes.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                experimentTypes.add(line.trim());
            }
            sort(experimentTypes);
            return this;
        } finally {
            close(in, true);
        }
    }

    public List<String> getExperimentTypes() {
        return unmodifiableList(experimentTypes);
    }

    public static ArrayExpressExperimentTypeList create() {
        ArrayExpressExperimentTypeList list = new ArrayExpressExperimentTypeList();
        try {
            return list.load();
        } catch (IOException e) {
            log.error("Can't load AE Experiment Types", e);
        }
        return list;
    }
}
