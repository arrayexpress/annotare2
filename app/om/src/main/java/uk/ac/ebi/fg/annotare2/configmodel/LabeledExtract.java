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

package uk.ac.ebi.fg.annotare2.configmodel;

import com.google.common.annotations.GwtCompatible;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
@GwtCompatible
public class LabeledExtract implements Serializable {

    private String id;
    private Extract extract;
    private String label;

    LabeledExtract() {
        /*used by GWT serialization */
    }

    public LabeledExtract(String id, Extract extract, String label) {
        this.id = id;
        this.extract = extract;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Extract getExtract() {
        return extract;
    }

    public String getName() {
        return extract.getName() + ":" + label;
    }
}

