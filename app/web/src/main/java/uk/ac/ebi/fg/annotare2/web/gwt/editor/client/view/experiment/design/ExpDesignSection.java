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

import uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.LeftNavigationView;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.util.EnumSet.*;
import static uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType.SEQUENCING;
import static uk.ac.ebi.fg.annotare2.configmodel.enums.ExperimentConfigType.TWO_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
public enum ExpDesignSection implements LeftNavigationView.Section {
    SAMPLES(ExpDesignSectionType.SAMPLES, "Create your samples", allOf(ExperimentConfigType.class)),
    EXTRACTS(ExpDesignSectionType.EXTRACTS, "Create extracts and assign ENA library info", of(SEQUENCING)),
    LABELED_EXTRACTS(ExpDesignSectionType.LABELED_EXTRACTS, "Create labeled extracts and assign a label", of(TWO_COLOR_MICROARRAY)),
    RAW_FILES(ExpDesignSectionType.ARRAY_DATA_FILES, "Upload raw files", allOf(ExperimentConfigType.class)),
    PROCESSED_FILES(ExpDesignSectionType.DERIVED_ARRAY_DATA_FILES, "Upload processed files", allOf(ExperimentConfigType.class)),
    NONE(ExpDesignSectionType.NONE, "None", noneOf(ExperimentConfigType.class));

    private final String title;
    private final ExpDesignSectionType type;
    private final EnumSet applyTo;

    private ExpDesignSection(ExpDesignSectionType type, String title, EnumSet<ExperimentConfigType> applyTo) {
        this.type = type;
        this.title = title;
        this.applyTo = applyTo;
    }

    private boolean appliesTo(ExperimentConfigType type) {
        return type != null && applyTo.contains(type);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getKey() {
        return name();
    }

    public ExpDesignSectionType getType() {
        return type;
    }

    public static List<ExpDesignSection> experimentDesignSectionsFor(ExperimentConfigType type) {
        List<ExpDesignSection> list = new ArrayList<ExpDesignSection>();
        for (ExpDesignSection section : values()) {
            if (section.appliesTo(type)) {
                list.add(section);
            }
        }
        return list;
    }

    public boolean isNone() {
        return this == NONE;
    }
}
