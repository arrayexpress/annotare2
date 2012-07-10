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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.EditorAppLayout;
import uk.ac.ebi.fg.annotare2.web.gwt.user.client.view.widget.AppLayout;

/**
 * @author Olga Melnichuk
 */
public class EditorApp implements EntryPoint {

    private EditorAppLayout appWidget = new EditorAppLayout();

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    private void loadModule(HasWidgets root) {
        root.add(appWidget);
    }
}
