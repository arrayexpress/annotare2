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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermsDto;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType.CHARACTERISTIC;
import static uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType.NEITHER;

/**
 * @author Olga Melnichuk
 */
public class SampleColumn implements IsSerializable {

    private static String NO_NAME = "NEW ATTRIBUTE";

    private AttributeType type;

    private ColumnValueType valueType;

    private String name;

    private boolean isEditable = true;

    public SampleColumn() {
        this(null, CHARACTERISTIC, new TextValueType(), true);
    }

    public SampleColumn(SampleColumn template) {
        this(template.name, template.type, template.valueType, template.isEditable);
    }

    public SampleColumn(String name, AttributeType type, ColumnValueType valueType, boolean isEditable) {
        setName(name);
        this.type = type;
        this.valueType = valueType;
        this.isEditable = isEditable;
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
        templates.add(new SampleColumn("Material Type", NEITHER, new EfoTermValueType(result.getMaterialTypeTerm()), false));
        templates.add(new SampleColumn("Organism", CHARACTERISTIC, new EfoTermValueType(result.getOrganismTerm()), false));
        templates.add(new SampleColumn("OrganismPart", CHARACTERISTIC, new EfoTermValueType(result.getOrganismPartTerm()), false));
        return templates;
    }

    public static class Editor {

        private SampleColumn column;

        public Editor(SampleColumn column) {
            this.column = new SampleColumn(column);
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
            return new SampleColumn(column);
        }
    }
}
