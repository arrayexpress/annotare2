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

import com.google.common.base.Charsets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSubmission extends Submission {

    private String investigation;

    private String sampleAndDataRel;

    public ExperimentSubmission(User user, Acl acl) {
        super(user, acl);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void setInvestigation(String text) {
        this.investigation = text;
    }

    public void setSampleAndDataRelationship(String rel) {
        this.sampleAndDataRel = rel;
    }

    public InputStream getInvestigation() throws IOException {
        String str = (investigation == null) ? "" : investigation;
        return new ByteArrayInputStream(str.getBytes(Charsets.UTF_8));
    }

    public InputStream getSampleAndDataRelationship() {
        String str = (sampleAndDataRel == null) ? "" : sampleAndDataRel;
        return new ByteArrayInputStream(str.getBytes(Charsets.UTF_8));
    }

}
