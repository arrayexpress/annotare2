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

import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.ae.AEConnection;
import uk.ac.ebi.fg.annotare2.ae.AeConnectionProperties;
import uk.ac.ebi.fg.annotare2.ae.ArrayExpressProperties;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTracking;
import uk.ac.ebi.fg.annotare2.autosubs.SubsTrackingProperties;
import uk.ac.ebi.fg.annotare2.core.AnnotarePluginModule;
import uk.ac.ebi.fg.annotare2.core.components.MessengerService;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;

import static com.google.inject.Scopes.SINGLETON;

@SuppressWarnings("unused")
@AnnotarePluginModule
public class ArrayExpressIntegrationPlugin extends AbstractModule {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    protected void configure() {
        bind(SubsTracking.class).in(SINGLETON);
        bind(AEConnection.class).in(SINGLETON);
        bind(AeIntegrationWatchdog.class).asEagerSingleton();
        bind(FileValidationService.class).asEagerSingleton();
        bind(SubmissionStatusUpdater.class).asEagerSingleton();
        bind(MessengerService.class).to(RtMessengerService.class).asEagerSingleton();

        bind(ExtendedAnnotareProperties.class).asEagerSingleton();

        bind(AnnotareProperties.class).to(ExtendedAnnotareProperties.class);
        bind(ArrayExpressProperties.class).to(ExtendedAnnotareProperties.class);
        bind(SubsTrackingProperties.class).to(ExtendedAnnotareProperties.class);
        bind(AeConnectionProperties.class).to(ExtendedAnnotareProperties.class);
    }
}
