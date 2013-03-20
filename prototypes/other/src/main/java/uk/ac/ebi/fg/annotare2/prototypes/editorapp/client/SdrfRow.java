package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Olga Melnichuk
 */
public class SdrfRow {
    private final Map<SdrfColumn, String> values = new HashMap<SdrfColumn, String>();
    private final int index;
    private String name;

    public SdrfRow(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getValue(SdrfColumn column) {
        return values.get(column);
    }
}
