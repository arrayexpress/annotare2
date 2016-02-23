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

package uk.ac.ebi.fg.annotare2.core;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.atteo.classindex.ClassIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotarePluginModules extends AbstractModule {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    protected void configure() {
        Iterable<Class<?>> handlers = ClassIndex.getAnnotated(AnnotarePluginModule.class);
        for (Class<?> clazz : handlers) {
            if (Module.class.isAssignableFrom(clazz)) {
                try {
                    install((Module) clazz.newInstance());
                } catch (InstantiationException | IllegalAccessException x) {
                    logger.error("Unable to instantiate a plugin module " + clazz.getName() + " due to: ", x);
                }
            } else {
                logger.error("Plugin module {} should implement interface com.google.inject.Module",  clazz.getName());
            }
        }
    }
}
