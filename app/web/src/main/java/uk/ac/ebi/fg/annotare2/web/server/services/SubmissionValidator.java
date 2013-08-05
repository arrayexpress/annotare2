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

package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.magetabcheck.MageTabChecker;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.ExperimentType;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.magetabcheck.modelimpl.limpopo.LimpopoBasedExperiment;
import uk.ac.ebi.fg.annotare2.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.web.server.rpc.MageTabFormat;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static com.google.common.collect.Ordering.natural;
import static java.util.Collections.emptyList;
import static uk.ac.ebi.fg.annotare2.web.server.rpc.MageTabFormat.createMageTab;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidator {

    private final MageTabChecker checker;

    @Inject
    public SubmissionValidator(MageTabChecker checker) {
        this.checker = checker;
    }

    public Collection<CheckResult> validate(ExperimentSubmission submission) throws IOException,
            ParseException, UknownExperimentTypeException, DataSerializationException {

        ExperimentProfile exp = submission.getExperimentProfile();
        ExperimentType type = exp.getType().isMicroarray() ? ExperimentType.MICRO_ARRAY : ExperimentType.HTS;

        MageTabFormat mageTab = createMageTab(exp);

        Collection<CheckResult> results = checker.check(new LimpopoBasedExperiment(mageTab.getIdf(), mageTab.getSdrf()), type);
        return natural().sortedCopy(results);
    }
}
