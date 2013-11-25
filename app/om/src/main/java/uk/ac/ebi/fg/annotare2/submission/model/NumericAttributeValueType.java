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

package uk.ac.ebi.fg.annotare2.submission.model;

/**
 * @author Olga Melnichuk
 */
public class NumericAttributeValueType extends AttributeValueType {

    private OntologyTerm units;

    NumericAttributeValueType() {
    /* used by GWT serialization */
    }

    public NumericAttributeValueType(OntologyTerm units) {
        super(AttributeValueSubType.NUMBER);
        this.units = units;
    }

    @Override
    public void set(SampleAttribute attribute) {
        super.set(attribute);
        attribute.setUnits(units);
    }

    @Override
    public void visit(Visitor visitor) {
        visitor.visitNumericValueType(this);
    }

    public OntologyTerm getUnits() {
        return units;
    }
}
