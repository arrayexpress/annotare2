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

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionValidationService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;

import java.util.ArrayList;
import java.util.Random;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidationServiceImpl extends RemoteServiceBase implements SubmissionValidationService {

    private static final Random random = new Random(123);

    @Override
    public ValidationResult validate(int submissionId) throws ResourceNotFoundException, NoPermissionException {
        //TODO add real validation code here
        ArrayList<String> errors = generate(3);
        ArrayList<String> warnings = generate(2);
        return new ValidationResult(errors, warnings);
    }

    private ArrayList<String> generate(int n) {
        ArrayList<String> list = newArrayList();
        for (int i = 0; i < n; i++) {
            int r = random.nextInt();
            if (r % 2 == 0) {
                list.add("generated text " + r);
            }
        }
        return list;
    }
}
