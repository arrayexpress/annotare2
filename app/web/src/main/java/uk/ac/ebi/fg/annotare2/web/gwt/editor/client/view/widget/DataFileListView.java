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

import com.google.gwt.cell.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFileListView extends Composite {

    private ListDataProvider<DataFileRow> dataProvider;

    public DataFileListView() {
        ScrollPanel scrollPanel = new ScrollPanel();
        initWidget(scrollPanel);

        CellTable<DataFileRow> grid = new CellTable<DataFileRow>();
        grid.setEmptyTableWidget(new Label("You have not uploaded any data files yet"));
        grid.addColumn(new Column<DataFileRow, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(DataFileRow object) {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                String md5 = object.getMd5();
                sb.appendEscapedLines(object.getName() + "\n" + (md5 == null || md5.isEmpty() ? "" : md5));
                return sb.toSafeHtml();
            }
        });

        grid.addColumn(new Column<DataFileRow, Date>(new DateCell(DateTimeFormat.getFormat("dd/MM/yyyy HH:mm"))) {
            @Override
            public Date getValue(DataFileRow object) {
                return object.getCreated();
            }
        });

        grid.addColumn(new Column<DataFileRow, String>(new TextCell()) {
            @Override
            public String getValue(DataFileRow object) {
                return object.getStatus().getTitle();
            }
        });

        Column<DataFileRow, String> deleteButton = new Column<DataFileRow, String>(new ButtonCell()) {
            @Override
            public String getValue(DataFileRow object) {
                return "delete";
            }
        };
        deleteButton.setFieldUpdater(new FieldUpdater<DataFileRow, String>() {
            @Override
            public void update(int index, DataFileRow object, String value) {
                Window.confirm("The file " + object.getName() + " will be removed from the server. Do you want to continue?");
            }
        });
        grid.addColumn(deleteButton);
        scrollPanel.add(grid);

        dataProvider = new ListDataProvider<DataFileRow>();
        dataProvider.addDataDisplay(grid);
    }

    public void setRows(List<DataFileRow> rows) {
        dataProvider.setList(new ArrayList<DataFileRow>(rows));
    }
}
