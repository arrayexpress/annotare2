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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.DialogCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class MultiSelectListDialog extends DialogBox {

    interface Binder extends UiBinder<Widget, MultiSelectListDialog> {
        Binder BINDER = GWT.create(Binder.class);
    }

    @UiField
    Button cancelButton;

    @UiField
    Button okButton;

    @UiField
    ScrollPanel centerPanel;

    private SelectionModel<String> selectionModel;
    private ListDataProvider<String> dataProvider;

    private final DialogCallback<List<String>> callback;

    public MultiSelectListDialog(String title, Map<String, Boolean> items, DialogCallback<List<String>> callback) {
        this.callback = callback;

        setModal(true);
        setGlassEnabled(true);
        setText(title);

        setWidget(Binder.BINDER.createAndBindUi(this));
        centerPanel.add(createCellTable(items));

        center();
    }

    private CellTable<String> createCellTable(Map<String, Boolean> items) {
        CellTable<String> cellTable = new CellTable<String>();
        cellTable.setWidth("100%", true);
        cellTable.setEmptyTableWidget(new Label("empty list"));
        cellTable.setVisibleRange(0, items.size());

        selectionModel = new MultiSelectionModel<String>();
        cellTable.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<String>createCheckboxManager());

        Column<String, Boolean> checkboxColumn = new Column<String, Boolean>(new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(String object) {
                return selectionModel.isSelected(object);
            }
        };
        cellTable.addColumn(checkboxColumn);
        cellTable.setColumnWidth(checkboxColumn, 40, Style.Unit.PX);

        Column<String, String> valueColumn = new Column<String, String>(new TextCell()) {
            @Override
            public String getValue(String object) {
                return object;
            }
        };
        cellTable.addColumn(valueColumn);

        dataProvider = new ListDataProvider<String>();
        dataProvider.addDataDisplay(cellTable);
        dataProvider.setList(new ArrayList<String>(items.keySet()));

        for (String item : items.keySet()) {
            selectionModel.setSelected(item, items.get(item));
        }
        return cellTable;
    }

    @UiHandler("cancelButton")
    void cancelClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onCancel();
        }
    }

    @UiHandler("okButton")
    void okClicked(ClickEvent event) {
        hide();
        if (null != callback) {
            callback.onOkay(getSelected());
        }
    }

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (Event.ONKEYDOWN == event.getTypeInt()) {
            if (KeyCodes.KEY_ESCAPE == event.getNativeEvent().getKeyCode()) {
                hide();
                if (null != callback) {
                    callback.onCancel();
                }
            }
        }
    }

    private List<String> getSelected() {
        List<String> selected = new ArrayList<String>();
        for (String item : dataProvider.getList()) {
            if (selectionModel.isSelected(item)) {
                selected.add(item);
            }
        }
        return selected;
    }
}
