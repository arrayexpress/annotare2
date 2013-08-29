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
import uk.ac.ebi.fg.annotare2.configmodel.DataSerializationException;
import uk.ac.ebi.fg.annotare2.configmodel.JsonCodec;
import uk.ac.ebi.fg.annotare2.dao.RecordNotFoundException;
import uk.ac.ebi.fg.annotare2.om.*;
import uk.ac.ebi.fg.annotare2.om.enums.Role;
import uk.ac.ebi.fg.annotare2.om.enums.SubmissionStatus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Olga Melnichuk
 */
public class DummyData {

    private static final Logger log = LoggerFactory.getLogger(DummyData.class);

    private static final Map<String, User> userByEmail = newHashMap();

    private static final Map<Long, Submission> submissions = newHashMap();

    private static final ListMultimap<User, Submission> userSubmissions = ArrayListMultimap.create();

    private static final List<ArrayPrintingProtocol> arrayProtocols = newArrayList();

    private static long count = 1;

    static {

        User user = createUser("user@ebi.ac.uk", "ee11cbb19052e40b07aac0ca060c23ee");
        addRole(user, Role.AUTHENTICATED);

        createSubmission(user,
                SubmissionStatus.IN_PROGRESS,
                "E-MTAB-1160.json.txt",
                "E-MTAB-1160",
                "Transfection of HaCaT cells with HPV11 HPV16 or HPV45 genome");

        createSubmission(user,
                SubmissionStatus.IN_PROGRESS,
                "E-MEXP-3237.json.txt",
                "E-MEXP-3237",
                "rogB mutant in NEM316 S. agalactiae strain");

        createSubmission(user,
                SubmissionStatus.PUBLIC_IN_AE,
                "E-MTAB-582.json.txt",
                "E-MTAB-582",
                "RNA and chromatin structure");

        createAdSubmission(user,
                SubmissionStatus.IN_PROGRESS,
                "A-MEXP-2196.adf.header.json.txt",
                "A-MEXP-2196.adf.txt",
                "A-MEXP-2196",
                "LSTM_An.gambiae_s.s._AGAM15K_V1.0");

        arrayProtocols.add(new ArrayPrintingProtocol("Protocol-1", "<em>Protocol-1 description</em>"));
        arrayProtocols.add(new ArrayPrintingProtocol("Protocol-2", "<em>Protocol-2 description</em>"));
        arrayProtocols.add(new ArrayPrintingProtocol("Protocol-3", "<em>Protocol-3 description</em>"));
    }

    private DummyData() {
    }

    private static User createUser(String email, String password) {
        User user = new User(nextId(), email, password);
        userByEmail.put(user.getEmail(), user);
        return user;
    }

    private static void createSubmission(User user,
                                               SubmissionStatus status,
                                               String jsonFile,
                                               String accession,
                                               String title) {
        try {
            ExperimentSubmission submission = new SubmissionFactory().createExperimentSubmission(user);
            submission.setStatus(status);

            //TODO use experiment object instead
            submission.setExperimentProfile(JsonCodec.fromJson2Experiment(
                    CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(jsonFile), Charsets.UTF_8))));

            submission.setTitle(title);
            submission.setAccession(accession);
            save(submission);
        } catch (IOException e) {
            log.error("Can't create submission '" + jsonFile + "' ", e);
        } catch (DataSerializationException e) {
            log.error("Can't create submission '" + jsonFile + "' ", e);
        }
    }

    private static void createAdSubmission(User user,
                                                 SubmissionStatus status,
                                                 String headerFile,
                                                 String bodyFile,
                                                 String accession,
                                                 String title) {
        try {
            ArrayDesignSubmission submission = new SubmissionFactory().createArrayDesignSubmission(user);
            submission.setStatus(status);
            submission.setAccession(accession);
            submission.setTitle(title);
            submission.setHeader(
                    JsonCodec.fromJson2ArrayDesign(
                            CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(headerFile), Charsets.UTF_8))));
            submission.setBody(
                    CharStreams.toString(new InputStreamReader(DummyData.class.getResourceAsStream(bodyFile), Charsets.UTF_8))
            );
            save(submission);
        } catch (IOException e) {
            log.error("Can't create ArrayDesign submission: '" + headerFile + "' ", e);
        } catch (DataSerializationException e) {
            log.error("Can't create ArrayDesign submission: '" + headerFile + "' ", e);
        }
    }

    private static long nextId() {
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

    public static <T extends Submission> T getSubmission(long id, Class<T> clazz)
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

    private static void addRole(User user, Role role) {
        UserRole userRole = new UserRole(user, role);
        user.getRoles().add(userRole);
    }
}
