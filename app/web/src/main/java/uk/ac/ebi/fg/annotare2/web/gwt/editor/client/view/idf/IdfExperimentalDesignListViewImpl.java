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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

import uk.ac.ebi.fg.annotare2.magetab.idf.ExperimentalDesign;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.ExperimentalDesignView;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class IdfExperimentalDesignListViewImpl extends IdfListView<ExperimentalDesign> implements IdfExperimentalDesignListView {

    @Override
    public void setExperimentalDesigns(ArrayList<ExperimentalDesign> designs) {
        for (ExperimentalDesign d : designs) {
            addExperimentalDesignView(d);
        }
    }

    private DisclosureListItem addExperimentalDesignView(ExperimentalDesign d) {
        ExperimentalDesignView view = new ExperimentalDesignView();
        view.setItem(d);
        return addListItem(view);
    }
}
