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

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
@Deprecated
public abstract class AttributeValueType implements Serializable {

    private AttributeValueSubType valueSubType;

    protected AttributeValueType() {
    /* used by GWT serialization */
    }

    protected AttributeValueType(AttributeValueSubType valueSubType) {
        this.valueSubType = valueSubType;
    }

    public void set(SampleAttribute attribute) {
       // attribute.setValueSubType(valueSubType);
    }

    public abstract void visit(Visitor visitor);

    public interface Visitor {
        void visitTextValueType(TextAttributeValueType valueType);

        void visitNumericValueType(NumericAttributeValueType valueType);

        void visitTermValueType(TermAttributeValueType valueType);
    }
}
