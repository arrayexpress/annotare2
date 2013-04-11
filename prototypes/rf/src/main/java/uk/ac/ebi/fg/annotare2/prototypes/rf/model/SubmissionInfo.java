package uk.ac.ebi.fg.annotare2.prototypes.rf.model;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class SubmissionInfo {

    private String title;

    private String description;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
