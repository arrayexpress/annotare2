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

package uk.ac.ebi.fg.annotare2.submission.model;

import java.io.Serializable;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtract implements Serializable {

    private Extract extract;
    private Integer extractId;

    private Label label;
    private Integer labelId;

    private String id;

    LabeledExtract() {
        /* used by GWT serialization only */
        this(null, null);
    }

    public LabeledExtract(Extract extract) {
        this(extract, null);
    }

    public LabeledExtract(Extract extract, Label label) {
        this.extract = extract;
        this.label = label;
        this.id = generateId(extract, label);
    }

    public Extract getExtract() {
        return extract;
    }

    public Label getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return extract.getName() + (label != null && !label.isEmpty() ? ":" + label.getName() : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LabeledExtract that = (LabeledExtract) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    void restoreObjects(ExperimentProfile exp) {
        extract = exp.getExtract(extractId);
        if (extract == null) {
            throw new IllegalStateException("Assay can't exist without extract; (cause: extract with id=" +
                    extractId + " was not found in experiment profile)");
        }
        extractId = null;

        label = exp.getLabel(labelId);
        labelId = null;
    }

    static String generateId(Extract extract, Label label) {
        return extract == null ? null :
                (extract.getId() + (label != null ? "_" + label.getId() : ""));
    }
}

