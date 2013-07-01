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

package uk.ac.ebi.fg.annotare2.configmodel;

import com.google.common.annotations.GwtCompatible;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public enum AttributeType {
    CHARACTERISTIC_ATTRIBUTE {
        @Override
        public String getName(String name) {
            return "Characteristic[" + name + "]";
        }
    },
    FACTOR_VALUE_ATTRIBUTE {
        @Override
        public String getName(String name) {
            return "Factor Value[" + name + "]";
        }
    },
    MATERIAL_TYPE_ATTRIBUTE,
    COMMENT_ATTRIBUTE;

    public String getName(String name) {
        return name;
    }

    public boolean isFactorValue() {
        return this == FACTOR_VALUE_ATTRIBUTE;
    }

    public boolean isCharacteristic() {
        return this == CHARACTERISTIC_ATTRIBUTE;
    }

    public boolean isMaterialType() {
        return this == MATERIAL_TYPE_ATTRIBUTE;
    }

    public boolean isComment() {
        return this == COMMENT_ATTRIBUTE;
    }
}
