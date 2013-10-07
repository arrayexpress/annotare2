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

package uk.ac.ebi.fg.annotare2.web.server.properties;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTrackingProperties;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabCheckProperties;
import uk.ac.ebi.fg.annotare2.magetabcheck.efo.EfoServiceProperties;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTerm;
import uk.ac.ebi.fg.annotare2.web.server.services.DataFileStore;

import java.io.*;
import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class AnnotareProperties implements DataFileStoreProperties, SubsTrackingProperties {

    private static final Logger log = LoggerFactory.getLogger(AnnotareProperties.class);

    private static final String ANNOTARE_PROPERTIES = "annotare.properties";

    private final MageTabCheckProperties mageTabCheckProperties;

    private final Properties properties;

    private final File tempDir;

    @Inject
    public AnnotareProperties(MageTabCheckProperties mageTabCheckProperties) {
        this.mageTabCheckProperties = mageTabCheckProperties;
        properties = load();

        tempDir = new File(System.getProperty("java.io.tmpdir"));
    }

    public File getEfoIndexDir() {
        return getDirProperty("efo.index.dir");
    }

    public File getHttpUploadDir() {
        return getDirProperty("httpupload.temp.dir");
    }

    public File getFilePickUpDir() {
        return getDirProperty("pickup.dir");
    }

    @Override
    public File getDataStoreDir() {
        return getDirProperty("datastore.dir");
    }

    public File getExportDir() {
        return getDirProperty("export.dir");
    }

    public String getPublicFtpUrl() {
        return getProperty("ftp.public.url");
    }

    public String getPublicFtpUsername() {
        return getProperty("ftp.public.username");
    }

    public String getPublicFtpPassword() {
        return getProperty("ftp.public.password");
    }

    @Override
    public Boolean getAeSubsTrackingEnabled() {
        return Boolean.parseBoolean(getProperty("ae-subs-tracking.enabled"));
    }

    @Override
    public String getAeSubsTrackingUser() {
        return getProperty("ae-subs-tracking.user");
    }

    @Override
    public String getAeSubsTrackingExperimentType() {
        return getProperty("ae-subs-tracking.experiment-type");
    }

    @Override
    public File getAeSubsTrackingExportDir() {
        return getDirProperty("ae-subs-tracking.export.dir");
    }

    private File getDirProperty(String name) {
        String property = getProperty(name);
        File dir = property == null ? new File(tempDir, name.replaceAll(".","-")) :
                new File(property);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.error("Can't create directory: {}", dir);
            }
        }
        return dir;
    }

    public EfoServiceProperties getEfoServiceProperties() {
        return mageTabCheckProperties;
    }

    private String getProperty(String key) {
        return properties.getProperty(key);
    }

    private Properties load() {
        Properties defaults = load("/Annotare-default.properties", new Properties());

        Properties p = new Properties(defaults);

        String propertiesFile = System.getProperty(ANNOTARE_PROPERTIES);
        p = isNullOrEmpty(propertiesFile) ?
                load("/Annotare.properties", p) :
                load(new File(propertiesFile), p);
        return p;
    }

    private static Properties load(String resourceName, Properties properties) {
        try {
            log.info("Loading properties from resource: " + resourceName);
            return load(AnnotareProperties.class.getResourceAsStream(resourceName), properties);
        } catch (IOException e) {
            log.error("Can't load properties from classpath: " + resourceName, e);
        }
        return properties;
    }

    private static Properties load(File file, Properties properties) {
        log.info("Loading properties from file: " + file.getAbsolutePath());
        try {
            return load(new FileInputStream(file), properties);
        } catch (FileNotFoundException e) {
            log.error("Can't load properties from file " + file.getAbsoluteFile(), e);
        } catch (IOException e) {
            log.error("Can't load properties from file " + file.getAbsoluteFile(), e);
        }
        return properties;
    }

    public static Properties load(InputStream in, Properties properties) throws IOException {
        if (in != null) {
            properties.load(in);
        }
        return properties;
    }

    public String getEfoTermAccession(SystemEfoTerm term) {
        return getProperty("efo.term." + term.getPropertyName());
    }
}
