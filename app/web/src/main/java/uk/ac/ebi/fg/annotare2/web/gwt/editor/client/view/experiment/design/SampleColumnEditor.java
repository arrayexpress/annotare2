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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.ColumnValueType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ContentChangeEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.HasContentChangeEventHandlers;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnEditor extends Composite implements HasContentChangeEventHandlers {

    interface Binder extends UiBinder<Widget, SampleColumnEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox nameBox;

    @UiField
    CheckBox factorValueCheckbox;

    @UiField
    ColumnValueTypeEditor valueTypeEditor;

    private SampleColumn column;

    public SampleColumnEditor(SampleColumn column) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        nameBox.setValue(column.getName());
        nameBox.setEnabled(!column.isDefault());

        valueTypeEditor.setValue(column.getValueType());
        valueTypeEditor.setEnabled(!column.isDefault());

        AttributeType type = column.getType();
        factorValueCheckbox.setValue(type.isFactorValue());
        factorValueCheckbox.setVisible(type.isFactorValue() || type.isCharacteristic());

        this.column = column;
    }

    @UiHandler("nameBox")
    void nameValueChanged(ChangeEvent event) {
        if (column.isDefault()) {
            return;
        }
        column.setName(nameBox.getValue());
        ContentChangeEvent.fire(this);
    }

    @UiHandler("factorValueCheckbox")
    void factorValueCheckboxChanged(ValueChangeEvent<Boolean> event) {
        AttributeType type = column.getType();
        if (type.isFactorValue() || type.isCharacteristic()) {
            column.setType(event.getValue() ? AttributeType.FACTOR_VALUE : AttributeType.CHARACTERISTIC);
            ContentChangeEvent.fire(this);
        }
    }

    @UiHandler("valueTypeEditor")
    void valueTypeChanged(ValueChangeEvent<ColumnValueType> event) {
        if (column.isDefault()) {
            return;
        }
        column.setValueType(event.getValue());
        ContentChangeEvent.fire(this);
    }

    @Override
    public HandlerRegistration addContentChangeEventHandler(ContentChangeEventHandler handler) {
        return addHandler(handler, ContentChangeEvent.getType());
    }
}
