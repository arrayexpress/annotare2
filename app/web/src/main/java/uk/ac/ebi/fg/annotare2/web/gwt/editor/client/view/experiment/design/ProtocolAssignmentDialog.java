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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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

    @UiField(provided = true)
    ListBox assignedListBox;

    @UiField(provided = true)
    ListBox availableListBox;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    @UiField
    Label errorMessage;

    @UiField
    InlineLabel protocolSubject;

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
        setText("Assign " + profile.getProtocolName() + " to " + profile.getProtocolSubjectType() + "s...");

        createDialog1(profile);
    }

    private void createDialog1(final ProtocolAssignmentProfile profile) {
        if (!profile.isDefault()) {
            createDialog2(profile);
            return;
        }

        Button changeButton = new Button("Change");
        changeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createDialog2(profile);
            }
        });
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setSpacing(5);
        buttonPanel.add(cancelButton);
        buttonPanel.add(changeButton);

        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setWidth("100%");
        hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        hPanel.add(buttonPanel);
        hPanel.getElement().getStyle().setMarginTop(5, Style.Unit.PX);

        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.add(new Label("Protocol '" + profile.getProtocolName() +
                "' is assigned to all " + profile.getProtocolSubjectType() +
                "s by default. Do you want to change this behavior?"));
        panel.add(hPanel);
        setWidget(panel);
        center();
    }

    private void createDialog2(ProtocolAssignmentProfile profile) {
        assignedListBox = new ListBox(true);
        availableListBox = new ListBox(true);
        setWidget(Binder.BINDER.createAndBindUi(this));
        center();
        protocolSubject.setText(profile.getProtocolSubjectType());
        updateListBoxes();
    }

    @UiHandler(value = {"assignedListBox", "availableListBox"})
    void listBoxChanged(ChangeEvent event) {
        hideError();
    }

    @UiHandler("cancelButton")
    void cancelClicked(ClickEvent event) {
        hide();
    }

    @UiHandler("okButton")
    void okClicked(ClickEvent event) {
        ProtocolAssignmentProfileUpdates updates = getAssignmentUpdates();
        if (updates.getAssignments().isEmpty()) {
            showError("Assigned list can't be empty");
            return;
        }
        hide();
        if (callback != null) {
            callback.onOkay(updates);
        }
    }

    @UiHandler("removeButton")
    void removeButtonClicked(ClickEvent event) {
        assign(getSelected(assignedListBox), false);
    }

    @UiHandler("addButton")
    void addButtonClicked(ClickEvent event) {
        assign(getSelected(availableListBox), true);
    }

    private ProtocolAssignmentProfileUpdates getAssignmentUpdates() {
        Set<String> assignments = new HashSet<String>();
        for (int i = 0; i < assignedListBox.getItemCount(); i++) {
            assignments.add(assignedListBox.getValue(i));
        }
        return new ProtocolAssignmentProfileUpdates(protocolId, assignments);
    }

    private void assign(Set<String> assigned, boolean assign) {
        for (String key : assignments.keySet()) {
            if (assigned.contains(key)) {
                assignments.put(key, assign);
            }
        }
        updateListBoxes();
    }

    private Set<String> getSelected(ListBox listBox) {
        Set<String> selected = new HashSet<String>();
        for (int i = 0, len = listBox.getItemCount(); i < len; i++) {
            if (listBox.isItemSelected(i)) {
                selected.add(listBox.getValue(i));
            }
        }
        return selected;
    }

    private void hideError() {
        showError(null);
    }

    private void showError(String msg) {
        errorMessage.setText(msg == null ? "" : "Error:" + msg);
    }

    private void updateListBoxes() {
        assignedListBox.clear();
        availableListBox.clear();

        for (String id : assignments.keySet()) {
            boolean assigned = assignments.get(id);
            ListBox listBox = assigned ? assignedListBox : availableListBox;
            listBox.addItem(names.get(id), id);
        }
    }
}
