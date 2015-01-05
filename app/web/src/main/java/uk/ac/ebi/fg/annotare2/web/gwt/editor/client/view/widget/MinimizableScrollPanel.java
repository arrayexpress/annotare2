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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Olga Melnichuk
 */
public class MinimizableScrollPanel extends ScrollPanel {

    // TODO GWT 2.5 should support hide/show ibn DockLayoutPanel for free
    private int size;

    @Override
    public void setWidget(Widget w) {
        Widget parent = getParent();
        if (parent instanceof DockLayoutPanel) {
            DockLayoutPanel layoutPanel = (DockLayoutPanel) parent;
            if (w == null) {
                layoutPanel.setWidgetSize(this, 0);
            } else {
                layoutPanel.setWidgetSize(this, size);
            }
        }
        super.setWidget(w);
    }

    public void setSize(int size) {
        this.size = size;
    }
}
