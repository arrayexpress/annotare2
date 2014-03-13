package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.util.Set;

public interface ITableDataHandler {
    public void setOptions(Set<TableParser.Option> options);
    public void startTable();
    public void addColumn(String value);
    public void addRow();
    public void endTable();
    public boolean needsData();
    public String[][] getTable();
}
