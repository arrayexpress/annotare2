/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place;

import com.google.gwt.place.shared.Place;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.ExperimentTab;

/**
 * @author Olga Melnichuk
 */
public abstract class ExperimentPlace extends Place {

    public abstract ExperimentTab getSelectedTab();

    public static ExperimentPlace create(ExperimentTab tab) {
        //TODO
        switch (tab) {
            case EXP_DESCRIPTION:
                return new IdfPlace();
            case EXP_DESIGN:
                return new SdrfPlace();
            default:
                throw new IllegalStateException("Unknown experiment tab:" + tab);
        }
    }
}
