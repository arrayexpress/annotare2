/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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
public enum SampleAttributeType {
    CHARACTERISTIC("Sample Characteristic"),
    CHARACTERISTIC_AND_FACTOR_VALUE("Sample Characteristic and Experimental Variable"),
    FACTOR_VALUE("Experimental Variable"),
    MATERIAL_TYPE("Material Type"),
    PROVIDER("Provider"),
    DESCRIPTION("Description");

    private final String name;

    SampleAttributeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isFactorValueOnly() {
        return this == FACTOR_VALUE;
    }

    public boolean isFactorValue() {
        return this == CHARACTERISTIC_AND_FACTOR_VALUE || isFactorValueOnly();
    }

    public boolean isCharacteristic() {
        return this == CHARACTERISTIC_AND_FACTOR_VALUE ||
                this == CHARACTERISTIC;
    }

    public boolean isMaterialType() {
        return this == MATERIAL_TYPE;
    }

    public boolean isProvider() {
        return this == PROVIDER;
    }

    public boolean isDescription() {
        return this == DESCRIPTION;
    }
}
