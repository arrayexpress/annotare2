package uk.ac.ebi.fg.annotare2.prototypes.rf.model;

/**
 * @author Olga Melnichuk
 */
public class Submission {

    private Long id;

    private Long version;

    private SubmissionInfo info;

    private SubmissionDesign design;

    public Submission() {
        id = System.currentTimeMillis();
        info = new SubmissionInfo();
        design = new SubmissionDesign();
    }

    public long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public SubmissionDesign getDesign() {
        return design;
    }

    public SubmissionInfo getInfo() {
        return info;
    }
}
