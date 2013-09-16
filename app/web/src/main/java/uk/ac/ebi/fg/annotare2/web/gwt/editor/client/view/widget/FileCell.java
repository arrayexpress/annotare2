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

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

/**
 * @author Olga Melnichuk
 */
public class FileCell extends AbstractEditableCell<Long, Long> {

    private static final int ESCAPE = 27;

    private final PopupPanel panel;
    private final ListBox listBox;
    private final SafeHtmlRenderer<String> renderer;
    private final ListProvider<Long> listProvider;
    private final Map<Long, Integer> valueToIndex;
    private final List<Long> values;

    private Object lastKey;
    private Long lastValue;
    private int lastIndex;
    private int lastColumn;
    private Element lastParent;
    private ValueUpdater<Long> valueUpdater;

    public FileCell(ListProvider<Long> listProvider) {
        this(SimpleSafeHtmlRenderer.getInstance(), listProvider);
    }

    public FileCell(SafeHtmlRenderer<String> renderer, ListProvider<Long> listProvider) {
        super(CLICK, KEYDOWN);

        this.renderer = renderer;
        this.listProvider = listProvider;
        valueToIndex = new HashMap<Long, Integer>();
        values = new ArrayList<Long>();

        listBox = new ListBox();
        listBox.setVisibleItemCount(8);
        listBox.setWidth("100%");

        ScrollPanel listPanel = new ScrollPanel();
        listPanel.setHeight("150px");
        listPanel.setWidth("200px");
        listPanel.add(listBox);

        panel = new PopupPanel(true, true) {
            @Override
            protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                if (Event.ONKEYUP == event.getTypeInt()) {
                    if (event.getNativeEvent().getKeyCode() == ESCAPE) {
                        panel.hide();
                    }
                }
            }
        };

        panel.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {
                lastKey = null;
                lastValue = null;
                lastIndex = -1;
                lastColumn = -1;
                if (lastParent != null && !event.isAutoClosed()) {
                    // Refocus on the containing cell after the user selects a value, but
                    // not if the popup is auto closed.
                    lastParent.focus();
                }
                lastParent = null;
            }
        });
        panel.add(listPanel);

        listBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Element cellParent = lastParent;
                Long oldValue = lastValue;
                Object key = lastKey;
                int index = lastIndex;
                int column = lastColumn;

                Long value = getValue(listBox.getSelectedIndex());
                setViewData(key, value);
                setValue(new Context(index, column, key), cellParent, oldValue);
                if (valueUpdater != null) {
                    valueUpdater.update(value);
                }
                panel.hide();
            }
        });
    }

    @Override
    public boolean isEditing(Context context, Element parent, Long value) {
        return lastKey != null && lastKey.equals(context.getKey());
    }

    @Override
    public void render(Context context, Long value, SafeHtmlBuilder sb) {
        Object key = context.getKey();
        Long viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        String toRender = null;
        if (viewData != null) {
            toRender = getText(viewData);
        } else if (value != null) {
            toRender = getText(value);
        }

        if (toRender != null && toRender.trim().length() > 0) {
            sb.append(renderer.render(toRender));
        } else {
            // Render a blank space to force the rendered element to have a height.
            // Otherwise it is not clickable.
            sb.appendHtmlConstant("\u00A0");
        }
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, Long value,
                               NativeEvent event, ValueUpdater<Long> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        if (CLICK.equals(event.getType())) {
            onEnterKeyDown(context, parent, value, event, valueUpdater);
        }
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, Long value,
                                  NativeEvent event, ValueUpdater<Long> valueUpdater) {
        this.lastKey = context.getKey();
        this.lastParent = parent;
        this.lastValue = value;
        this.lastIndex = context.getIndex();
        this.lastColumn = context.getColumn();
        this.valueUpdater = valueUpdater;

        populateList();

        Long viewData = getViewData(lastKey);
        if (viewData != null) {
            listBox.setItemSelected(valueToIndex.get(viewData), true);
        }

        panel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                panel.setPopupPosition(lastParent.getAbsoluteLeft(),
                        lastParent.getAbsoluteBottom());
            }
        });
    }

    private String getText(Long value) {
        for (Option<Long> option : listProvider.getOptions()) {
            if (value.equals(option.getValue())) {
                return option.getText();
            }
        }
        return null;
    }

    private Long getValue(int index) {
        return index >= 0 ? values.get(index) : null;
    }

    private void populateList() {
        listBox.clear();
        valueToIndex.clear();
        values.clear();

        for (Option<Long> option : listProvider.getOptions()) {
            listBox.addItem(option.getText(), option.getStringValue());
            valueToIndex.put(option.getValue(), values.size());
            values.add(option.getValue());
        }
    }

    public static class Option<V> {

        private final V value;

        private final String text;

        public Option(V value, String text) {
            this.text = text;
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        public String getStringValue() {
            return value.toString();
        }

        public String getText() {
            return text;
        }
    }

    public static interface ListProvider<V> {

        List<Option<V>> getOptions();
    }
}
