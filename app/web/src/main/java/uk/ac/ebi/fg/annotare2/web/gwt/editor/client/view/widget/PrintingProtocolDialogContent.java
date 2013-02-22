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
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.UIPrintingProtocol;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.HasDialogCloseHandlers;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class PrintingProtocolDialogContent extends Composite implements HasDialogCloseHandlers<UIPrintingProtocol> {

    interface Binder extends UiBinder<Widget, PrintingProtocolDialogContent> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button cancelButton;

    @UiField
    Button selectNone;

    @UiField
    Button okButton;

    @UiField
    ListBox listBox;

    @UiField
    Label name;

    @UiField
    HTML descirption;

    private UIPrintingProtocol selection;

    public PrintingProtocolDialogContent(final List<UIPrintingProtocol> protocols, String selected) {
        initWidget(Binder.BINDER.createAndBindUi(this));

        for (UIPrintingProtocol p : protocols) {
            listBox.addItem(p.getName());
        }
        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int idx = listBox.getSelectedIndex();
                if (idx >= 0) {
                    UIPrintingProtocol protocol = protocols.get(idx);
                    name.setText(protocol.getName());
                    descirption.setHTML(protocol.getDescription());
                    selection = protocol;
                }
            }
        });
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireDialogCloseEvent(selection, true);
            }
        });
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireDialogCloseEvent(null, false);
            }
        });
        selectNone.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireDialogCloseEvent(null, true);
            }
        });
        selectItem(listBox, selected);
    }

    private void selectItem(ListBox listBox, String target) {
        int idx = 0;
        if (target != null) {
            for (int i = 0; i < listBox.getItemCount(); i++) {
                if (target.equals(listBox.getValue(i))) {
                    idx = i;
                }
            }
        }
        listBox.setItemSelected(idx, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), listBox);
    }

    private void fireDialogCloseEvent(UIPrintingProtocol selection, boolean isOk) {
        DialogCloseEvent.fire(this, selection, isOk);
    }

    @Override
    public HandlerRegistration addDialogCloseHandler(DialogCloseHandler<UIPrintingProtocol> handler) {
        return addHandler(handler, DialogCloseEvent.getType());
    }
}
