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

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ImageResourceRenderer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.*;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;
import static com.google.gwt.user.client.Window.confirm;
import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.resources.EditorResources.EDITOR_RESOURCES;

/**
 * @author Olga Melnichuk
 */
public class DataFileListPanel extends SimpleLayoutPanel {

    private final ListDataProvider<DataFileRow> dataProvider;

    private final static int MAX_FILES = 40000;

    private Set<Long> selected = new HashSet<Long>();

    private Presenter presenter;

    public DataFileListPanel() {
        final CellTable<DataFileRow> grid = new CellTable<DataFileRow>(MAX_FILES);
        grid.setEmptyTableWidget(new Label("No files uploaded"));
        grid.setWidth("100%", true);

        final EditTextCell nameCell = new EditTextCell();
        Column<DataFileRow, String> column = new Column<DataFileRow, String>(nameCell) {
            @Override
            public String getValue(DataFileRow row) {
                return row.getName();
            }
        };
        column.setFieldUpdater(new FieldUpdater<DataFileRow, String>() {
            @Override
            public void update(int index, DataFileRow row, String value) {
                if (isNameValid(value, index)) {
                    presenter.renameFile(row, value);
                } else {
                    nameCell.clearViewData(row);
                    grid.redraw();
                }
            }
        });
        grid.addColumn(column);
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

        ActionCell<DataFileRow> actionCell = new ActionCell<DataFileRow>("Delete...", EDITOR_RESOURCES.smallLoader()) {
            @Override
            public boolean isActivated(DataFileRow row) {
                return selected.contains(row);
            }
        };
        Column<DataFileRow, DataFileRow> deleteButton = new Column<DataFileRow, DataFileRow>(actionCell) {
            @Override
            public DataFileRow getValue(DataFileRow object) {
                return object;
            }
        };
        deleteButton.setFieldUpdater(new FieldUpdater<DataFileRow, DataFileRow>() {
            @Override
            public void update(int index, DataFileRow object, DataFileRow value) {
                if (presenter != null && confirm("The file " + object.getName() + " will be removed from the server. Do you want to continue?")) {
                    selected.add(object.getId());
                    presenter.removeFile(object);
                }
            }
        });
        grid.addColumn(deleteButton);

        dataProvider = new ListDataProvider<DataFileRow>();
        dataProvider.addDataDisplay(grid);

        add(grid);
    }

    public void setRows(List<DataFileRow> rows) {
        dataProvider.setList(new ArrayList<DataFileRow>(rows));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private boolean isNameValid(String name, int rowIndex) {
        if (name == null || name.trim().isEmpty()) {
            Window.alert("File name should not be empty");
            return false;
        }

        if (!name.matches("^[_a-zA-Z0-9\\-\\.]+$")) {
            Window.alert("File name should only contain alphanumeric characters, underscores and dots");
            return false;
        }

        if (isDuplicated(name, rowIndex)) {
            Window.alert("File with the name '" + name + "' already exists");
            return false;
        }
        return true;
    }

    private boolean isDuplicated(String name, int rowIndex) {
        List<DataFileRow> rows = dataProvider.getList();
        int i = 0;
        for (DataFileRow row : rows) {
            if (i != rowIndex && name.equals(row.getName())) {
                return true;
            }
            i++;
        }
        return false;
    }

    public interface Presenter {
        void renameFile(DataFileRow dataFileRow, String newFileName);

        void removeFile(DataFileRow dataFileRow);
    }

    public static abstract class ActionCell<T> extends AbstractCell<T> {

        private final String title;
        private final ImageResource imageResource;
        private static ImageResourceRenderer renderer;

        public ActionCell(String title, ImageResource imageResource) {
            super(CLICK, KEYDOWN);
            this.title = title;
            this.imageResource = imageResource;
            renderer = new ImageResourceRenderer();
        }

        @Override
        public void onBrowserEvent(Context context, Element parent, T value,
                                   NativeEvent event, ValueUpdater<T> valueUpdater) {
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            if (!isActivated(value) && CLICK.equals(event.getType())) {
                EventTarget eventTarget = event.getEventTarget();
                if (!Element.is(eventTarget)) {
                    return;
                }
                if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
                    // Ignore clicks that occur outside of the main element.
                    onEnterKeyDown(context, parent, value, event, valueUpdater);
                }
            }
        }

        @Override
        public void render(Context context, T value, SafeHtmlBuilder sb) {
            if (isActivated(value)) {
                sb.append(renderer.render(imageResource));
            } else {
                sb.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\">");
                sb.appendEscaped(title);
                sb.appendHtmlConstant("</button>");
            }
        }

        @Override
        protected void onEnterKeyDown(Context context, Element parent, T value,
                                      NativeEvent event, ValueUpdater<T> valueUpdater) {
            if (valueUpdater != null) {
                valueUpdater.update(value);
            }
        }

        public abstract boolean isActivated(T value);
    }
}
