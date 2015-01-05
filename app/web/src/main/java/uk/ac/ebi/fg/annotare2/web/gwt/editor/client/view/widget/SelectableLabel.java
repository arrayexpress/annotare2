/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.SelectionChangeEvent;


/**
 * @author Olga Melnichuk
 */
public class SelectableLabel<T> extends Composite implements SelectionChangeEvent.HasSelectionChangedHandlers {

    private HorizontalPanel panel;
    private CheckBox checkbox;
    //private SimplePanel infoIcon;

    private final T value;

    public SelectableLabel(String name, T value) {
        this.value = value;

        Label label = new Label(name);
        checkbox = new CheckBox();
        checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setSelected(event.getValue(), true);
            }
        });
        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                checkbox.setValue(!checkbox.getValue(), true);
            }
        });

        //infoIcon = new SimplePanel();
        //EDITOR_RESOURCES.editorStyles().ensureInjected();
        //infoIcon.addStyleName(EDITOR_RESOURCES.editorStyles().infoIconClass());

        panel = new HorizontalPanel();
        panel.addStyleName("app-SelectableLabel");
        panel.setWidth("100%");
        panel.add(checkbox);
        panel.add(label);
        //panel.add(infoIcon);
        panel.setCellWidth(checkbox, "25px");
        //panel.setCellWidth(infoIcon, "16px");
        initWidget(panel);
    }

    public T getValue() {
        return value;
    }

    public boolean isSelected() {
        return checkbox.getValue();
    }

    public void setSelected(boolean selected, boolean fireEvents) {
        if (checkbox.getValue() != selected) {
            checkbox.setValue(selected, false);
        }

        if (selected) {
            panel.addStyleName("selected");
        } else {
            panel.removeStyleName("selected");
        }

        if (fireEvents) {
            SelectionChangeEvent.fire(this);
        }
    }

    @Override
    public HandlerRegistration addSelectionChangeHandler(SelectionChangeEvent.Handler handler) {
        return addHandler(handler, SelectionChangeEvent.getType());
    }

    //public Widget info() {
    //    return infoIcon;
    //}
}
