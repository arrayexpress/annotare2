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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.om.*;
import uk.ac.ebi.fg.annotare2.om.enums.Role;
import uk.ac.ebi.fg.annotare2.om.enums.SubmissionStatus;
import uk.ac.ebi.fg.annotare2.submissionmodel.DataSerializationExcepetion;
import uk.ac.ebi.fg.annotare2.submissionmodel.Experiment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
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

    private static final List<ArrayPrintingProtocol> arrayProtocols = newArrayList();

    private static int count = 1;

    static {

        User user = createUser("user@ebi.ac.uk", "ee11cbb19052e40b07aac0ca060c23ee");
        user.setRoles(asList(Role.AUTHENTICATED));

        try {
            createSubmission(user,
                    SubmissionStatus.IN_PROGRESS,
                    "E-MTAB-641.idf.txt",
                    "E-MTAB-641.sdrf.txt",
                    "E-MTAB-641",
                    "agb1-1 mutant and Col-0 comparative transcriptomic analysis in a Plectosphaerella cucumerina inoculation");

            createSubmission(user,
                    SubmissionStatus.IN_PROGRESS,
                    "E-MEXP-3237.idf.txt",
                    "E-MEXP-3237.sdrf.txt",
                    "E-MEXP-3237",
                    "rogB mutant in NEM316 S. agalactiae strain");

            createSubmission(user,
                    SubmissionStatus.PUBLIC_IN_AE,
                    "E-MTAB-582.idf.txt",
                    "E-MTAB-582.sdrf.txt",
                    "E-MTAB-582",
                    "RNA and chromatin structure");

            createAdSubmission(user,
                    SubmissionStatus.IN_PROGRESS,
                    "A-MEXP-2196.adf.header.txt",
                    "A-MEXP-2196.adf.txt",
                    "A-MEXP-2196",
                    "LSTM_An.gambiae_s.s._AGAM15K_V1.0");

            arrayProtocols.add(new ArrayPrintingProtocol("Protocol-1", "<em>Protocol-1 description</em>"));
            arrayProtocols.add(new ArrayPrintingProtocol("Protocol-2", "<em>Protocol-2 description</em>"));
            arrayProtocols.add(new ArrayPrintingProtocol("Protocol-3", "<em>Protocol-3 description</em>"));

        } catch (IOException e) {
            log.error("", e);
        } catch (DataSerializationExcepetion e) {
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

    private static Submission createSubmission(User user,
                                               SubmissionStatus status,
                                               String idfName,
                                               String sdrfName,
                                               String accession,
                                               String title) throws IOException, DataSerializationExcepetion {
        ExperimentSubmission submission = new SubmissionFactory().createExperimentSubmission(user);
        submission.setStatus(status);

        //TODO use experiment object instead
        submission.setInvestigation(
                CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(idfName), Charsets.UTF_8)));
        if (!isNullOrEmpty(sdrfName)) {
            submission.setSampleAndDataRelationship(
                    CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(sdrfName), Charsets.UTF_8)));
        }
        submission.setExperiment(new Experiment(Collections.<String, String>emptyMap()));
        submission.setTitle(title);
        submission.setAccession(accession);
        save(submission);
        return submission;
    }

    private static Submission createAdSubmission(User user,
                                                 SubmissionStatus status,
                                                 String headerFile,
                                                 String bodyFile,
                                                 String accession,
                                                 String title) throws IOException {
        ArrayDesignSubmission submission = new SubmissionFactory().createArrayDesignSubmission(user);
        submission.setStatus(status);
        submission.setAccession(accession);
        submission.setTitle(title);
        submission.setHeader(
                CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(headerFile), Charsets.UTF_8))
        );
        submission.setBody(
                CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(bodyFile), Charsets.UTF_8))
        );
        save(submission);
        return submission;
    }

    private static int nextId() {
        return count++;
    }

    public static List<ArrayPrintingProtocol> getAllArrayPrintingProtocols() {
        return ImmutableList.copyOf(arrayProtocols);
    }

    public static User getUserByEmail(String email) {
        return userByEmail.get(email);
    }

    public static List<Submission> getSubmissions(User user) {
        return userSubmissions.get(user);
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
