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

package uk.ac.ebi.fg.annotare2.om;

import com.google.common.base.Optional;

import static com.google.common.base.Optional.absent;

/**
 * @author Olga Melnichuk
 */
public class SubmissionFactory implements HasEffectiveAcl {
    public static final Optional<User> OWNER = absent();

    private static Acl submissionAcl;

    static {
        submissionAcl = new Acl()
                .add(createAclEntry(Role.AUTHENTICATED, Permission.CREATE))

                .add(createAclEntry(Role.OWNER, Permission.VIEW))
                .add(createAclEntry(Role.OWNER, Permission.UPDATE))

                .add(createAclEntry(Role.CURATOR, Permission.CREATE))
                .add(createAclEntry(Role.CURATOR, Permission.VIEW))
                .add(createAclEntry(Role.CURATOR, Permission.UPDATE));
    }

    private static AclEntry createAclEntry(Role role, Permission permission) {
        return new AclEntry(role, permission);
    }

    public ExperimentSubmission createExperimentSubmission(User creator) {
        return new ExperimentSubmission(creator, submissionAcl);
    }

    public ArrayDesignSubmission createArrayDesignSubmission(User creator) {
        return new ArrayDesignSubmission(creator, submissionAcl);
    }

    @Override
    public EffectiveAcl getEffectiveAcl() {
        return new EffectiveAcl(submissionAcl, OWNER);
    }
}
