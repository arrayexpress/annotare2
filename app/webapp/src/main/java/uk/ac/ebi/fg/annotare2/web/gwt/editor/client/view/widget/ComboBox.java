/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;

import java.util.List;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources.EditorResources.EDITOR_RESOURCES;

/**
 * @author Olga Melnichuk
 */
public class ComboBox extends Composite implements HasText, Focusable, HasEnabled,
        HasAllKeyHandlers, HasValue<String>, HasSelectionHandlers<String> {

    private final TextBox textBox;

    private final DropDownDisplay display;

    public ComboBox() {
        textBox = new TextBox();
        textBox.addStyleName("wgt-ComboBoxIcon");
        textBox.setWidth("100%");
        addEventsToTextBox(textBox);

        Image icon = new Image(EDITOR_RESOURCES.dropDownIcon());
        icon.addStyleName("wgt-ComboBoxButton");
        icon.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleDropDown();
            }
        });

        display = new DropDownDisplay();
        display.addPopupAutoHidePartner(textBox.getElement());
        display.addPopupAutoHidePartner(icon.getElement());

        FlowPanel layout = new FlowPanel();
        layout.add(textBox);
        layout.add(icon);
        initWidget(layout);
    }

    public void setOptions(List<String> options) {
        display.setOptions(options, new OptionSelectionCallback() {
            @Override
            public void onOptionSelected(String option) {
                setNewSelection(option);
            }
        });
    }

    @Override
    public int getTabIndex() {
        return textBox.getTabIndex();
    }

    @Override
    public String getText() {
        return textBox.getText();
    }

    @Override
    public String getValue() {
        return textBox.getValue();
    }

    @Override
    public boolean isEnabled() {
        return textBox.isEnabled();
    }

    @Override
    public void setAccessKey(char key) {
        textBox.setAccessKey(key);
    }

    @Override
    public void setEnabled(boolean enabled) {
        textBox.setEnabled(enabled);
        if (!enabled) {
            display.hideDropDown();
        }
    }

    @Override
    public void setTabIndex(int index) {
        textBox.setTabIndex(index);
    }

    @Override
    public void setText(String text) {
        textBox.setText(text);
    }

    @Override
    public void setValue(String newValue) {
        textBox.setValue(newValue);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        textBox.setValue(value, fireEvents);
    }

    @Override
    public void setFocus(boolean focused) {
        textBox.setFocus(focused);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return addDomHandler(handler, KeyPressEvent.getType());
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(
            ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }

    private void fireSelectionEvent(String selected) {
        SelectionEvent.fire(this, selected);
    }

    private void addEventsToTextBox(final TextBox textBox) {
        class TextBoxEvents extends HandlesAllKeyEvents implements ValueChangeHandler<String> {

            public void onKeyDown(KeyDownEvent event) {
                switch (event.getNativeKeyCode()) {
                    case KeyCodes.KEY_DOWN:
                        display.moveSelectionDown(textBox);
                        break;
                    case KeyCodes.KEY_UP:
                        display.moveSelectionUp(textBox);
                        break;
                    case KeyCodes.KEY_ENTER:
                    case KeyCodes.KEY_TAB:
                        String option = display.getCurrentSelection();
                        if (option == null) {
                            display.hideDropDown();
                        } else {
                            setNewSelection(option);
                        }
                        break;
                    case KeyCodes.KEY_ESCAPE:
                        display.hideDropDown();
                        break;
                }
                delegateEvent(ComboBox.this, event);
            }

            public void onKeyPress(KeyPressEvent event) {
                delegateEvent(ComboBox.this, event);
            }

            public void onKeyUp(KeyUpEvent event) {
                delegateEvent(ComboBox.this, event);
            }

            public void onValueChange(ValueChangeEvent<String> event) {
                delegateEvent(ComboBox.this, event);
            }
        }

        TextBoxEvents events = new TextBoxEvents();
        events.addKeyHandlersTo(textBox);
        textBox.addValueChangeHandler(events);
    }

    private void setNewSelection(String option) {
        textBox.setValue(option, true);
        display.hideDropDown();
        fireSelectionEvent(option);
    }

    private void toggleDropDown() {
        if (display.isShowing()) {
            display.hideDropDown();
        } else {
            display.showDropDown(textBox);
            textBox.setFocus(true);
        }
    }

    private static class DropDownDisplay {

        private static final int SCROLLABLE_HEIGHT = 150;
        private final DropDownMenu menuBar;
        private final PopupPanel popupPanel;
        private final ScrollPanel scrollPanel;

        private DropDownDisplay() {
            menuBar = createMenu();
            popupPanel = createPopup();
            scrollPanel = new ScrollPanel(menuBar);
            popupPanel.setWidget(scrollPanel);
            menuBar.setStyleName("gwt-MenuBar");
        }

        private DropDownMenu createMenu() {
            DropDownMenu mb = new DropDownMenu();
            mb.setStyleName("");
            mb.setFocusOnHoverEnabled(false);
            return mb;
        }

        private PopupPanel createPopup() {
            PopupPanel p = new DecoratedPopupPanel(true, false);
            p.setPreviewingAllNativeEvents(true);
            return p;
        }

        protected void setOptions(List<String> options, final OptionSelectionCallback callback) {
            menuBar.clearItems();

            for (final String option : options) {
                final DropDownItem menuItem = new DropDownItem(option, false,
                        new Scheduler.ScheduledCommand() {
                            public void execute() {
                                callback.onOptionSelected(option);
                            }
                        });
                menuBar.addItem(menuItem);
            }

            if (options.size() > 6) {
                popupPanel.getWidget().setHeight(SCROLLABLE_HEIGHT + "px");
            }
        }

        protected void showDropDown(TextBox textBox) {
            if (popupPanel.isAttached()) {
                popupPanel.hide();
            }
            popupPanel.showRelativeTo(textBox);
        }

        protected void hideDropDown() {
            popupPanel.hide();
        }

        /**
         * Add popup auto-hide partner element (auto-hide behavior will be disabled on this element).
         *
         * @param element DOM element
         */
        protected void addPopupAutoHidePartner(Element element) {
            popupPanel.addAutoHidePartner(element);
        }

        protected void moveSelectionDown(TextBox textBox) {
            if (isShowing()) {
                menuBar.moveSelectionDown();
                scrollToSelection();
            } else {
                showDropDown(textBox);
            }
        }

        protected void moveSelectionUp(TextBox textBox) {
            if (isShowing()) {
                menuBar.moveSelectionUp();
            } else {
                showDropDown(textBox);
            }
        }

        protected String getCurrentSelection() {
            if (!isShowing()) {
                return null;
            }
            MenuItem item = menuBar.getSelectedItem();
            return item == null ? null : ((DropDownItem) item).getValue();
        }

        protected boolean isShowing() {
            return popupPanel.isShowing();
        }

        private void scrollToSelection() {
            Element selected = menuBar.getSelectedItem().getElement();
            int top = selected.getOffsetTop();
            int height = selected.getOffsetHeight();
            int pos = scrollPanel.getVerticalScrollPosition();
            if (top < pos || top + height > pos + SCROLLABLE_HEIGHT) {
                scrollPanel.setVerticalScrollPosition(top);
            }
        }
    }

    private static class DropDownMenu extends MenuBar {
        private DropDownMenu() {
            super(true);
        }

        @Override
        protected MenuItem getSelectedItem() {
            return super.getSelectedItem();
        }
    }

    private static class DropDownItem extends MenuItem {
        private String option;

        public DropDownItem(String option, boolean asHTML, Scheduler.ScheduledCommand command) {
            super(option, asHTML, command);
            DOM.setStyleAttribute(getElement(), "whiteSpace", "nowrap");
            this.option = option;
        }

        public String getValue() {
            return option;
        }
    }

    public static interface OptionSelectionCallback {
        void onOptionSelected(String option);
    }
}

