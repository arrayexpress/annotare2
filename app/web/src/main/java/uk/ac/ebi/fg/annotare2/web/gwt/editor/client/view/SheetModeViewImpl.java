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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import uk.ac.ebi.fg.annotare2.magetab.table.Row;
import uk.ac.ebi.fg.annotare2.magetab.table.Table;

import java.util.ArrayList;

/**
 * @author Olga Melnichuk
 */
public class SheetModeViewImpl extends Composite implements SheetModeView {

    private final HorizontalPanel panel;

    public SheetModeViewImpl() {
        panel = new HorizontalPanel();
        initWidget(panel);
    }

    @Override
    public void setTable(Table table, boolean hasHeaders) {
        if (table == null || table.isEmpty()) {
            setContent(new Label("There's no data yet, come later"));
            return;
        }

        int tableWidth = table.getTrimmedWidth();
        int tableHeight = table.getHeight();

        ArrayList<ColumnConfig<Row, ?>> columnConfigs = new ArrayList<ColumnConfig<Row, ?>>();
        Row headers = table.getRow(0);

        for (int i = 0; i < tableWidth; i++) {
            final int colIndex = i;

            ColumnConfig<Row, String> config = new ColumnConfig<Row, String>(new ValueProvider<Row, String>() {
                @Override
                public String getValue(Row row) {
                    return row.getValue(colIndex);
                }

                @Override
                public void setValue(Row row, String value) {
                    row.getValue(colIndex);
                }

                @Override
                public String getPath() {
                    return null;
                }
            });

            if (hasHeaders) {
                config.setHeader(headers.getValue(i));
            } else {
                config.setHeader(new SafeHtml() {
                    @Override
                    public String asString() {
                        return "&nbsp;";
                    }
                });
            }
            config.setSortable(false);
            config.setMenuDisabled(true);
            columnConfigs.add(config);
        }

        //TODO move this to the Table
        ArrayList<Row> rows = new ArrayList<Row>();
        for (int j = hasHeaders ? 1 : 0; j < tableHeight; j++) {
            rows.add(table.getRow(j));
        }

        ListDataProvider<Row> dataProvider = new ListDataProvider<Row>();
        dataProvider.setList(rows);

        ListStore<Row> store = new ListStore<Row>(new ModelKeyProvider<Row>() {
            @Override
            public String getKey(Row item) {
                return item.toString();
            }
        });
        store.addAll(rows);

        ColumnModel<Row> columnModel = new ColumnModel<Row>(columnConfigs);

        Grid<Row> grid = new Grid<Row>(store, columnModel);
        grid.setColumnReordering(false);

        setContent(grid);
    }

    private void setContent(Widget w) {
        if (panel.getWidgetCount() > 0) {
            panel.remove(0);
        }
        panel.add(w);
    }
}