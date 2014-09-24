/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ProtocolRow;

public class ProtocolHardwareColumn extends Column<ProtocolRow, String> {

    private final Cell<String> defaultCell;
    private final Cell<String> sequencingCell;
    private Cell<String> activeCell;

    public ProtocolHardwareColumn(Cell<String> defaultCell, Cell<String> sequencingCell) {
        super(null);
        this.defaultCell = defaultCell;
        this.sequencingCell = sequencingCell;
        this.activeCell = defaultCell;
    }

    @Override
    public Cell<String> getCell() {
        return activeCell;
    }

    public void onBrowserEvent(Cell.Context context, Element elem, final ProtocolRow object, NativeEvent event) {
        setActiveCell(object);
        final int index = context.getIndex();
        ValueUpdater<String> valueUpdater = new ValueUpdater<String>() {
            @Override
            public void update(String value) {
                getFieldUpdater().update(index, object, value);
            }
        };
        activeCell.onBrowserEvent(context, elem, getValue(object), event, valueUpdater);
    }

    @Override
    public void render(Cell.Context context, ProtocolRow object, SafeHtmlBuilder sb) {
        setActiveCell(object);
        activeCell.render(context, getValue(object), sb);
    }

    @Override
    public String getValue(ProtocolRow row) {
        String v = row.getHardware();
        return v == null ? "" : v;
    }

    private void setActiveCell(ProtocolRow object) {
        if (isSequencingProtocol(object)) {
            activeCell = sequencingCell;
        } else {
            activeCell = defaultCell;
        }
    }
    private boolean isSequencingProtocol(ProtocolRow object) {
        return "EFO_0004170".equals(object.getProtocolType().getAccession());
    }
}
