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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DialogCloseHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.HasDialogCloseHandlers;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class ExpDesignTemplatesDialogContent extends Composite
        implements HasDialogCloseHandlers<List<UITerm>> {

    interface Binder extends UiBinder<Widget, ExpDesignTemplatesDialogContent> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    protected Button okButton;

    @UiField
    protected Button cancelButton;

    @UiField
    protected Button selectNone;

    @UiField(provided = true)
    protected ListBox filterBox;

    @UiField(provided = true)
    protected ListBox valueBox;

    private Map<Integer, Set<Integer>> selected = new HashMap<Integer, Set<Integer>>();

    private final List<UITerm> templates = new ArrayList<UITerm>();

    private final List<Category> categories = new ArrayList<Category>();

    public ExpDesignTemplatesDialogContent(List<UITerm> terms) {
        templates.addAll(terms);
        categories.addAll(extractCategories(terms));

        filterBox = new ListBox();
        filterBox.setVisibleItemCount(10);
        for (Category c : categories) {
            filterBox.addItem(c.name);
        }

        valueBox = new ListBox(true);
        valueBox.setVisibleItemCount(10);

        filterBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                valueBox.clear();
                int idx = filterBox.getSelectedIndex();
                Category category = categories.get(idx);
                Set<Integer> selection = selected.get(idx);
                for (int i = 0; i < templates.size(); i++) {
                    UITerm t = templates.get(i);
                    if (category.contains(t)) {
                        valueBox.addItem(t.getName(), Integer.toString(i));
                        if (selection != null && selection.contains(i)) {
                            valueBox.setItemSelected(valueBox.getItemCount() - 1, true);
                        }
                    }
                }
            }
        });

        valueBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                Set<Integer> selection = new HashSet<Integer>();
                for (int i = 0; i < valueBox.getItemCount(); i++) {
                    if (valueBox.isItemSelected(i)) {
                        selection.add(Integer.parseInt(valueBox.getValue(i)));
                    }
                }
                selected.put(filterBox.getSelectedIndex(), selection);
            }
        });

        initWidget(Binder.BINDER.createAndBindUi(this));

        filterBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), filterBox);

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!selected.isEmpty()) {
                    fireDialogCloseEvent(getSelection(), true);
                }
            }
        });

        selectNone.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireDialogCloseEvent(new ArrayList<UITerm>(), true);
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireDialogCloseEvent(null, false);
            }
        });
    }

    private List<Category> extractCategories(List<UITerm> terms) {
        Set<String> exist = new HashSet<String>();
        List<Category> list = new ArrayList<Category>();
        for (UITerm t : terms) {
            if (exist.add(t.getCategory())) {
                list.add(Category.create(t.getCategory()));
            }
        }
        return list;
    }

    private void fireDialogCloseEvent(List<UITerm> selection, boolean isOk) {
        DialogCloseEvent.fire(this, selection, isOk);
    }

    @Override
    public HandlerRegistration addDialogCloseHandler(DialogCloseHandler<List<UITerm>> handler) {
        return addHandler(handler, DialogCloseEvent.getType());
    }

    private List<UITerm> getSelection() {
        List<UITerm> list = new ArrayList<UITerm>();
        for (Integer categoryIndex : selected.keySet()) {
            Set<Integer> selection = selected.get(categoryIndex);
            for (Integer termIndex : selection) {
                list.add(templates.get(termIndex));
            }
        }
        return list;
    }

    private static final class Category {
        private static Category OTHER = new Category("Other");
        private final String name;

        private Category(String name) {
            this.name = name;
        }

        public static Category create(String name) {
            return isEmpty(name) ? OTHER : new Category(name);
        }

        public boolean contains(UITerm t) {
            String category = t.getCategory();
            if (this != OTHER) {
                return name.equals(category);
            }
            return isEmpty(category);
        }

        private static boolean isEmpty(String s) {
            return s == null || "".equals(s);
        }
    }
}
