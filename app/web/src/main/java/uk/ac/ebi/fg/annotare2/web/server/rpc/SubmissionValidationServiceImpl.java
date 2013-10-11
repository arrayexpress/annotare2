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

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.db.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.db.om.ExperimentSubmission;
import uk.ac.ebi.fg.annotare2.db.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.CheckResult;
import uk.ac.ebi.fg.annotare2.magetabcheck.checker.UknownExperimentTypeException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.NoPermissionException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.ResourceNotFoundException;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.SubmissionValidationService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.server.login.AuthService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccessControlException;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionManager;
import uk.ac.ebi.fg.annotare2.web.server.services.SubmissionValidator;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionValidationServiceImpl extends SubmissionBasedRemoteService implements SubmissionValidationService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionValidationServiceImpl.class);

    private final SubmissionValidator validator;

    @Inject
    public SubmissionValidationServiceImpl(AuthService authService,
                                           SubmissionManager submissionManager,
                                           SubmissionValidator validator) {
        super(authService, submissionManager);
        this.validator = validator;
    }

    @Transactional
    @Override
    public ValidationResult validate(int submissionId) throws ResourceNotFoundException, NoPermissionException {
        List<String> failures = newArrayList();
        List<String> errors = newArrayList();
        List<String> warnings = newArrayList();

        try {
            ExperimentSubmission subm = getExperimentSubmission(submissionId, Permission.VIEW);
            Collection<CheckResult> results = validator.validate(subm);
            for (CheckResult cr : results) {
                switch (cr.getStatus()) {
                    case WARNING:
                        warnings.add(cr.asString());
                        break;
                    case ERROR:
                        errors.add(cr.asString());
                        break;
                    case FAILURE:
                        errors.add(cr.asString());
                        break;
                }
            }
        } catch (RecordNotFoundException e) {
            throw noSuchRecord(e);
        } catch (AccessControlException e) {
            throw noPermission(e);
        } catch (IOException e) {
            log.error("Validation failure", e);
            failures.add("Failure: " + e.getMessage());
        } catch (ParseException e) {
            log.error("Validation failure", e);
            failures.add("Failure: " + e.getMessage());
        } catch (UknownExperimentTypeException e) {
            log.error("Validation failure", e);
            failures.add("Failure: " + e.getMessage());
        } catch (DataSerializationException e) {
            log.error("Validation failure", e);
            failures.add("Failure: " + e.getMessage());
        }
        return new ValidationResult(errors, warnings, failures);
    }
}
