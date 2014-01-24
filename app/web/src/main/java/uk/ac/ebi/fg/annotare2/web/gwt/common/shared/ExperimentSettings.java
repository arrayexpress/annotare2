/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;

import java.util.Collection;

/**
 * @author Olga Melnichuk
 */
public class ExperimentSettings implements IsSerializable {

    private ExperimentProfileType experimentType;
    private String arrayDesign;
    private String label;

    protected ExperimentSettings() {
        /* used by GWT serialization */
    }

    public ExperimentSettings(ExperimentProfileType experimentType) {
        this.experimentType = experimentType;
    }

    public void setArrayDesign(String arrayDesign) {
        this.arrayDesign = arrayDesign;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ExperimentProfileType getExperimentType() {
        return experimentType;
    }

    public String getArrayDesign() {
        return arrayDesign;
    }

    public String getLabel() {
        return label;
    }

    public static ExperimentSettings create(ExperimentProfile exp) {
        Collection<String> labels = exp.getLabelNames();

        ExperimentSettings settings = new ExperimentSettings(exp.getType());
        settings.setArrayDesign(exp.getArrayDesign());
        settings.setLabel(labels.isEmpty() ? null : labels.iterator().next());
        return settings;
    }
}
