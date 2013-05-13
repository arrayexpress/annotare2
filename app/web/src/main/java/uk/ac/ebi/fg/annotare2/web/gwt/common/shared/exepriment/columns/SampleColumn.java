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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType.CHARACTERISTIC;
import static uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType.NEITHER;

/**
 * @author Olga Melnichuk
 */
public class SampleColumn implements IsSerializable {

    public static List<SampleColumn> DEFAULTS = new ArrayList<SampleColumn>();

    static {
        DEFAULTS.add(createDefault("Material Type", NEITHER, new EfoTermValueType("EFO_0001434")));
        DEFAULTS.add(createDefault("Organism", CHARACTERISTIC, new EfoTermValueType("EFO_0000634")));
        DEFAULTS.add(createDefault("OrganismPart", CHARACTERISTIC, new EfoTermValueType("EFO_0000635")));
    }

    private static String NO_NAME = "NEW ATTRIBUTE";

    private AttributeType type;

    private ColumnValueType valueType;

    private String name;

    private boolean isDefault;

    public SampleColumn() {
        this(null, CHARACTERISTIC, new TextValueType());
    }

    public SampleColumn(String name, AttributeType type, ColumnValueType valueType) {
        setName(name);
        this.type = type;
        this.valueType = valueType;
    }

    public SampleColumn(SampleColumn template) {
        name = template.name;
        type = template.type;
        valueType = template.valueType;
        isDefault = template.isDefault;
    }

    private static SampleColumn createDefault(String name, AttributeType type, ColumnValueType valueType) {
        SampleColumn column = new SampleColumn(name, type, valueType);
        column.isDefault = true;
        return column;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name == null || name.trim().isEmpty() ? NO_NAME : name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public AttributeType getType() {
        return type;
    }

    private void setType(AttributeType type) {
        this.type = type;
    }

    public ColumnValueType getValueType() {
        return valueType;
    }

    private void setValueType(ColumnValueType valueType) {
        this.valueType = valueType;
    }

    public Editor editor() {
        return new Editor(this);
    }

    public static class Editor {

        private SampleColumn column;

        public Editor(SampleColumn column) {
            this.column = new SampleColumn(column);
        }

        public boolean isDefault() {
            return column.isDefault();
        }

        public String getName() {
            return column.getName();
        }

        public void setName(String name) {
            column.setName(name);
        }

        public AttributeType getType() {
            return column.getType();
        }

        public void setType(AttributeType type) {
            column.setType(type);
        }

        public ColumnValueType getValueType() {
            return column.getValueType();
        }

        public void setValueType(ColumnValueType valueType) {
            column.setValueType(valueType);
        }

        public SampleColumn copy() {
            return new SampleColumn(column);
        }
    }
}
