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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class EditorLayout extends Composite {

    interface Binder extends UiBinder<DockLayoutPanel, EditorLayout> {
    }

    @UiField
    HasOneWidget titleBarDisplay;

    @UiField
    HasOneWidget tabBarDisplay;

    @UiField
    HasOneWidget leftMenuDisplay;

    @UiField
    HasOneWidget contentDisplay;

    @UiField
    HasOneWidget logBarDisplay;

    @UiField
    SplitLayoutPanel splitPanel;

    public EditorLayout() {
        Binder uiBinder = GWT.create(Binder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    public HasOneWidget getTitleBarDisplay() {
        return titleBarDisplay;
    }

    public HasOneWidget getTabBarDisplay() {
        return tabBarDisplay;
    }

    public HasOneWidget getLeftMenuDisplay() {
        return leftMenuDisplay;
    }

    public HasOneWidget getContentDisplay() {
        return contentDisplay;
    }

    public HasOneWidget getLogBarDisplay() {
        return logBarDisplay;
    }

    public void expandLogBar(double size) {
        Widget w = splitPanel.getWidget(0);
        double widgetSize = splitPanel.getWidgetSize(w);
        if (widgetSize < size) {
            splitPanel.setWidgetSize(w, size);
        }
    }
}
