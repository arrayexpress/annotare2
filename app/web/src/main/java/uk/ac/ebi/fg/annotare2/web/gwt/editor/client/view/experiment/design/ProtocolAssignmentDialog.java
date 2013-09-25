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
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolAssignmentProfileUpdates;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget.DialogCallback;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
    ListBox availableListBox;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    private DialogCallback<ProtocolAssignmentProfileUpdates> callback;
    private final int protocolId;
    private Map<String, Boolean> assignments;
    private Map<String, String> names;

    public ProtocolAssignmentDialog(ProtocolAssignmentProfile profile, DialogCallback<ProtocolAssignmentProfileUpdates> callback) {
        this.callback = callback;
        this.protocolId = profile.getProtocolId();
        this.assignments = new LinkedHashMap<String, Boolean>(profile.getAssignments());
        this.names = new LinkedHashMap<String, String>(profile.getNames());

        setModal(true);
        setGlassEnabled(true);
        setText("Assign " + profile.getProtocolName() + " to...");

        setWidget(Binder.BINDER.createAndBindUi(this));

        center();
        updateListBoxes();
    }

    @UiHandler("cancelButton")
    void cancelClicked(ClickEvent event) {
        hide();
    }

    @UiHandler("okButton")
    void okClicked(ClickEvent event) {
        hide();
        if (callback != null) {
            callback.onOkay(getAssignmentUpdates());
        }
    }

    @UiHandler("removeButton")
    void removeButtonClicked(ClickEvent event) {
        //TODO
    }

    @UiHandler("addButton")
    void addButtonClicked(ClickEvent event) {
        //TODO
    }

    private ProtocolAssignmentProfileUpdates getAssignmentUpdates() {
        Set<String> assignments = new HashSet<String>();
        for(int i= 0; i < assignedListBox.getItemCount(); i++) {
            assignments.add(assignedListBox.getValue(i));
        }
        return new ProtocolAssignmentProfileUpdates(protocolId, assignments);
    }

    private void updateListBoxes() {
        assignedListBox.clear();
        availableListBox.clear();

        for(String id : assignments.keySet()) {
            boolean assigned = assignments.get(id);
            if (assigned) {
                assignedListBox.addItem(names.get(id), id);
            } else {
                availableListBox.addItem(names.get(id), id);
            }
        }
    }
}
