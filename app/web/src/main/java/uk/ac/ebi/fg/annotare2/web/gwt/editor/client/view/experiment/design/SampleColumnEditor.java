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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.configmodel.enums.AttributeType;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.columns.SampleColumn;

/**
 * @author Olga Melnichuk
 */
public class SampleColumnEditor extends Composite {

    interface Binder extends UiBinder<Widget, SampleColumnEditor> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox nameBox;

    @UiField
    CheckBox factorValueCheckbox;

    @UiField
    ColumnValueTypeEditor valueTypeEditor;

    public SampleColumnEditor(SampleColumn column) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        nameBox.setValue(column.getName());
        nameBox.setEnabled(!column.isDefault());

        valueTypeEditor.setValue(column.getValueType());
        valueTypeEditor.setEnabled(!column.isDefault());

        AttributeType type = column.getType();
        factorValueCheckbox.setValue(type.isFactorValue());
        factorValueCheckbox.setVisible(type.isFactorValue() || type.isCharacteristic());
    }
}
