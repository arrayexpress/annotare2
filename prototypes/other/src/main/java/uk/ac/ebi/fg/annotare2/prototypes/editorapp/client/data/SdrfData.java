package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data;

import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfColumn;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfSection;

import java.util.*;

/**
 * @author Olga Melnichuk
 */
public class SdrfData {

    private static SdrfData INSTANCE = new SdrfData();

    private final List<SdrfSection> sections = new ArrayList<SdrfSection>();
    {
        for(SdrfSection.Type t : SdrfSection.Type.values()) {
            sections.add(new SdrfSection(t));
        }
    }

    private final Set<SdrfValue> values = new HashSet<SdrfValue>();

    private SdrfData() {
    }

    public boolean saveValue(SdrfValue value) {
        return values.add(value);
    }

    public List<SdrfValue> getValuesFor(SdrfSection section, SdrfColumn column) {
        List<SdrfValue> list = new ArrayList<SdrfValue>();
        for (SdrfValue v : values) {
            if (section.equals(v.getSection()) && column.equals(v.getColumn())) {
                list.add(v);
            }
        }
        return list;
    }


    public List<SdrfSection> getSections() {
        return new ArrayList<SdrfSection>(sections);
    }


    public static SdrfData get() {
        return INSTANCE;
    }

    public SdrfSection addSection(SdrfSection.Type type) {
        SdrfSection section = new SdrfSection(type);
        sections.add(section);
        return section;
    }

    public SdrfSection getPrevious(SdrfSection section) {
        SdrfSection prev  = null;
        for (SdrfSection s : sections) {
            if (section.equals(s)) {
                return prev;
            }
            prev = s;
        }
        return null;
    }
}
