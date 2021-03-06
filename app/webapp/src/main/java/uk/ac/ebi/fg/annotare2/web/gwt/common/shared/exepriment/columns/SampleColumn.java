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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.core.data.SystemEfoTerm;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttribute;
import uk.ac.ebi.fg.annotare2.submission.model.SampleAttributeType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.SystemEfoTermMap;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.util.ValueRange;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SampleAttributeTemplate.parse;

/**
 * @author Olga Melnichuk
 */
public class SampleColumn implements IsSerializable {

    private SampleAttribute attr;

    private SampleAttributeTemplate template;

    SampleColumn() {
        /* used by GWT serialization only */
    }

    public SampleColumn(SampleAttribute attr) {
        this.attr = attr.copy();
        this.template = parse(attr.getTemplate());
    }

    public int getId() {
        return attr.getId();
    }

    public String getName() {
        return attr.getName();
    }

    public void setName(String name) {
        attr.setName(name);
    }

    public String getTitle() {
        return attr.getName() + (template.hasUnits() ? " (" + (attr.getUnits() == null ? "no units" : attr.getUnits().getLabel()) + ")" : "");
    }

    public OntologyTerm getTerm() {
        return attr.getTerm();
    }

    public void setTerm(OntologyTerm term) {
        attr.setTerm(term);
    }

    public OntologyTerm getUnits() {
        return attr.getUnits();
    }

    public void setUnits(OntologyTerm units) {
        attr.setUnits(units);
    }

    public SampleAttributeType getType() {
        return attr.getType();
    }

    public void setType(SampleAttributeType type) {
        attr.setType(type);
    }

    public SampleAttributeTemplate getTemplate() {
        return template;
    }

    public SampleColumn copy() {
        return new SampleColumn(attr);
    }

    public static SampleColumn create(SampleAttributeTemplate template, SystemEfoTermMap context, String experimentDesignType) {

        SampleAttribute attr = new SampleAttribute(0, template.name());
        attr.setType(template.getTypes().iterator().next());
        ValueRange<String> nameRange = template.getNameRange();
        if (nameRange.isSingleton()) {
            attr.setName(nameRange.get());
        }

        ValueRange<SystemEfoTerm> termRange = template.getTermRange();
        if (termRange.isSingleton()) {
            SystemEfoTerm systemTerm = termRange.get();
            OntologyTerm term = context.getEfoTerm(systemTerm);
            if (term != null) {
                attr.setTerm(term);
            }
        }

                if (isNullOrEmpty(experimentDesignType)) {
                    if (!isMaterialTypeOrOrganismAttribute(template) && !isSingleCellAttribute(template)) {
                        attr.setType(SampleAttributeType.CHARACTERISTIC_AND_FACTOR_VALUE);
                    }
                } else {
                    if (!isMaterialType(template) && !isSingleCellAttribute(template)) {
                        attr.setType(SampleAttributeType.CHARACTERISTIC_AND_FACTOR_VALUE);
                    }
                }
        return new SampleColumn(attr);
    }

    public static SampleColumn create(SampleAttributeTemplate template, SystemEfoTermMap context) {

        SampleAttribute attr = new SampleAttribute(0, template.name());
        attr.setType(template.getTypes().iterator().next());
        ValueRange<String> nameRange = template.getNameRange();
        if (nameRange.isSingleton()) {
            attr.setName(nameRange.get());
        }

        ValueRange<SystemEfoTerm> termRange = template.getTermRange();
        if (termRange.isSingleton()) {
            SystemEfoTerm systemTerm = termRange.get();
            OntologyTerm term = context.getEfoTerm(systemTerm);
            if (term != null) {
                attr.setTerm(term);
            }
        }

        return new SampleColumn(attr);
    }

    private static boolean isMaterialTypeOrOrganismAttribute(SampleAttributeTemplate template){
        return template.getName().toLowerCase().equalsIgnoreCase("material type") || template.getName().toLowerCase().equalsIgnoreCase("organism");
    }

    private static boolean isMaterialType(SampleAttributeTemplate template){
        return template.getName().toLowerCase().equalsIgnoreCase("material type");
    }

    private static boolean isSingleCellAttribute(SampleAttributeTemplate template){
        return template.getName().toLowerCase().equalsIgnoreCase("single cell identifier") ||
                template.getName().toLowerCase().equalsIgnoreCase("inferred cell type") ||
                template.getName().toLowerCase().equalsIgnoreCase("single cell well quality") ||
                template.getName().toLowerCase().equalsIgnoreCase("post analysis well quality");
    }
}
