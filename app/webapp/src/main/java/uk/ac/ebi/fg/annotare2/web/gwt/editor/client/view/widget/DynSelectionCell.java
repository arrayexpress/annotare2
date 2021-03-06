/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynSelectionCell<C> extends AbstractInputCell<C, C> {

    interface Template extends SafeHtmlTemplates {
        @Template("<option value=\"{0}\">{0}</option>")
        SafeHtml deselected(String option);

        @Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
        SafeHtml selected(String option);
    }

    public interface Option<C> {
        public C getValue();

        public String getText();
    }

    public interface ListProvider<C> {

        public List<Option<C>> getOptions();

        public Option<C> getDefault();

    }

    protected static Template template;

    protected final List<Option<C>> options;
    protected final Map<C, Integer> indexForOption;

    protected final ListProvider<C> optionsProvider;

    protected static List<String> deletedFileNames;

    public DynSelectionCell(ListProvider<C> optionsProvider) {
        super(BrowserEvents.CHANGE);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        this.options = new ArrayList<Option<C>>();
        this.indexForOption = new HashMap<C, Integer>();
        this.deletedFileNames = new ArrayList<>();
        this.optionsProvider = optionsProvider;
        updateOptions();
    }

    public DynSelectionCell(ListProvider<C> optionsProvider, List<String> deletedFileNames) {
        super(BrowserEvents.CHANGE);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        this.options = new ArrayList<Option<C>>();
        this.deletedFileNames = new ArrayList<>();
        this.indexForOption = new HashMap<C, Integer>();
        this.optionsProvider = optionsProvider;

        this.deletedFileNames = deletedFileNames;
        updateOptions();
    }

    public DynSelectionCell(final List<C> options) {
        super(BrowserEvents.CHANGE);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        this.options = new ArrayList<Option<C>>();
        this.indexForOption = new HashMap<C, Integer>();
        this.deletedFileNames = new ArrayList<>();
        this.optionsProvider = new ListProvider<C>() {
            List<Option<C>> list;
            @Override
            public List<Option<C>> getOptions() {
                list = new ArrayList<>();
                for (final C item: options ) {
                    list.add(new Option<C>() {
                        @Override
                        public C getValue() {
                            return item;
                        }

                        @Override
                        public String getText() {
                            return item.toString();
                        }
                    });
                }
                return list;
            }

            @Override
            public Option<C> getDefault() {
                return null;
            }
        };
        updateOptions();
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, C value,
                               NativeEvent event, ValueUpdater<C> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        String type = event.getType();

        if (BrowserEvents.CHANGE.equals(type)) {
            Object key = context.getKey();
            SelectElement select = parent.getFirstChild().cast();
            C newValue = options.get(select.getSelectedIndex()).getValue();
            if(deletedFileNames.contains(newValue)) {
                deletedFileNames.remove(newValue);
            }
            setViewData(key, newValue);
            finishEditing(parent, newValue, key, valueUpdater);
            if (valueUpdater != null) {
                valueUpdater.update(newValue);
            }
        }
    }

    @Override
    public void render(Context context, C value, SafeHtmlBuilder sb) {
        // Get the view data.
        Object key = context.getKey();
        C viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        int selectedIndex = getSelectedIndex(viewData == null ? value : viewData);

        sb.appendHtmlConstant("<select tabindex=\"-1\">");
        int index = 0;
        for (Option<C> option : options) {
            if (index++ == selectedIndex) {
                sb.append(template.selected(option.getText()));
            } else {
                sb.append(template.deselected(option.getText()));
            }
        }
        sb.appendHtmlConstant("</select>");
    }

    protected int getSelectedIndex(C value) {
        updateOptions();

        if(!deletedFileNames.contains(value)) {
            Integer index = indexForOption.get(value);
            if (null == index) {
                return indexForOption.get(optionsProvider.getDefault().getValue());
            } else {
                return index;
            }
        }
        else {
            return 0;
        }
    }

    protected void updateOptions() {
        options.clear();
        indexForOption.clear();
        addOption(optionsProvider.getDefault());
        List<Option<C>> opts = optionsProvider.getOptions();
        for (Option<C> option : opts) {
            addOption(option);
        }
    }

    private void addOption(Option<C> option) {
        indexForOption.put(option.getValue(), options.size());
        options.add(option);
    }
}
