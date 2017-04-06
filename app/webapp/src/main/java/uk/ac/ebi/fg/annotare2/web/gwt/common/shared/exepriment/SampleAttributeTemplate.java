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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import uk.ac.ebi.fg.annotare2.core.data.SystemEfoTerm;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttributeType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.util.ValueRange;

import java.util.Collection;
import java.util.EnumSet;

import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.*;
import static uk.ac.ebi.fg.annotare2.core.data.SystemEfoTerm.*;
import static uk.ac.ebi.fg.annotare2.submission.model.SampleAttributeType.*;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.util.ValueRange.one;

/**
 * @author Olga Melnichuk
 */
public enum SampleAttributeTemplate {

    MATERIAL_TYPE_ATTRIBUTE("Material Type", of(MATERIAL_TYPE), true, true),
    PROVIDER_ATTRIBUTE("Provider", of(PROVIDER)),

    ORGANISM_ATTRIBUTE(ORGANISM, true, true),
    ORGANISM_PART_ATTRIBUTE(ORGANISM_PART),
    STRAIN_ATTRIBUTE(STRAIN),
    DISEASE_ATTRIBUTE(DISEASE),
    GENOTYPE_ATTRIBUTE(GENOTYPE),
    RNA_INTERFERENCE_ATTRIBUTE(RNA_INTERFERENCE,of(FACTOR_VALUE)),
    CULTIVAR_ATTRIBUTE(CULTIVAR),
    DIET_ATTRIBUTE(DIET),
    INFECT_ATTRIBUTE(INFECT),
    INJURY_ATTRIBUTE(INJURY),
    RESPONSE_TO_TREATMENT_ATTRIBUTE(RESPONSE_TO_TREATMENT),
    SAMPLING_SITE_ATTRIBUTE(SAMPLING_SITE),
    ENVIRONMENTAL_STRESS_ATTRIBUTE(ENVIRONMENTAL_STRESS),
    SINGLE_CELL_WELL_QUANTITY_ATTRIBUTE(SINGLE_CELL_WELL_QUALITY),
    XENOGRAFT_ATTRIBUTE(XENOGRAFT),
    REPLICATE_ATTRIBUTE(REPLICATE),
    DESCRIPTION_ATTRIBUTE("Description", of(DESCRIPTION)),
    GENETIC_MODIFICATION_ORIGIN(GENETIC_MODIFICATION),

    AGE_ATTRIBUTE(AGE, ValueRange.<SystemEfoTerm>any()),

    TIME_ATTRIBUTE(TIME, ValueRange.<SystemEfoTerm>any()),

    CELL_LINE_ORIGIN(CELL_LINE),
    CELL_TYPE_ORIGIN(CELL_TYPE),
    DEVELOPMENTAL_STAGE_ORIGIN(DEVELOPMENTAL_STAGE),
    INDIVIDUAL_ORIGIN(INDIVIDUAL),
    SEX_ORIGIN(SEX),
    GROWTH_CONDITION_ORIGIN(GROWTH_CONDITION),

    DOSE_ORIGIN(DOSE, ValueRange.<SystemEfoTerm>any(), of(FACTOR_VALUE)),
    IMMUNOPRECIPITATE_ORIGIN(IMMUNOPRECIPITATE, of(FACTOR_VALUE)),
    TREATMENT_ATTRIBUTE("Treatment", of(FACTOR_VALUE), false, false),
    COMPOUND_ATTRIBUTE("Compound", of(FACTOR_VALUE)),

    STIMULUS_ATTRIBUTE("Stimulus", range(CHARACTERISTIC, CHARACTERISTIC_AND_FACTOR_VALUE)),
    CLINICAL_HISTORY_ATTRIBUTE(CLINICAL_HISTORY),
    DISEASE_STAGING_ATTRIBUTE(DISEASE_STAGING),
    ECOTYPE_ATTRIBUTE(ECOTYPE),
    IRRADIATE_ATTRIBUTE(IRRADIATE, of(FACTOR_VALUE)),
    FRACTION_ATTRIBUTE(FRACTION),
    KARYOTYPE_ATTRIBUTE(KARYOTYPE),
    PHENOTYPE_ATTRIBUTE(PHENOTYPE),
    TUMOR_GRADING_ATTRIBUTE(TUMOR_GRADING),

