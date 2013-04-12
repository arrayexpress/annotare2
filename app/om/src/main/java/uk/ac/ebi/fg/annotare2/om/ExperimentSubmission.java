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

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSubmission extends Submission {

    private String investigation;

    private String sampleAndDataRel;

    public ExperimentSubmission(User user, Acl acl) {
        super(user, acl);
    }

    public void setInvestigation(String text) {
        this.investigation = text;
    }

    public void setSampleAndDataRelationship(String rel) {
        this.sampleAndDataRel = rel;
    }

    public InputStream getInvestigation() throws IOException {
       return asStream(investigation);
    }

    public InputStream getSampleAndDataRelationship() {
        return asStream(sampleAndDataRel);
    }

    @Override
    public boolean hasNoData() {
        return isNullOrEmpty(investigation) || isNullOrEmpty(sampleAndDataRel);
    }
}
