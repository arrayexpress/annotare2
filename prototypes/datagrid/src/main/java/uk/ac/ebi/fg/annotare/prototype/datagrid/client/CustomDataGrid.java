package uk.ac.ebi.fg.annotare.prototype.datagrid.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.DataGrid;

public class CustomDataGrid<T> extends DataGrid<T> {
    public interface CustomDataGridResources extends DataGrid.Resources {

        @Override
        @Source("CustomDataGrid.css")
        Style dataGridStyle();

    }

    public CustomDataGrid() {
        super(50, (CustomDataGridResources)GWT.create(CustomDataGridResources.class));
        //getTableHeadElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
        //getTableBodyElement().getParentElement().getStyle().setProperty("borderCollapse", "collapse");
    }
}