    USER_DEFIED_ATTRIBUTE(ValueRange.<String>any(), ValueRange.<SystemEfoTerm>any(), ValueRange.<SystemEfoTerm>any(), range(CHARACTERISTIC, FACTOR_VALUE), false, true);

    private final ValueRange<String> nameRange;
    private final ValueRange<SystemEfoTerm> termRange;
    private final ValueRange<SystemEfoTerm> unitRange;

    private final EnumSet<SampleAttributeType> typeRange;
    private final boolean isMandatory;
    private final boolean isVisible;

    SampleAttributeTemplate(SystemEfoTerm term, EnumSet<SampleAttributeType> typeRange) {
        this(term, ValueRange.<SystemEfoTerm>none(), typeRange);
    }

    SampleAttributeTemplate(SystemEfoTerm term, ValueRange<SystemEfoTerm> unitRange, EnumSet<SampleAttributeType> typeRange) {
        this(term, unitRange, typeRange, false, true);
    }

    SampleAttributeTemplate(SystemEfoTerm term, ValueRange<SystemEfoTerm> unitRange) {
        this(term, unitRange, false, true);
    }

    SampleAttributeTemplate(SystemEfoTerm term) {
        this(term, false, true);
    }

    SampleAttributeTemplate(SystemEfoTerm term, boolean isMandatory, boolean isVisible) {
        this(term, ValueRange.<SystemEfoTerm>none(), isMandatory, isVisible);
    }

    SampleAttributeTemplate(SystemEfoTerm term, ValueRange<SystemEfoTerm> unitRange, boolean isMandatory, boolean isVisible) {
        this(term, unitRange, range(CHARACTERISTIC, CHARACTERISTIC_AND_FACTOR_VALUE), isMandatory, isVisible);
    }

    SampleAttributeTemplate(SystemEfoTerm term, ValueRange<SystemEfoTerm> unitRange,
                            EnumSet<SampleAttributeType> typeRange, boolean isMandatory, boolean isVisible) {
        this(one(term.getName()), one(term), unitRange, typeRange, isMandatory, isVisible);
    }

    SampleAttributeTemplate(String nameRange, EnumSet<SampleAttributeType> typeRange) {
        this(nameRange, typeRange, false, true);
    }

    SampleAttributeTemplate(String nameRange, EnumSet<SampleAttributeType> typeRange, boolean isMandatory, boolean isVisible) {
        this(one(nameRange), ValueRange.<SystemEfoTerm>none(), ValueRange.<SystemEfoTerm>none(), typeRange, isMandatory, isVisible);
    }

    SampleAttributeTemplate(ValueRange<String> nameRange, ValueRange<SystemEfoTerm> termRange,
                            ValueRange<SystemEfoTerm> unitRange, EnumSet<SampleAttributeType> typeRange,
                            boolean isMandatory, boolean isVisible) {
        this.nameRange = checkNotNull(nameRange);
        this.termRange = checkNotNull(termRange);
        this.unitRange = checkNotNull(unitRange);
        this.typeRange = checkNotNull(typeRange);
        this.isMandatory = isMandatory;
        this.isVisible = isVisible;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Collection<SampleAttributeType> getTypes() {
        return unmodifiableSet(typeRange);
    }

    public static SampleAttributeTemplate parse(String string) {
        try {
            return valueOf(string);
        } catch (NullPointerException e) {
            return USER_DEFIED_ATTRIBUTE;
        }
    }

    private static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException("null");
        }
        return t;
    }

    public String getName() {
        return nameRange.isSingleton() ? nameRange.get() : name();
    }

    public ValueRange<String> getNameRange() {
        return nameRange;
    }

    public ValueRange<SystemEfoTerm> getTermRange() {
        return termRange;
    }

    public ValueRange<SystemEfoTerm> getUnitRange() {
        return unitRange;
    }

    public boolean hasUnits() {
        return !unitRange.isNone();
    }

    public static Collection<SampleAttributeTemplate> getAll() {
        EnumSet<SampleAttributeTemplate> allTemplates = allOf(SampleAttributeTemplate.class);
        allTemplates.remove(USER_DEFIED_ATTRIBUTE);
        return allTemplates;
    }

    public boolean isFactorValueOnly() {
        return getTypes().size() == 1 && getTypes().iterator().next().isFactorValue();
    }
}
