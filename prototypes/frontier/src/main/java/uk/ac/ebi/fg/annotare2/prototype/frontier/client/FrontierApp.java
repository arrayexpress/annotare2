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

package uk.ac.ebi.fg.annotare2.prototype.frontier.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class FrontierApp implements EntryPoint {

    Label helloLabel;

    public FrontierApp() {

    }

    public void onModuleLoad() {
        helloLabel = new Label();
        helloLabel.setText("Hello, world!");
        helloLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NotificationPopupPanel.warning("Hello again!", false);
            }
        });

        RootPanel content = RootPanel.get("content");
        content.add(helloLabel);

	}
}

