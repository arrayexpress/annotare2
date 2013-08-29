/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.dao;

import uk.ac.ebi.fg.annotare2.om.*;
import uk.ac.ebi.fg.annotare2.om.enums.SubmissionStatus;

import java.util.Collection;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public interface SubmissionDao {

    ExperimentSubmission getExperimentSubmission(long id) throws RecordNotFoundException;

    ArrayDesignSubmission getArrayDesignSubmission(long id) throws RecordNotFoundException;

    List<Submission> getSubmissions(User user);

    Collection<Submission> getSubmissionsByStatus(User user, SubmissionStatus... statuses);

    void save(Submission submission);

    Submission get(long id) throws RecordNotFoundException;
}
