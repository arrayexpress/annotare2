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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.idf;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.ResizableHeader;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class IdfSheetModeViewImpl extends Composite implements IdfSheetModeView {

    public IdfSheetModeViewImpl() {
        CellTable<String> cellTable = new CellTable<String>();
        //cellTable.setWidth("100%", true);

        for (int i = 0; i < 5; i++) {
            Column<String, String> column = new Column<String, String>(new TextCell()) {
                @Override
                public String getValue(String object) {
                    return object;
                }
            };
            cellTable.addColumn(column, new ResizableHeader<String>("test", cellTable, column));
        }

        ListDataProvider<String> dataProvider = new ListDataProvider<String>();
        dataProvider.setList(asList("1", "2", "3", "4", "5"));
        dataProvider.setList(asList("1", "2", "3", "4", "5"));
        dataProvider.setList(asList("1", "2", "3", "4", "5"));
        dataProvider.addDataDisplay(cellTable);

        initWidget(cellTable);
    }
}
