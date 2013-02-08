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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.arraydesign;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ArrayDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTab;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.EditorTabBarView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.arraydesign.ArrayDesignTab;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignTabBarActivity  extends AbstractActivity  implements EditorTabBarView.Presenter {

    private EditorTabBarView view;
    private ArrayDesignTab selectedTab;

    @Inject
    public ArrayDesignTabBarActivity(EditorTabBarView view) {
        this.view = view;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        view.initWithTabs(ArrayDesignTab.values());
        view.setPresenter(this);
        view.selectTab(selectedTab);
        containerWidget.setWidget(view.asWidget());
    }

    public ArrayDesignTabBarActivity withPlace(ArrayDesignPlace place) {
        selectedTab = place.getSelectedTab();
        return this;
    }

    @Override
    public void onTabSelect(EditorTab tab) {
        //TODO
    }
}
