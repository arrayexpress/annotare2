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

package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class UpdateSampleColumnsCommand implements UpdateCommand {

    private List<SampleColumn> columns;

    UpdateSampleColumnsCommand() {
        /* used by GWT serializer only */
    }

    public UpdateSampleColumnsCommand(List<SampleColumn> columns) {
        this.columns = new ArrayList<SampleColumn>(columns);
    }

    @Override
    public void execute(UpdatePerformer performer) {
        performer.updateSampleColumns(columns);
    }

    @Override
    public boolean isCritical() {
        return true;
    }
}
