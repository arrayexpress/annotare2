package uk.ac.ebi.fg.annotare2.prototypes.layoutapp.client;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class SubmissionInfo {

    private String name;

    private Date created;

    private String status;

    public SubmissionInfo(String name, Date created, String status) {
        this.name = name;
        this.created = created;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public Date getCreated() {
        return created;
    }

    public String getStatus() {
        return status;
    }
}
