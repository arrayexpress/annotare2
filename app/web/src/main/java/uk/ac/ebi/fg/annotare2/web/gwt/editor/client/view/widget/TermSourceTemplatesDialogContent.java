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
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.HandlerRegistration;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITermSource;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CloseEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CloseEventHandler;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class TermSourceTemplatesDialogContent extends Composite {

    interface Binder extends UiBinder<Widget, TermSourceTemplatesDialogContent> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    protected Button okButton;

    @UiField
    protected Button cancelButton;

    @UiField
    protected Button selectNone;

    @UiField(provided = true)
    protected ListBox listBox;

    @UiField
    protected Label label;

    private boolean cancelled = false;

    public TermSourceTemplatesDialogContent(final ArrayList<UITermSource> templates) {
        listBox = new ListBox(true);
        listBox.setVisibleItemCount(10);
        for (UITermSource ts : templates) {
            listBox.addItem(ts.getName());
        }
        initWidget(Binder.BINDER.createAndBindUi(this));

        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int i = listBox.getSelectedIndex();
                if (i >= 0) {
                    label.setText(templates.get(i).getDescription());
                }
            }
        });

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (listBox.getSelectedIndex() > 0) {
                    fireCloseEvent();
                }
            }
        });

        selectNone.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireCloseEvent();
            }
        });


        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancelled = true;
                fireCloseEvent();
            }
        });
    }

    private void fireCloseEvent() {
        fireEvent(new CloseEvent());
    }

    public HandlerRegistration addCloseHandler(CloseEventHandler closeEventHandler) {
        return addHandler(closeEventHandler, CloseEvent.TYPE);
    }
}
