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

import com.google.common.base.Optional;
import uk.ac.ebi.fg.annotare2.om.*;
import uk.ac.ebi.fg.annotare2.om.enums.AclType;
import uk.ac.ebi.fg.annotare2.om.enums.Permission;
import uk.ac.ebi.fg.annotare2.om.enums.Role;

import static com.google.common.base.Optional.absent;
import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class SubmissionFactory {

    public static final Optional<User> OWNER = absent();

    private static Acl submissionAcl;

    static {
        submissionAcl = new Acl(AclType.SUBMISSION);
        submissionAcl.getEntries().addAll(asList(
                createAclEntry(Role.AUTHENTICATED, Permission.CREATE),

                createAclEntry(Role.OWNER, Permission.VIEW),
                createAclEntry(Role.OWNER, Permission.UPDATE),

                createAclEntry(Role.CURATOR, Permission.CREATE),
                createAclEntry(Role.CURATOR, Permission.VIEW),
                createAclEntry(Role.CURATOR, Permission.UPDATE)));
    }

    private static AclEntry createAclEntry(Role role, Permission permission) {
        return new AclEntry(role, permission);
    }

    public static ExperimentSubmission createExperimentSubmission(User creator) {
        ExperimentSubmission sbm = new ExperimentSubmission(creator);
        sbm.setAcl(submissionAcl);
        return sbm;
    }

    public static ArrayDesignSubmission createArrayDesignSubmission(User creator) {
        ArrayDesignSubmission sbm = new ArrayDesignSubmission(creator);
        sbm.setAcl(submissionAcl);
        return sbm;
    }

    public static EffectiveAcl getEffectiveAcl() {
        return new EffectiveAcl(submissionAcl, OWNER);
    }

    public static Acl getAcl() {
        return submissionAcl;
    }
}
