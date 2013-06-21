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
import com.google.gwt.user.client.ui.ScrollPanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class DataFileListView extends Composite {

    public DataFileListView() {
        ScrollPanel scrollPanel = new ScrollPanel();
        initWidget(scrollPanel);

        List<FileRow> values = new ArrayList<FileRow>();
        values.add(new FileRow(
                "data.raw.1.zip",
                "424896a587b9a879c9a66c52bfa76424",
                "10Mb",
                new Date(),
                true));
        values.add(new FileRow(
                "data.raw.2.zip",
                "424896a587b9a879c9a66c52bfa76424",
                "10Mb",
                new Date(),
                true));
        values.add(new FileRow(
                "data.raw.3.zip",
                "424896a587b9a879c9a66c52bfa76424",
                "10Mb",
                new Date(),
                true));

        CellTable<FileRow> grid = new CellTable<FileRow>();
        grid.addColumn(new Column<FileRow, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(FileRow object) {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                sb.appendEscapedLines(object.getFileName() + "\n" + object.getMd5());
                return sb.toSafeHtml();
            }
        });

        grid.addColumn(new Column<FileRow, String>(new TextCell()) {
            @Override
            public String getValue(FileRow object) {
                return object.getSize();
            }
        });

        grid.addColumn(new Column<FileRow, Date>(new DateCell(DateTimeFormat.getFormat("dd/MM/yyyy HH:mm"))) {
            @Override
            public Date getValue(FileRow object) {
                return object.getCreated();
            }
        });

        grid.addColumn(new Column<FileRow, String>(new TextCell()) {
            @Override
            public String getValue(FileRow object) {
                return object.isValid() ? "ok" : "?";
            }
        });

        Column<FileRow, String> deleteButton = new Column<FileRow, String>(new ButtonCell()) {
            @Override
            public String getValue(FileRow object) {
                return "delete";
            }
        };
        deleteButton.setFieldUpdater(new FieldUpdater<FileRow, String>() {
            @Override
            public void update(int index, FileRow object, String value) {
                Window.confirm("The file " + object.getFileName() + " will be removed from the server. Do you want to continue?");
            }
        });
        grid.addColumn(deleteButton);

        grid.setRowData(values);

        scrollPanel.add(grid);
    }

    private static class FileRow {

        private String fileName;
        private String size;
        private Date created;
        private String md5;
        private boolean isValid;

        private FileRow(String fileName, String md5, String size, Date created, boolean isValid) {
            this.fileName = fileName;
            this.size = size;
            this.created = created;
            this.md5 = md5;
            this.isValid = isValid;
        }

        private String getFileName() {
            return fileName;
        }

        private String getSize() {
            return size;
        }

        private Date getCreated() {
            return created;
        }

        private String getMd5() {
            return md5;
        }

        private boolean isValid() {
            return isValid;
        }
    }
}
