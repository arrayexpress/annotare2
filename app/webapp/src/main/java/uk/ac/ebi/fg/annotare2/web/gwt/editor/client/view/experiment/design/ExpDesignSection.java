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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.LeftNavigationView;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.util.EnumSet.*;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.SEQUENCING;
import static uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType.TWO_COLOR_MICROARRAY;

/**
 * @author Olga Melnichuk
 */
public enum ExpDesignSection implements LeftNavigationView.Section {
    SAMPLES("Create samples, add attributes and experimental variables", allOf(ExperimentProfileType.class)),
    EXTRACTS_LIBRARY_INFO("Assign ENA library information", of(SEQUENCING), "Provide technical details of your sequencing libraries as required by the Sequence Read Archive (SRA)"),
    LABELED_EXTRACTS("Create labeled extracts and assign labels", of(TWO_COLOR_MICROARRAY), "Assign the label to each sample by removing the one thatdoes not apply. For dye-swap designs both labels can be kept."),
    PROTOCOLS("Describe protocols", allOf(ExperimentProfileType.class)),
    FILES("Assign data files", allOf(ExperimentProfileType.class)),
    NONE("None", noneOf(ExperimentProfileType.class));

    private final String title;
    private final EnumSet applyTo;
    private final String helpText;

    ExpDesignSection(String title, EnumSet<ExperimentProfileType> applyTo) {
        this.title = title;
        this.applyTo = applyTo;
        this.helpText ="";
    }

    ExpDesignSection(String title, EnumSet<ExperimentProfileType> applyTo, String helpText) {
        this.title = title;
        this.applyTo = applyTo;
        this.helpText = helpText;
    }

    private boolean appliesTo(ExperimentProfileType type) {
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

    public static List<ExpDesignSection> experimentDesignSectionsFor(ExperimentProfileType type) {
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

    @Override
    public String getHelpText() {
        return helpText;
    }
}
