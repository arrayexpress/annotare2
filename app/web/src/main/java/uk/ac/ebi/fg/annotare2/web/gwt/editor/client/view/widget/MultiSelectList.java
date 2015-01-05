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

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class MultiSelectList extends Composite implements HasChangeHandlers, HasValue<List<String>> {

    private final HTML label;

    private List<String> value;
    private final String dialogTitle;

    public MultiSelectList(String title, final AsyncListItemProvider listItemProvider) {
        this.dialogTitle = title;

        label = new HTML();
        Anchor anchor = new Anchor("change...");
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (listItemProvider == null) {
                    return;
                }
                listItemProvider.getListItems(new AsyncCallback<List<String>>() {
                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(List<String> result) {
                        Set<String> selected = new HashSet<String>(getValue());
                        List<String> allItems = new ArrayList<String>(result);
                        Collections.sort(allItems);
                        Map<String, Boolean> map = new LinkedHashMap<String, Boolean>();
                        for(String item : allItems) {
                            map.put(item, selected.contains(item));
                        }
                        showDialog(map);
                    }
                });
            }
        });

        FlowPanel panel = new FlowPanel();
        panel.add(label);
        panel.add(anchor);

        initWidget(panel);
    }

    private void showDialog(Map<String, Boolean> listItems) {
        new MultiSelectListDialog(dialogTitle, listItems, new DialogCallback<List<String>>() {
            @Override
            public void onOkay(List<String> strings) {
                setValue(strings, true);
            }
        });
    }

    @Override
    public HandlerRegistration addChangeHandler(ChangeHandler handler) {
        return addHandler(handler, ChangeEvent.getType());
    }

    @Override
    public List<String> getValue() {
        return new ArrayList<String>(value);
    }

    @Override
    public void setValue(List<String> value) {
        setValue(value, false);
    }

    @Override
    public void setValue(List<String> value, boolean fireEvents) {
        List<String> oldValue = this.value;
        this.value = new ArrayList<String>(value);
        updateLabel();
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<String>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void updateLabel() {
        label.setHTML(join(value));
    }

    private static String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append("<br/>");
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public interface AsyncListItemProvider {
        void getListItems(AsyncCallback<List<String>> callback);
    }
}
