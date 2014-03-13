package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class TableDataHandler implements ITableDataHandler {

    private final List<String[]> rows = new ArrayList<String[]>();
    private final List<String> row = new ArrayList<String>();
    private Set<TableParser.Option> options;

    public void setOptions(Set<TableParser.Option> options) {
        this.options = options;
    }

    public void startTable() {

    }

    public void addColumn(String value) {
        row.add(
                null != value && options.contains(TableParser.Option.TRIM_COLUMN_WHITESPACE) ?
                        value.trim() : value
        );
    }

    public void addRow() {
        if (options.contains(TableParser.Option.TRIM_EMPTY_TRAILING_COLUMNS)) {
            trimEmptyTrilingColumns();
        }
        rows.add(row.toArray(new String[row.size()]));
        row.clear();
    }

    public void endTable() {
        if (options.contains(TableParser.Option.TRIM_EMPTY_TRAILING_ROWS)) {
            trimEmptyTrailingRows();
        }
    }

    public boolean needsData() {
        return true;
    }

    public String[][] getTable() {
        return rows.toArray(new String[rows.size()][]);
    }

    protected List<String> getRow() {
        return row;
    }

    private void trimEmptyTrilingColumns() {
        for (ListIterator<String> i = row.listIterator(row.size()); i.hasPrevious(); ) {
            if (i.previous().isEmpty()) {
                i.remove();
            } else {
                break;
            }
        }
    }

    private void trimEmptyTrailingRows() {
        for (ListIterator<String[]> i = rows.listIterator(rows.size()); i.hasPrevious(); ) {
            if (isRowEmpty(i.previous())) {
                i.remove();
            } else {
                break;
            }
        }
    }

    private boolean isRowEmpty(String[] row) {
        for (String col : row) {
            if (!col.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
