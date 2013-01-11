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

import uk.ac.ebi.fg.annotare2.magetab.idf.TermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DisclosureListItem;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.TermSourceView;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class IdfTermSourceListViewImpl extends IdfListView<TermSource> implements IdfTermSourceListView {

    @Override
    public void setTermSources(ArrayList<TermSource> termSources) {
        for(TermSource ts : termSources) {
            addTermSourceView(ts);
        }
    }

    private DisclosureListItem addTermSourceView(TermSource ts) {
        TermSourceView itemView = new TermSourceView();
        itemView.setItem(ts);
        return addListItem(itemView);

        /*item.addItemSelectionHandler(new ItemSelectionEventHandler() {
            @Override
            public void onSelect(boolean selected) {
                if (selected) {
                    selection++;
                } else if (selection > 0) {
                    selection--;
                }
            }
        });*/
    }

}
