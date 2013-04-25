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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SampleAttribute {

    public static enum Type {
        MATERIAL_TYPE("Material Type", "Material Type", false),
        ORGANISM("Organism", "Organism"),
        ORGANISM_PART("Organism Part", "OrganismPart"),
        DISEASE_STATE("Disease State", "DiseaseState"),
        AGE("Age", "Age"),
        OTHER("Other", null);

        private final String title;
        private final String sdrfName;
        private final boolean isCharacteristic;

        private Type(String title, String sdrfName) {
            this(title, sdrfName, true);
        }

        private Type(String title, String sdrfName, boolean isCharacteristic) {
            this.title = title;
            this.sdrfName = sdrfName;
            this.isCharacteristic = isCharacteristic;
        }

        public String getTitle() {
            return title;
        }

        public String getActualName(String name, boolean isFactorValue) {
            String actualName = this == OTHER ? name : sdrfName;
            if (!isCharacteristic) {
                return actualName;
            }
            return (isFactorValue ? "Factor Value" : "Characteristic") + "[" + actualName + "]";
        }

        public SampleAttribute createAttribute() {
            return new SampleAttribute(this, this == OTHER ? null : title);
        }

        public static List<Type> sampleAttributeTypes() {
            List<Type> types = new ArrayList<Type>();
            for (Type type : values()) {
                if (type != OTHER) {
                    types.add(type);
                }
            }
            return types;
        }
    }

    public static enum Unit {
        NONE("None"),
        YEARS("Years"),
        MONTHS("Months"),
        DAYS("Days"),
        HOURS("Hours"),
        MINUTES("Minutes"),
        SECONDS("Seconds");

        private final String title;

        private Unit(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private static String NO_NAME = "NO NAME";

    private final Type type;
    private String name;
    private Unit unit = Unit.NONE;
    private boolean isFactorValue;

    public SampleAttribute(Type type, String name) {
        if (type == null) {
            throw new IllegalArgumentException("type == null");
        }
        this.type = type;
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null || name.trim().isEmpty()) ? NO_NAME : name;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isFactorValue() {
        return isFactorValue;
    }

    public void setFactorValue(boolean factorValue) {
        isFactorValue = factorValue;
    }

    public Type getType() {
        return type;
    }

    public boolean isCustom() {
        return type == Type.OTHER;
    }

    public String getActualName() {
        return type.getActualName(name, isFactorValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleAttribute that = (SampleAttribute) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
