/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.gwt.widgets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressTrackerPanel extends ComplexPanel {

    public interface Resources extends ClientBundle {

        @Source(Style.DEFAULT_CSS)
        Style trackerStyle();
    }

    @ImportedWithPrefix("app-ProgressTracker")
    public interface Style extends CssResource {
        String DEFAULT_CSS = "ProgressTracker.css";

        String trackerContainer();

        String trackerMilestone();
    }

    public static Resources createResources() {
        return GWT.create(Resources.class);
    }

    private final Resources resources;

    @SuppressWarnings("unused")
    public ProgressTrackerPanel() {
        this(createResources());
    }

    public ProgressTrackerPanel(Resources resources) {
        this.resources = resources;
        setElement(Document.get().createOLElement());
        setStyleName(resources.trackerStyle().trackerContainer());
    }

    @UiChild(tagname = "milestone")
    public void addMilestone(Widget w, String title) {
        add(new ProgressTrackerMilestone(title, resources));
    }

    private static class ProgressTrackerMilestone extends SimplePanel {

        private final Resources resources;

        public ProgressTrackerMilestone(String title, Resources resources) {
            this.resources = resources;
            setElement(Document.get().createLIElement());
            setStyleName(resources.trackerStyle().trackerMilestone());
        }
    }
}
