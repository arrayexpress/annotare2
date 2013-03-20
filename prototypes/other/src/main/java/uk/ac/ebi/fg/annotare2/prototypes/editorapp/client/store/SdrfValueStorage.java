package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.store;

import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfColumn;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Olga Melnichuk
 */
public class SdrfValueStorage {

    public static final SdrfValueStorage INSTANCE = new SdrfValueStorage();

    private final Set<SdrfValue> values = new HashSet<SdrfValue>();

    private SdrfValueStorage() {
    }

    public boolean save(SdrfValue value) {
        return values.add(value);
    }

    public List<SdrfValue> getAll(SdrfSection section, SdrfColumn column) {
        List<SdrfValue> list = new ArrayList<SdrfValue>();
        for (SdrfValue v : values) {
            if (section.equals(v.getSection()) && column.equals(v.getColumn())) {
                list.add(v);
            }
        }
        return list;
    }

}
