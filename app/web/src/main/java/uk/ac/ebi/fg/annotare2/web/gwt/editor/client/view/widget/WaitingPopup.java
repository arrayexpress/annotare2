/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Olga Melnichuk
 */
public class WaitingPopup extends PopupPanel {

    private WaitingPanel panel;

    public WaitingPopup(String message) {
        super(false, true);
        panel = new WaitingPanel(message);
        setWidget(panel);
    }

    public void showError(Throwable caught) {
        panel.showError(caught);
    }

    public void showSuccess(String message) {
        panel.showSuccess(message);
    }

    public void positionAtWindowCenter(){
        setPopupPosition(Window.getClientWidth() / 2, Window.getClientHeight() / 2);
    }
}
