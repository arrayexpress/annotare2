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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;

/**
 * @author Olga Melnichuk
 */
public class ProtocolAssignmentDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, ProtocolAssignmentDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button okButton;

    @UiField
    Button cancelButton;

    @UiField
    ListBox assignedListBox;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    @UiField
    ListBox availableListBox;

    public ProtocolAssignmentDialog(ProtocolRow row) {
        setModal(true);
        setGlassEnabled(true);
        setText("Assign " + row.getName() + " to...");

        setWidget(Binder.BINDER.createAndBindUi(this));

        center();
    }

    @UiHandler("cancelButton")
    void cancelClicked(ClickEvent event) {
        hide();
    }

    @UiHandler("okButton")
    void okClicked(ClickEvent event) {
        hide();
    }

    @UiHandler("removeButton")
    void removeButtonClicked(ClickEvent event) {
        //TODO
    }

    @UiHandler("addButton")
    void addButtonClicked(ClickEvent event) {
        //TODO
    }
}
