package com.google.gwt.user.cellview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;

import java.util.logging.Logger;

public class CustomDataGrid<T> extends DataGrid<T> {
    public interface CustomDataGridResources extends DataGrid.Resources {

        @Override
        @Source("CustomDataGrid.css")
        Style dataGridStyle();

    }

    private final Logger logger = Logger.getLogger("CustomDataGrid");

    public CustomDataGrid() {
        super(50, (CustomDataGridResources)GWT.create(CustomDataGridResources.class));
        //getTableHeadElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
        //getTableBodyElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
    }

    @Override
    public void onBrowserEvent2(Event event) {
        //logger.log(Level.INFO, event.getType());
        super.onBrowserEvent2(event);
    }
}
