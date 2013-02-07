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

package uk.ac.ebi.fg.annotare2.dao.dummy;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.om.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class DummyData {

    private static final Logger log = LoggerFactory.getLogger(DummyData.class);

    private static final Map<String, User> userByEmail = newHashMap();

    private static final Map<Integer, Submission> submissions = newHashMap();

    private static final ListMultimap<User, Submission> userSubmissions = ArrayListMultimap.create();

    private static int count = 1;


    static {

        User user = createUser("user@ebi.ac.uk", "ee11cbb19052e40b07aac0ca060c23ee");
        user.setRoles(asList(Role.AUTHENTICATED));

        try {
            createSubmission(user,
                    SubmissionStatus.IN_PROGRESS,
                    "E-GEOD-37590.idf.txt",
                    "E-GEOD-37590",
                    "Natural genetic variation in yeast longevity");

            createSubmission(user,
                    SubmissionStatus.IN_PROGRESS,
                    "E-MTAB-996.idf.txt",
                    "E-MTAB-996",
                    "E. coli Anaerobic/aerobic transitions in chemostat");

            createSubmission(user,
                    SubmissionStatus.PUBLIC_IN_AE,
                    "E-GEOD-37372.idf.txt",
                    "E-GEOD-37372",
                    "Ewing's sarcoma tumor samples");
        } catch (IOException e) {
            log.error("", e);
        }
    }

    private DummyData() {
    }

    private static User createUser(String email, String password) {
        User user = new User(nextId(), email, password);
        userByEmail.put(user.getEmail(), user);
        return user;
    }

    private static Submission createSubmission(User user, SubmissionStatus status, String idfName, String accession, String title) throws IOException {
        ExperimentSubmission submission = new SubmissionFactory().createExperimentSubmission(user);
        submission.setStatus(status);
        submission.setInvestigation(
                CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(idfName), Charsets.UTF_8)));
        submission.setTitle(title);
        submission.setAccession(accession);
        save(submission);
        return submission;
    }

    private static int nextId() {
        return count++;
    }

    public static User getUserByEmail(String email) {
        return userByEmail.get(email);
    }

    public static List<Submission> getSubmissions(User user) {
        return userSubmissions.get(user);
    }

    public static List<Submission> getSubmissions(User user, Predicate<Submission> predicate) {
        return new ArrayList<Submission>(filter(getSubmissions(user), predicate));
    }

    public static <T extends Submission> T getSubmission(int id, Class<T> clazz)
            throws RecordNotFoundException {
        Submission submission = submissions.get(id);
        if (submission == null)
            throw new RecordNotFoundException("Ooops!!!  " + id);
        return clazz.cast(submission);
    }

    public static void save(Submission submission) {
        if (submission.getId() == 0) {
            submission.setId(nextId());
            submissions.put(submission.getId(), submission);
            userSubmissions.put(submission.getCreatedBy(), submission);
        }
    }
}
