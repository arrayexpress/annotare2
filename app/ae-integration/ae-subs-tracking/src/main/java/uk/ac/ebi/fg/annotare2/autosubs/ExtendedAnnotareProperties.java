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

package uk.ac.ebi.fg.annotare2.autosubs;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.ae.AEConnectionProperties;
import uk.ac.ebi.fg.annotare2.ae.ArrayExpressProperties;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.properties.DataFileStoreProperties;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabCheckProperties;

import java.io.File;

public class ExtendedAnnotareProperties extends AnnotareProperties
        implements DataFileStoreProperties, ArrayExpressProperties, SubsTrackingProperties, AEConnectionProperties {

    @Inject
    public ExtendedAnnotareProperties(MageTabCheckProperties mageTabCheckProperties) {
        super(mageTabCheckProperties);
    }

    @Override
    public String getArrayExpressArrayListURL() {
        return getProperty("arrayexpress.arraylist.url");
    }

    @Override
    public Boolean isSubsTrackingEnabled() {
        return Boolean.parseBoolean(getProperty("ae-subs-tracking.enabled"));
    }

    @Override
    public String getSubsTrackingUser() {
        return getProperty("ae-subs-tracking.user");
    }

    @Override
    public String getSubsTrackingExperimentType() {
        return getProperty("ae-subs-tracking.experiment-type");
    }

    @Override
    public File getSubsTrackingExportDir() {
        return getDirProperty("ae-subs-tracking.export.dir");
    }

    @Override
    public String getSubsTrackingConnectionDriverClass() {
        return getProperty("ae-subs-tracking.connection.driver");
    }

    @Override
    public String getSubsTrackingConnectionURL() {
        return getProperty("ae-subs-tracking.connection.url");
    }

    @Override
    public String getSubsTrackingConnectionUser() {
        return getProperty("ae-subs-tracking.connection.user");
    }

    @Override
    public String getSubsTrackingConnectionPassword() {
        return getProperty("ae-subs-tracking.connection.password");
    }

    @Override
    public String getSubsTrackingDataFilesPostProcessingScript() {
        return getProperty("ae-subs-tracking.export.post-processing-script");
    }

    @Override
    public Boolean isAeConnectionEnabled() {
        return Boolean.parseBoolean(getProperty("ae-connection.enabled"));
    }

    @Override
    public String getAeConnectionDriverClass() {
        return getProperty("ae-connection.driver");
    }

    @Override
    public String getAeConnectionURL() {
        return getProperty("ae-connection.url");
    }

    @Override
    public String getAeConnectionUser() {
        return getProperty("ae-connection.user");
    }

    @Override
    public String getAeConnectionPassword() {
        return getProperty("ae-connection.password");
    }
}
