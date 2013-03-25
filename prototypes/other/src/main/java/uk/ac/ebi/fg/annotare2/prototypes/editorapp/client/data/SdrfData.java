package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.data;

import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfColumn;
import uk.ac.ebi.fg.annotare2.prototypes.editorapp.client.SdrfSection;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class SdrfData {

    private static SdrfData INSTANCE = new SdrfData();

    private final List<SdrfSection> sections = new ArrayList<SdrfSection>();
    private Map<Pair<SdrfSection>, Set<Pair<Integer>>> assosiations = new HashMap<Pair<SdrfSection>, Set<Pair<Integer>>>();

    {
        SdrfSection sources = new SdrfSection(SdrfSection.Type.SOURCES);
        List<SdrfColumn> columns = new ArrayList<SdrfColumn>();
        columns.add(new SdrfColumn(SdrfColumn.Type.CHARCTERISTIC, "OrganismPart"));
        columns.add(new SdrfColumn(SdrfColumn.Type.CHARCTERISTIC, "DevelopmentalStage"));
        sources.setColumns(columns);
        sources.addRow("Source_1");
        sources.addRow("Source_2");
        sources.addRow("Source_3");
        sections.add(sources);

        SdrfSection samples = new SdrfSection(SdrfSection.Type.SAMPLES);
        columns = new ArrayList<SdrfColumn>();
        columns.add(new SdrfColumn(SdrfColumn.Type.PROTOCOL, null));
        samples.setColumns(columns);
        samples.addRow("Sample_1");
        samples.addRow("Sample_2");
        samples.addRow("Sample_3");
        sections.add(samples);

        SdrfSection extracts = new SdrfSection(SdrfSection.Type.EXTRACTS);
        columns = new ArrayList<SdrfColumn>();
        columns.add(new SdrfColumn(SdrfColumn.Type.MATERIAL_TYPE, null));
        columns.add(new SdrfColumn(SdrfColumn.Type.PROTOCOL, null));
        extracts.addRow("Extract_1");
        extracts.addRow("Extract_2");
        extracts.addRow("Extract_3");
        sections.add(extracts);

        SdrfSection labeledExtracts = new SdrfSection((SdrfSection.Type.LABELED_EXTRACTS));
        columns = new ArrayList<SdrfColumn>();
        columns.add(new SdrfColumn(SdrfColumn.Type.LABEL, null));
        columns.add(new SdrfColumn(SdrfColumn.Type.MATERIAL_TYPE, null));
        columns.add(new SdrfColumn(SdrfColumn.Type.PROTOCOL, null));
        labeledExtracts.addRow("LabeledExtract_1_1");
        labeledExtracts.addRow("LabeledExtract_1_2");
        labeledExtracts.addRow("LabeledExtract_2_1");
        labeledExtracts.addRow("LabeledExtract_2_2");
        labeledExtracts.addRow("LabeledExtract_3_1");
        labeledExtracts.addRow("LabeledExtract_3_2");
        sections.add(labeledExtracts);

        SdrfSection assays = new SdrfSection(SdrfSection.Type.ASSAYS);
        columns = new ArrayList<SdrfColumn>();
        columns.add(new SdrfColumn(SdrfColumn.Type.ARRAY_DESIGN, null));
        columns.add(new SdrfColumn(SdrfColumn.Type.PROTOCOL, null));
        assays.addRow("Hyb_1_1");
        assays.addRow("Hyb_1_2");
        assays.addRow("Hyb_2_1");
        assays.addRow("Hyb_2_2");
        assays.addRow("Hyb_3_1");
        assays.addRow("Hyb_3_2");
        sections.add(assays);

        SdrfSection dataFiles = new SdrfSection(SdrfSection.Type.ARRAY_DATA_FILES);
        columns = new ArrayList<SdrfColumn>();
        columns.add(new SdrfColumn(SdrfColumn.Type.PROTOCOL, null));
        dataFiles.addRow("Hyb_1_1.CELL");
        dataFiles.addRow("Hyb_1_2.CELL");
        dataFiles.addRow("Hyb_2_1.CELL");
        dataFiles.addRow("Hyb_2_2.CELL");
        dataFiles.addRow("Hyb_3_1.CELL");
        dataFiles.addRow("Hyb_3_2.CELL");
        sections.add(dataFiles);

        assosiations.put(sectionPair(sources, samples),
                new HashSet<Pair<Integer>>(asList(intPair(0, 0), intPair(1, 1), intPair(2, 2))));

        assosiations.put(sectionPair(samples, extracts),
                new HashSet<Pair<Integer>>(asList(intPair(0, 0), intPair(1, 1), intPair(2, 2))));

        assosiations.put(sectionPair(extracts, labeledExtracts),
                new HashSet<Pair<Integer>>(asList(intPair(0, 0), intPair(0, 1),
                        intPair(1, 2), intPair(1, 3),
                        intPair(2, 4), intPair(2, 5))));

        assosiations.put(sectionPair(labeledExtracts, assays),
                new HashSet<Pair<Integer>>(asList(intPair(0, 0), intPair(1, 1),
                        intPair(2, 2), intPair(3, 3),
                        intPair(4, 4), intPair(5, 5))));

        assosiations.put(sectionPair(assays, dataFiles),
                new HashSet<Pair<Integer>>(asList(intPair(0, 0), intPair(1, 1),
                        intPair(2, 2), intPair(3, 3),
                        intPair(4, 4), intPair(5, 5))));

    }

    private final Set<SdrfValue> values = new LinkedHashSet<SdrfValue>();

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

    private SdrfValue findValue(String name, SdrfSection section, SdrfColumn column) {
        List<SdrfValue> values = getValuesFor(section, column);
        for (SdrfValue v : values) {
            if (name.equals(v.getName())) {
                return v;
            }
        }
        return null;
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
        SdrfSection prev = null;
        for (SdrfSection s : sections) {
            if (section.equals(s)) {
                return prev;
            }
            prev = s;
        }
        return null;
    }

    public void addOrReplace(String name, SdrfValue value) {
        SdrfValue old = findValue(name, value.getSection(), value.getColumn());
        if (old != null) {
            values.remove(old);
        }
        saveValue(value);
    }

    public Set<Pair<Integer>> getAssociations(SdrfSection sec1, SdrfSection sec2) {
        return assosiations.get(sectionPair(sec1, sec2));
    }

    private static Pair<Integer> intPair(int f, int s) {
        return new Pair<Integer>(f, s);
    }

    private static Pair<SdrfSection> sectionPair(SdrfSection sec1, SdrfSection sec2) {
        return new Pair<SdrfSection>(sec1, sec2);
    }

    public static class Pair<T> {
        private final T first;
        private final T second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }

        public T first(boolean reverse) {
            return reverse ? second : first;
        }

        public T second(boolean reverse) {
            return reverse ? first : second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (!first.equals(pair.first)) return false;
            if (!second.equals(pair.second)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = first.hashCode();
            result = 31 * result + second.hashCode();
            return result;
        }
    }
}
