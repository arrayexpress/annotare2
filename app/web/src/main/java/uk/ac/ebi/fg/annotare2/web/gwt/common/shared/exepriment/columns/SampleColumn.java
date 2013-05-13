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
    static  {
        DEFAULTS.add(createDefault("Material Type", NEITHER, new EfoTermValueType("EFO_0001434")));
        DEFAULTS.add(createDefault("Organism", CHARACTERISTIC, new EfoTermValueType("EFO_0000634")));
        DEFAULTS.add(createDefault("OrganismPart", CHARACTERISTIC, new EfoTermValueType("EFO_0000635")));
    }

    private static String NO_NAME = "NO NAME";

    private AttributeType type;

    private ColumnValueType valueType;

    private String name;

    private boolean isDefault;

    public SampleColumn() {
        this(null, CHARACTERISTIC, new TextValueType());
    }

    public SampleColumn(String name, AttributeType type,  ColumnValueType valueType) {
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

    public void setName(String name) {
        this.name = name == null || name.trim().isEmpty() ? NO_NAME : name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public AttributeType getType() {
        return type;
    }

    public ColumnValueType getValueType() {
        return valueType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampleColumn column = (SampleColumn) o;

        if (!name.equals(column.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
