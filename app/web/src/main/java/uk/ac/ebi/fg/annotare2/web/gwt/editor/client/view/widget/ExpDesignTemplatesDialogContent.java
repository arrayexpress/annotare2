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
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.HandlerRegistration;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.idf.UITerm;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CloseEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.CloseEventHandler;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Olga Melnichuk
 */
public class ExpDesignTemplatesDialogContent extends Composite {

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

    private boolean cancelled = false;

    private ArrayList<Integer> selected = new ArrayList<Integer>();

    private ArrayList<UITerm> templates = new ArrayList<UITerm>();

    private ArrayList<Category> categories = new ArrayList<Category>();

    public ExpDesignTemplatesDialogContent(ArrayList<UITerm> terms) {
        templates.addAll(terms);
        categories = extractCategories(terms);

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
                Category category = categories.get(filterBox.getSelectedIndex());
                for (int i = 0; i < templates.size(); i++) {
                    UITerm t = templates.get(i);
                    if (category.contains(t)) {
                        valueBox.addItem(t.getName(), Integer.toString(i));
                        if (selected.contains(i)) {
                            valueBox.setItemSelected(valueBox.getItemCount() - 1, true);
                        }
                    }
                }
            }
        });

        valueBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                selected = new ArrayList<Integer>();
                for (int i = 0; i < valueBox.getItemCount(); i++) {
                    if (valueBox.isItemSelected(i)) {
                        selected.add(Integer.parseInt(valueBox.getValue(i)));
                    }
                }
            }
        });

        initWidget(Binder.BINDER.createAndBindUi(this));

        filterBox.setItemSelected(0, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), filterBox);

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!selected.isEmpty()) {
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

    private ArrayList<Category> extractCategories(ArrayList<UITerm> terms) {
        HashSet<String> exist = new HashSet<String>();
        ArrayList<Category> list = new ArrayList<Category>();
        for (UITerm t : terms) {
            if (exist.add(t.getCategory())) {
                list.add(Category.create(t.getCategory()));
            }
        }
        return list;
    }

    private void fireCloseEvent() {
        fireEvent(new CloseEvent());
    }

    public HandlerRegistration addCloseHandler(CloseEventHandler closeEventHandler) {
        return addHandler(closeEventHandler, CloseEvent.TYPE);
    }

    public boolean isCancelled() {
        return cancelled;
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
