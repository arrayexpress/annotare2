/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class LabeledExtracts implements IsSerializable {

    private  List<LabeledExtractsRow> rows;

    public LabeledExtracts() {
        /* used by GWT serialization */
    }

    public LabeledExtracts(List<LabeledExtractsRow> rows) {
        this.rows = rows;
    }

    public List<String> getLabels() {
        return new ArrayList<String>();
    }

    public List<LabeledExtractsRow> getRows() {
        return new ArrayList<LabeledExtractsRow>(rows);
    }
}