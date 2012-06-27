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

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import uk.ac.ebi.fg.annotare2.om.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class DummyData {

    private static final Map<String, User> userByEmail = new HashMap<String, User>();

    private static final Map<Integer, Submission> submissions = new HashMap<Integer, Submission>();
    private static final Multimap<Integer, Integer> userSubmissions = ArrayListMultimap.create();

    private static int count = 1;

    static {
        User user = createUser("user@ebi.ac.uk", "ee11cbb19052e40b07aac0ca060c23ee");
        user.setRoles(asList(UserRole.AUTHENTICATED));

        createSubmission(user,
                "Transcription profiling of human brain total RNA vs Universal Human Reference RNA on 4 different commercially available microarray to assess comparability of gene expression measurements on microarrays (24 assays)",
                "Commercially available human genomic microarrays from four different manufacturers were used to compare Human Brain Total RNA against Universal Human Reference RNA (both commercially available) prepared at two different starting amounts (20 µg or 1µg). For each amount of RNA, 6 replicates were performed with Human Brain Total RNA labelled with Cy3, and Universal Human Reference RNA labelled with Cy5. The labelling was then reversed (dye flip) creating another 6 replicates. This meant that for each of the four manufacturers there were a total of 24 arrays. Image processing was performed with two different software packages, and data was normalized with three different strategies.");

        createSubmission(user,
                "Transcription profiling of non-cancerous tissue and cancerous tissue from gastric and colon cancer patients (96 assays)",
                "Whole-genome microarray profiling of gene expression pattern in 96 tissues from gastric and colon cancer patients");

        createSubmission(user,
                "Ewing's sarcoma tumor samples",
                "This SuperSeries is composed of the following subset Series: GSE37370: microRNA expression data from Ewing's sarcoma tumor samples GSE37371: Expression data from Ewing's sarcoma tumor samples Refer to individual Series");
    }

    private DummyData() {}


    private static User createUser(String email, String password) {
        User user = new User(count++, email, password);
        userByEmail.put(user.getEmail(), user);
        return user;
    }

    private static Submission createSubmission(User user, String title, String description) {
        Submission submission = new ExperimentSubmission(count++, title, description);
        submissions.put(submission.getId(), submission);
        userSubmissions.put(user.getId(), submission.getId());
        return submission;
    }

    public static User getUserByEmail(String email) {
        return userByEmail.get(email);
    }

    public static Submission getSubmission(int id) {
        return submissions.get(id);
    }

    public static List<Submission> getSubmissions(User user, SubmissionType type) {
        return Lists.transform(new ArrayList<Integer>(userSubmissions.get(user.getId())), new Function<Integer, Submission>() {
            public Submission apply(@Nullable Integer id) {
                return getSubmission(id);
            }
        });
    }
}
