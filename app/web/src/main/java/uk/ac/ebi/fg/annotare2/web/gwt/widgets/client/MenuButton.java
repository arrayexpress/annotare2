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

package uk.ac.ebi.fg.annotare2.web.gwt.widgets.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class MenuButton extends Composite implements HasText, HasCloseHandlers<PopupPanel> {

    interface Binder extends UiBinder<Widget, MenuButton> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField(provided = true)
    FocusPanel panel = new FocusPanel();

    @UiField(provided = true)
    Label label = new Label();

    private ArrayList<MenuButtonItem> items = new ArrayList<MenuButtonItem>();

    private VerticalPanel vpanel = new VerticalPanel();

    private PopupPanel popup;

    private MenuButtonItem selectedItem;

    @UiConstructor
    public MenuButton() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        panel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                openPopup();
            }
        });
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void addMenuButtonItem(MenuButtonItem item) {
        items.add(items.size(), item);
        vpanel.add(item);
    }

    public MenuButtonItem addMenuButtonItem(String text) {
        MenuButtonItem item = new MenuButtonItem();
        item.setText(text);
        addMenuButtonItem(item);
        item.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                popup.hide();
            }
        });
        return item;
    }

    public void enable() {
        //TODO
    }

    public void disable() {
        //TODO
    }

    @Override
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONCLICK: {
                openPopup();
                break;
            }
        }
        super.onBrowserEvent(event);
    }

    public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> popupPanelCloseHandler) {
        return null;
    }

    private void openPopup() {
        if (popup == null) {
            popup = new DecoratedPopupPanel(true, false);
            popup.setStyleName("wgt-MenuButtonPopup");
            popup.setWidget(vpanel);
//        popup.setAnimationType(PopupPanel.AnimationType.ONE_WAY_CORNER);
//        popup.setAnimationEnabled(isAnimationEnabled);
//        popup.setStyleName(STYLENAME_DEFAULT + "Popup");
            //popup.addCloseHandler(this);
        }

        popup.setPopupPosition(panel.getAbsoluteLeft(), panel.getAbsoluteTop() + panel.getOffsetHeight());

        /*popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {

            public void setPosition(int offsetWidth, int offsetHeight) {

                    popup.setPopupPosition(
                            MenuButton.this.getAbsoluteLeft(), MenuButton.this.getAbsoluteTop() + MenuButton.this.getOffsetHeight());

            }
        });*/

        popup.show();
    }

}
