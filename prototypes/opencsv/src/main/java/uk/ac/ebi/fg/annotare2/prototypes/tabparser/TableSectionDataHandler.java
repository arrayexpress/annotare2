package uk.ac.ebi.fg.annotare2.prototypes.tabparser;


public class TableSectionDataHandler extends TableDataHandler {

    private final String sectionStartTag;
    private final String sectionEndTag;

    private State state;

    public TableSectionDataHandler(String sectionStartTag, String sectionEndTag) {
        this.sectionStartTag = sectionStartTag;
        this.sectionEndTag = sectionEndTag;
    }

    @Override
    public void startTable() {
        state = State.SECTION_HAS_NOT_STARTED;
    }

    @Override
    public boolean needsData() {
        return !(State.SECTION_FINISHED == state);
    }

    @Override
    public void addRow() {
        switch (state) {
            case SECTION_HAS_NOT_STARTED:
                if (doesRowMatch(sectionStartTag)) {
                    state = State.SECTION_STARTED;
                }
                // discard row
                getRow().clear();
                break;
            case SECTION_STARTED:
                if (doesRowMatch(sectionEndTag)) {
                    state = State.SECTION_FINISHED;
                } else {
                    super.addRow();
                }
                break;
        }
    }

    private boolean doesRowMatch(String tag) {
        return 1 == getRow().size() &&
                getRow().get(0).trim().equalsIgnoreCase(tag);
    }

    enum State {
        SECTION_HAS_NOT_STARTED,
        SECTION_STARTED,
        SECTION_FINISHED
    }
}
