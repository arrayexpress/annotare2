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

package uk.ac.ebi.fg.annotare2.integration;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTrackingProperties;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;
import uk.ac.ebi.fg.annotare2.core.properties.DataFileStoreProperties;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabCheckProperties;
import uk.ac.ebi.fg.annotare2.otrs.OtrsProperties;

import java.io.File;

public class ExtendedAnnotareProperties extends AnnotareProperties implements DataFileStoreProperties,
        BiostudiesProperties, SubsTrackingProperties, OtrsProperties, FileValidationProperties, RtProperties {

    @Inject
    public ExtendedAnnotareProperties(MageTabCheckProperties mageTabCheckProperties) {
        super(mageTabCheckProperties);
    }

    @Override
    public String getBiostudiesArrayListUrl() {
        return getProperty("biostudies.arraylist.url");
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
    public String getSubsTrackingConnectionUrl() {
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
    public String getMoveExportDirectoryScript() {
        return getProperty("ae-subs-tracking.export.move-script");
    }

    @Override
    public boolean isOtrsIntegrationEnabled() {
        return getBooleanProperty("otrs-integration.enabled");
    }

    @Override
    public String getOtrsIntegrationUrl() {
        return getProperty("otrs-integration.url");
    }

    @Override
    public String getOtrsIntegrationUser() {
        return getProperty("otrs-integration.user");
    }

    @Override
    public String getOtrsIntegrationPassword() {
        return getProperty("otrs-integration.password");
    }

    @Override
    public String getOtrsIntegrationSubjectTemplate() {
        return getProperty("otrs-integration.subject-template");
    }

    @Override
    public String getOtrsIntegrationBodyTemplate() { return getProperty("otrs-integration.body-template"); }

    @Override
    public String getFileValidationUrl() { return getProperty("file-validation.url");
    }

    @Override
    public boolean isRtIntegrationEnabled() { return getBooleanProperty("rt-integration.enabled"); }

    @Override
    public String getRtIntegrationUrl() { return getProperty("rt-integration.url"); }

    @Override
    public String getRtIntegrationUser() { return getProperty("rt-integration.user"); }

    @Override
    public String getRtIntegrationPassword() { return getProperty("rt-integration.password"); }

    @Override
    public String getRtIntegrationSubjectTemplate() { return getProperty("rt-integration.subject-template"); }

    @Override
    public String getRtIntegrationBodyTemplate() { return getProperty("rt-integration.body-template"); }

    @Override
    public String getRtQueueName() { return getProperty("rt-integration.queue-name"); }


}
