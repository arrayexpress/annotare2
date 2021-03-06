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

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.gwt.dom.client.BrowserEvents.*;

/**
 * @author Olga Melnichuk
 */
public class EditSelectionCell extends AbstractEditableCell<String, EditSelectionCell.ViewData> implements AsyncOptionProvider.OptionDisplay {

    interface Template extends SafeHtmlTemplates {

        @Template("<select tabindex=\"{0}\">")
        SafeHtml selectStart(String index);

        @Template("</select>")
        SafeHtml selectEnd();

        @Template("<optgroup label=\"{0}\">")
        SafeHtml groupStart(String label);

        @Template("</optgroup>")
        SafeHtml groupEnd();

        @Template("<option value=\"{1}\">{0}</option>")
        SafeHtml option(String option, String index);

        @Template("<option value=\"{1}\" selected=\"selected\">{0}</option>")
        SafeHtml selectedOption(String option, String index);
    }

    static class ViewData {

        private boolean isEditing;

        private String original;

        private String text;

        public ViewData(String text) {
            this.original = text;
            this.text = text;
            this.isEditing = true;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            ViewData vd = (ViewData) o;
            return equalsOrBothNull(original, vd.original)
                    && equalsOrBothNull(text, vd.text) && isEditing == vd.isEditing;
        }

        public String getOriginal() {
            return original;
        }

        public String getText() {
            return text;
        }

        @Override
        public int hashCode() {
            return original.hashCode() + text.hashCode()
                    + Boolean.valueOf(isEditing).hashCode() * 29;
        }

        public boolean isEditing() {
            return isEditing;
        }

        public void setEditing(boolean isEditing) {
            boolean wasEditing = this.isEditing;
            this.isEditing = isEditing;

            // This is a subsequent edit, so start from where we left off.
            if (!wasEditing && isEditing) {
                original = text;
            }
        }

        public void setText(String text) {
            this.text = text;
        }

        private boolean equalsOrBothNull(Object o1, Object o2) {
            return (o1 == null) ? o2 == null : o1.equals(o2);
        }
    }

    private static Template template;

    private final SafeHtmlRenderer<String> renderer;

    private HashMap<String, Integer> indexForOption = new HashMap<>();
    private List<String> options = new ArrayList<>();

    public EditSelectionCell(AsyncOptionProvider optionProvider) {
        this(SimpleSafeHtmlRenderer.getInstance(), optionProvider);
    }

    public EditSelectionCell(SafeHtmlRenderer<String> renderer, AsyncOptionProvider optionProvider) {
        super(CLICK, KEYUP, KEYDOWN,  BLUR, CHANGE);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        if (renderer == null) {
            throw new IllegalArgumentException("renderer == null");
        }
        this.renderer = renderer;
        optionProvider.addOptionDisplay(this);
        optionProvider.update();
    }

    @Override
    public void updateOptions(List<String> newOptions) {
        indexForOption.clear();
        options.clear();

        for (int index = 0; index < newOptions.size(); index++) {
            String option = newOptions.get(index);
            if (!option.startsWith("---")) {
                indexForOption.put(option, index);
            }
        }

        options.addAll(newOptions);
    }

