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
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SampleAttribute.Type.sampleAttributeTypes;

/**
 * @author Olga Melnichuk
 */
public class SampleAttributesDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, SampleAttributesDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    ListBox predefinedAttributes;

    @UiField
    ListBox currentAttributes;

    @UiField
    Button moveUpButton;

    @UiField
    Button moveDownButton;

    @UiField
    Button removeButton;

    @UiField
    Button addButton;

    @UiField
    Label otherLabel;

    @UiField
    SimpleLayoutPanel attributeEditor;

    private List<SampleAttribute> attributes = new ArrayList<SampleAttribute>();

    public SampleAttributesDialog() {
        setModal(true);
        setGlassEnabled(true);
        setText("Sample Attributes");

        setWidget(Binder.BINDER.createAndBindUi(this));
        center();

        updatePredefinedList();
    }

    @UiHandler("currentAttributes")
    void selectionChanged(ChangeEvent event) {
        int index = currentAttributes.getSelectedIndex();
        if (index < 0) {
            attributeEditor.setWidget(new Label("No selection"));
        } else {
            attributeEditor.setWidget(new SampleAttributeEditor(attributes.get(index)));
        }
    }

    @UiHandler("addButton")
    void addButtonClick(ClickEvent event) {
        int index = predefinedAttributes.getSelectedIndex();
        if (index < 0) {
            return;
        }
        String typeName = predefinedAttributes.getValue(index);
        SampleAttribute.Type type = SampleAttribute.Type.valueOf(typeName);
        predefinedAttributes.removeItem(index);
        addAttribute(type);
    }

    @UiHandler("removeButton")
    void removeButtonClick(ClickEvent event) {
        int index = currentAttributes.getSelectedIndex();
        if (index < 0) {
            return;
        }
        removeAttribute(index);
    }

    @UiHandler("otherLabel")
    void newAttributeClick(ClickEvent event) {
        addAttribute(SampleAttribute.Type.OTHER);
    }

    @UiHandler("okButton")
    void okButtonClick(ClickEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    void cancelButtonClick(ClickEvent event) {
        hide();
    }

    private void addAttribute(SampleAttribute.Type type) {
        SampleAttribute attribute = type.createAttribute();
        if (attributes.contains(attribute)) {
            return;
        }
        attributes.add(attribute);
        currentAttributes.addItem(attribute.getName());
        currentAttributes.setItemSelected(attributes.size() - 1, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), currentAttributes);
    }

    private void removeAttribute(int index) {
        currentAttributes.removeItem(index);
        SampleAttribute attr = attributes.remove(index);
        if (!attr.isCustom()) {
            updatePredefinedList();
        }
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), currentAttributes);
    }

    private void updatePredefinedList() {
        predefinedAttributes.clear();
        Set<SampleAttribute.Type> used = new HashSet<SampleAttribute.Type>();
        for (SampleAttribute attr : attributes) {
            used.add(attr.getType());
        }
        for (SampleAttribute.Type type : sampleAttributeTypes()) {
            if (!used.contains(type)) {
                predefinedAttributes.addItem(type.getTitle(), type.name());
            }
        }
    }
}
