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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.HasIdentity;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermsDto;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType.CHARACTERISTIC;
import static uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType.MATERIAL_TYPE;

/**
 * @author Olga Melnichuk
 */
public class SampleColumn implements IsSerializable, HasIdentity {

    private static String NO_NAME = "NEW ATTRIBUTE";

    private int id;

    private int tmpId;

    private String name;

    private AttributeType type;

    private ColumnValueType valueType;

    private boolean isEditable = true;

    SampleColumn() {
        /* used by GWT serialization only */
    }

    public SampleColumn(int id, String name) {
        this(id, name, CHARACTERISTIC, new TextValueType(), true);
    }

    public SampleColumn(int id, SampleColumn template) {
        this(id, template.getId(), template);
    }

    public SampleColumn(int id, int tmpId, SampleColumn template) {
        this(id, template.name, template.type, template.valueType, template.isEditable);
        this.tmpId = tmpId;
    }

    public SampleColumn(int id, String name, AttributeType type, ColumnValueType valueType, boolean isEditable) {
        setName(name);
        this.id = id;
        this.tmpId = id;
        this.type = type;
        this.valueType = valueType;
        this.isEditable = isEditable;
    }

    public int getId() {
        return id;
    }

    public int getTmpId() {
        return tmpId;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name == null || name.trim().isEmpty() ? NO_NAME : name;
    }

    public boolean isEditable() {
        return isEditable;
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

    public static List<SampleColumn> getTemplateColumns(SystemEfoTermsDto result) {
        List<SampleColumn> templates = new ArrayList<SampleColumn>();
        templates.add(new SampleColumn(0, "Material Type", MATERIAL_TYPE, new EfoTermValueType(result.getMaterialTypeTerm()), false));
        templates.add(new SampleColumn(0, "Organism", CHARACTERISTIC, new EfoTermValueType(result.getOrganismTerm()), false));
        templates.add(new SampleColumn(0, "Organism Part", CHARACTERISTIC, new EfoTermValueType(result.getOrganismPartTerm()), false));
        return templates;
    }

    public static class Editor {

        private SampleColumn column;

        public Editor(SampleColumn column) {
            this.column = new SampleColumn(column.id, column);
        }

        public boolean isEditable() {
            return column.isEditable();
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
            return new SampleColumn(column.id, column);
        }
    }
}
