/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.server.rpc.transform;

import org.junit.Test;
import sun.reflect.ReflectionFactory;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.ExperimentBuilderFactory.createExperimentProfile;

/**
 * @author Olga Melnichuk
 */
public class ExperimentBuilderFactoryTest {

    @Test(expected = IllegalStateException.class)
    public void unknownExperimentTypeTest() throws NoSuchMethodException, InvocationTargetException, InstantiationException {
        Constructor cstr = ExperimentProfileType.class.getDeclaredConstructor(
                String.class, int.class, String.class
        );
        ReflectionFactory reflection =
                ReflectionFactory.getReflectionFactory();
        Enum e = (Enum) reflection.newConstructorAccessor(cstr).newInstance(
                new Object[]{"MISSING_EXPERIMENT_TYPE", ExperimentProfileType.values().length, "Missing Experiment Type"});

        createExperimentProfile(new ExperimentSetupSettings((ExperimentProfileType) e), null);
    }
}
