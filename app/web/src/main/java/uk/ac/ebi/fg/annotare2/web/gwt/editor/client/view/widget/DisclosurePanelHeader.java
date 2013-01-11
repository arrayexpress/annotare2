/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemSelectionEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ItemSelectionEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources.EditorResources;

/**
 * @author Olga Melnichuk
 */
public class DisclosurePanelHeader extends Composite {

    interface Binder extends UiBinder<Widget, DisclosurePanelHeader> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Label label;

    @UiField
    TableCellElement expandCollapseIcon;

    @UiField
    CheckBox checkbox;

    private final String collapseClassName;

    private final String expandClassName;

    public DisclosurePanelHeader() {
        EditorResources.INSTANCE.editorStyles().ensureInjected();
        collapseClassName = EditorResources.INSTANCE.editorStyles().collapseIconClass();
        expandClassName = EditorResources.INSTANCE.editorStyles().expandIconClass();
        initWidget(Binder.BINDER.createAndBindUi(this));

        checkbox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.stopPropagation();
            }
        });

        checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                fireEvent(new ItemSelectionEvent(checkbox.isEnabled()));
            }
        });
    }

    @UiFactory
    public EditorResources getResources() {
        return EditorResources.INSTANCE;
    }

    public void setHeaderText(String title) {
        label.setText(title.isEmpty() ? "" : title);
    }

    public void setExpanded() {
        expandCollapseIcon.removeClassName(expandClassName);
        expandCollapseIcon.addClassName(collapseClassName);
    }

    public void setCollapsed() {
        expandCollapseIcon.removeClassName(collapseClassName);
        expandCollapseIcon.addClassName(expandClassName);
    }

    public void addItemSelectionHandler(ItemSelectionEventHandler handler) {
        addHandler(handler, ItemSelectionEvent.TYPE);
    }

    public boolean isSelected() {
        return checkbox.getValue();
    }
}
