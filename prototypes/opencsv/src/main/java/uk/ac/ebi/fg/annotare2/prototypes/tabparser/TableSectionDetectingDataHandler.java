package uk.ac.ebi.fg.annotare2.prototypes.tabparser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TableSectionDetectingDataHandler extends TableDataHandler {

    private final Set<String> sectionTags;
    private boolean isDetectionInProgress;
    private boolean hasSectionDetected;

    public TableSectionDetectingDataHandler(String... sectionTags) {
        this.sectionTags = new HashSet<String>(Arrays.asList(sectionTags));
    }

    @Override
    public void startTable() {
        hasSectionDetected = false;
        isDetectionInProgress = true;
    }

    @Override
    public boolean needsData() {
        return isDetectionInProgress;
    }

    @Override
    public void addRow() {
        if (isDetectionInProgress) {
            if (1 == getRow().size()) {
                if (!getRow().get(0).startsWith("#")) {
                    hasSectionDetected = sectionTags.contains(getRow().get(0));
                    isDetectionInProgress = false;
                }
            } else if (getRow().size() > 1) {
                hasSectionDetected = false;
                isDetectionInProgress = false;
            }
        }
    }

    public boolean hasSectionDetected() {
        return hasSectionDetected;
    }
}