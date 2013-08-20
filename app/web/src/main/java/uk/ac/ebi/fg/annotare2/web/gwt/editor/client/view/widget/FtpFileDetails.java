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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DeleteEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DeleteEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.HasDeleteEventHandlers;

/**
 * @author Olga Melnichuk
 */
public class FtpFileDetails extends Composite implements HasDeleteEventHandlers {

    interface Binder extends UiBinder<Widget, FtpFileDetails> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    TextBox fileNameBox;

    @UiField
    TextBox md5Box;

    @UiField
    Anchor cancelIcon;

    @UiField
    InlineLabel md5Error;

    @UiField
    InlineLabel fileNameError;

    @UiField
    InlineLabel error;

    private final NotEmptyTextBox notEmptyFileName;
    private final NotEmptyTextBox notEmptyMd5;

    public FtpFileDetails() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        notEmptyFileName = new NotEmptyTextBox(fileNameBox, fileNameError);
        notEmptyMd5 = new NotEmptyTextBox(md5Box, md5Error);
    }

    public String getFileName() {
        return fileNameBox.getValue().trim();
    }

    public String getMd5() {
        return md5Box.getValue().trim();
    }

    public boolean isValid() {
        return notEmptyFileName.isValid() && notEmptyMd5.isValid();
    }

    public void setError(String errorMessage) {
        error.setText(errorMessage);
    }

    public void setEnabled(boolean enabled) {
        fileNameBox.setEnabled(enabled);
        md5Box.setEnabled(enabled);
        cancelIcon.setEnabled(enabled);
    }

    @Override
    public HandlerRegistration addDeleteEventHandler(DeleteEventHandler handler) {
        return addHandler(handler, DeleteEvent.getType());
    }

    @UiHandler("cancelIcon")
    void onCancelClick(ClickEvent event) {
        DeleteEvent.fire(this);
    }

    private static class NotEmptyTextBox {
        private final Label label;
        private final TextBox textBox;

        private NotEmptyTextBox(TextBox textBox, Label label) {
            this.label = label;
            this.textBox = textBox;
            textBox.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    validate();
                }
            });
        }

        boolean isValid() {
            return validate();
        }

        private boolean validate() {
            boolean isEmpty = isNullOrEmpty(textBox.getValue());
            label.setText(isEmpty ? "Please, specify a value" : "");
            return !isEmpty;
        }

        private boolean isNullOrEmpty(String value) {
            return value == null || value.trim().isEmpty();
        }
    }
}
