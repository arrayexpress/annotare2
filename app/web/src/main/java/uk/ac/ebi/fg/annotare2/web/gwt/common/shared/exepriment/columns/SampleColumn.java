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
import uk.ac.ebi.fg.annotare2.submission.model.AttributeType;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;

import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.fg.annotare2.submission.model.AttributeType.*;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTerm.*;

/**
 * @author Olga Melnichuk
 */
public class SampleColumn implements IsSerializable {

    private int id;

    private String name;

    private OntologyTerm term;

    private AttributeType type;

    private ColumnValueType valueType;

    private boolean isEditable = true;

    SampleColumn() {
        /* used by GWT serialization only */
    }

    private SampleColumn(int id, String name, OntologyTerm term, AttributeType type, ColumnValueType valueType, boolean isEditable) {
        setName(name);
        this.id = id;
        this.term = term;
        this.type = type;
        this.valueType = valueType;
        this.isEditable = isEditable;
    }

    public int getId() {
        return id;
    }

    public String getPrettyName() {
        return valueType.getColumnName(getName());
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        String NO_NAME = "NEW ATTRIBUTE";
        this.name = name == null || name.trim().isEmpty() ? NO_NAME : name;
    }

    public OntologyTerm getTerm() {
        return term;
    }

    private void setTerm(OntologyTerm term) {
        this.term = term;
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

    public static SampleColumn create(int id, String name, OntologyTerm term) {
        return create(id, name, term, CHARACTERISTIC_ATTRIBUTE, new TextValueType(), true);
    }

    public static SampleColumn create(int id, SampleColumn template) {
        return create(id, template.name, template.term, template.type, template.valueType, template.isEditable);
    }

    public static SampleColumn create(int id, String name, OntologyTerm term, AttributeType type, ColumnValueType valueType, boolean isEditable) {
        return new SampleColumn(id, name, term, type, valueType, isEditable);
    }

    private static SampleColumn createTemplate(String name, OntologyTerm term, ColumnValueType valueType) {
        return createTemplate(name, term, CHARACTERISTIC_ATTRIBUTE, valueType);
    }

    private static SampleColumn createTemplate(String name, OntologyTerm term, AttributeType type, ColumnValueType valueType) {
        if (valueType instanceof OntologyTermValueType &&
                ((OntologyTermValueType) valueType).getEfoTerm() == null) {
            return null;
        }
        return create(0, name, term, type, valueType, false);
    }

    public static List<SampleColumn> getTemplateColumns(SystemEfoTermMap result) {
        List<SampleColumn> templates = new ArrayList<SampleColumn>();
        templates.add(
                createTemplate(
                        MATERIAL_TYPE.getFriendlyName(),
                        result.getEfoTerm(MATERIAL_TYPE),
                        MATERIAL_TYPE_ATTRIBUTE,
                        new OntologyTermValueType(result.getEfoTerm(MATERIAL_TYPE))));
        templates.add(
                createTemplate(
                        ORGANISM.getFriendlyName(),
                        result.getEfoTerm(ORGANISM),
                        new OntologyTermValueType(result.getEfoTerm(ORGANISM))));
        templates.add(
                createTemplate(
                        ORGANISM_PART.getFriendlyName(),
                        result.getEfoTerm(ORGANISM_PART),
                        new OntologyTermValueType(result.getEfoTerm(ORGANISM_PART))));
        templates.add(
                createTemplate(
                        STRAIN.getFriendlyName(),
                        result.getEfoTerm(STRAIN),
                        new TextValueType()));
        templates.add(
                createTemplate(
                        DISEASE.getFriendlyName(),
                        result.getEfoTerm(DISEASE),
                        new OntologyTermValueType(result.getEfoTerm(DISEASE))));
        templates.add(
                createTemplate(
                        GENOTYPE.getFriendlyName(),
                        result.getEfoTerm(GENOTYPE),
                        new OntologyTermValueType(result.getEfoTerm(GENOTYPE))));
        templates.add(
                createTemplate(
                        AGE.getFriendlyName(),
                        result.getEfoTerm(AGE),
                        new NumericValueType(null)));
        templates.add(
                createTemplate(
                        CELL_LINE.getFriendlyName(),
                        result.getEfoTerm(CELL_LINE),
                        new OntologyTermValueType(result.getEfoTerm(CELL_LINE))));
        templates.add(
                createTemplate(
                        CELL_TYPE.getFriendlyName(),
                        result.getEfoTerm(CELL_TYPE),
                        new OntologyTermValueType(result.getEfoTerm(CELL_TYPE))));
        templates.add(
                createTemplate(
                        DEVELOPMENTAL_STAGE.getFriendlyName(),
                        result.getEfoTerm(DEVELOPMENTAL_STAGE),
                        new OntologyTermValueType(result.getEfoTerm(DEVELOPMENTAL_STAGE))));
        templates.add(
                createTemplate(
                        GENETIC_MODIFICATION.getFriendlyName(),
                        result.getEfoTerm(GENETIC_MODIFICATION),
                        new OntologyTermValueType(result.getEfoTerm(GENETIC_MODIFICATION))));
        templates.add(
                createTemplate(
                        ENVIRONMENTAL_HISTORY.getFriendlyName(),
                        result.getEfoTerm(ENVIRONMENTAL_HISTORY),
                        new OntologyTermValueType(result.getEfoTerm(ENVIRONMENTAL_HISTORY))));
        templates.add(
                createTemplate(
                        INDIVIDUAL.getFriendlyName(),
                        result.getEfoTerm(INDIVIDUAL),
                        new OntologyTermValueType(result.getEfoTerm(INDIVIDUAL))));
        templates.add(
                createTemplate(
                        SEX.getFriendlyName(),
                        result.getEfoTerm(SEX),
                        new OntologyTermValueType(result.getEfoTerm(SEX))));
        templates.add(
                createTemplate(
                        SPECIMEN_WITH_KNOWN_STORAGE_STATE.getFriendlyName(),
                        result.getEfoTerm(SPECIMEN_WITH_KNOWN_STORAGE_STATE),
                        new OntologyTermValueType(result.getEfoTerm(SPECIMEN_WITH_KNOWN_STORAGE_STATE))));
        templates.add(
                createTemplate(
                        GROWTH_CONDITION.getFriendlyName(),
                        result.getEfoTerm(GROWTH_CONDITION),
                        new OntologyTermValueType(result.getEfoTerm(GROWTH_CONDITION))));
        templates.add(
                createTemplate(
                        "Provider",
                        null,
                        PROVIDER_ATTRIBUTE,
                        new TextValueType()));
        templates.add(
                createTemplate(
                        "Description",
                        null,
                        DESCRIPTION_ATTRIBUTE,
                        new TextValueType()));

        List<SampleColumn> filteredTemplates = new ArrayList<SampleColumn>();
        for (SampleColumn column : templates) {
            if (column != null) {
                filteredTemplates.add(column);
            }
        }
        return filteredTemplates;
    }

    public static class Editor {

        private SampleColumn column;

        public Editor(SampleColumn column) {
            this.column = SampleColumn.create(column.id, column);
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

        public OntologyTerm getTerm() {
            return column.getTerm();
        }

        public void setTerm(OntologyTerm term) {
            column.setTerm(term);
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
            return SampleColumn.create(column.id, column);
        }
    }
}