    @Override
    public boolean isEditing(Context context, Element parent, String value) {
        ViewData viewData = getViewData(context.getKey());
        return viewData != null && viewData.isEditing();
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, String value,
                               NativeEvent event, ValueUpdater<String> valueUpdater) {
        Object key = context.getKey();
        ViewData viewData = getViewData(key);
        if (viewData != null && viewData.isEditing()) {
            // Handle the edit event.
            editEvent(context, parent, value, viewData, event, valueUpdater);
        } else {
            String type = event.getType();
            int keyCode = event.getKeyCode();
            boolean enterPressed = KEYUP.equals(type)
                    && keyCode == KeyCodes.KEY_ENTER;
            if (CLICK.equals(type) || enterPressed) {
                // Go into edit mode.
                if (viewData == null) {
                    viewData = new ViewData(value);
                    setViewData(key, viewData);
                } else {
                    viewData.setEditing(true);
                }
                edit(context, parent, value);
            }
        }
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        // Get the view data.
        Object key = context.getKey();
        ViewData viewData = getViewData(key);
        if (viewData != null && !viewData.isEditing() && value != null
                && value.equals(viewData.getText())) {
            clearViewData(key);
            viewData = null;
        }

        String toRender = value;
        if (viewData != null) {
            String text = viewData.getText();
            if (viewData.isEditing()) {
                int selectedIndex = getSelectedIndex(viewData.getText());
                sb.append(template.selectStart("-1"));

                boolean hasGroupOpened = false;
                for (int index = 0; index < options.size(); index++) {
                    String option = options.get(index);
                    if (!option.startsWith("---")) {
                        if (index == selectedIndex) {
                            sb.append(template.selectedOption(option, String.valueOf(index)));
                        } else {
                            sb.append(template.option(option, String.valueOf(index)));
                        }
                    } else {
                        if (hasGroupOpened) {
                            sb.append(template.groupEnd());
                            hasGroupOpened = false;
                        }
                        sb.append(template.groupStart(option.replaceFirst("^---\\s*", "")));
                        hasGroupOpened = true;
                    }
                }
                if (hasGroupOpened) {
                    sb.append(template.groupEnd());
                }
                sb.append(template.selectEnd());
                return;
            } else {
                // The user pressed enter, but view data still exists.
                toRender = text;
            }
        }

        if (toRender != null && toRender.trim().length() > 0) {
            sb.append(renderer.render(toRender));
        } else {
      /*
       * Render a blank space to force the rendered element to have a height.
       * Otherwise it is not clickable.
       */
            sb.appendHtmlConstant("\u00A0");
        }
    }

    private void editEvent(Context context, Element parent, String value,
                           ViewData viewData, NativeEvent event, ValueUpdater<String> valueUpdater) {
        String type = event.getType();
        boolean keyUp = KEYUP.equals(type);
        boolean keyDown = KEYDOWN.equals(type);
        if (keyUp || keyDown) {
            int keyCode = event.getKeyCode();
            if (keyUp && keyCode == KeyCodes.KEY_ENTER) {
                commit(context, parent, viewData, valueUpdater);
            } else if (keyUp && keyCode == KeyCodes.KEY_ESCAPE) {
                String originalText = viewData.getOriginal();
                viewData.setText(originalText);
                viewData.setEditing(false);

                cancel(context, parent, value);
            }
        } else if (BLUR.equals(type)) {
            // Commit the change. Ensure that we are blurring the input element and
            // not the parent element itself.
            EventTarget eventTarget = event.getEventTarget();
            if (Element.is(eventTarget)) {
                Element target = Element.as(eventTarget);
                if ("select".equals(target.getTagName().toLowerCase())) {
                    commit(context, parent, viewData, valueUpdater);
                }
            }
        } else if (CHANGE.equals(type)) {
            updateViewData(parent, viewData, true);
        }
    }

    private String updateViewData(Element parent, ViewData viewData, boolean isEditing) {
        SelectElement select = parent.getFirstChild().cast();
        String newValue = options.get(Integer.decode(select.getValue()));
        viewData.setText(newValue);
        viewData.setEditing(isEditing);
        return newValue;
    }

    private void commit(Context context, Element parent, ViewData viewData,
                        ValueUpdater<String> valueUpdater) {
        String value = updateViewData(parent, viewData, false);
        setValue(context, parent, viewData.getOriginal());
        if (valueUpdater != null) {
            valueUpdater.update(value);
        }
    }

    private void cancel(Context context, Element parent, String value) {
        setValue(context, parent, value);
    }

    private int getSelectedIndex(String value) {
        Integer index = indexForOption.get(value);
        if (index == null) {
            return -1;
        }
        return index;
    }

    @Override
    public boolean resetFocus(Context context, Element parent, String value) {
        if (isEditing(context, parent, value)) {
            getSelectElement(parent).focus();
            return true;
        }
        return false;
    }

    protected void edit(Context context, Element parent, String value) {
        setValue(context, parent, value);
        SelectElement input = getSelectElement(parent);
        input.focus();
    }

    private SelectElement getSelectElement(Element parent) {
        return parent.getFirstChild().cast();
    }

}
