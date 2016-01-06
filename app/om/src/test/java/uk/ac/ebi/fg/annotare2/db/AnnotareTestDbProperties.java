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

package uk.ac.ebi.fg.annotare2.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Olga Melnichuk
 */
public class AnnotareTestDbProperties {

    private static final Logger log = LoggerFactory.getLogger(AnnotareTestDbProperties.class);

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(AnnotareTestDbProperties.class.getResourceAsStream("/AnnotareTestDb.properties"));
        } catch (IOException e) {
            log.error("Unable to load test DB properties", e);
        }
    }

    public static String getTestDbUser() {
        return properties.getProperty("test.db.user");
    }

    public static String getTestDbPassword() {
        return properties.getProperty("test.db.password");
    }

    public static String getTestDbUrl() {
        return properties.getProperty("test.db.url");
    }
}
