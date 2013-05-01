package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/**
 * @author Olga Melnichuk
 */
public class ExperimentDetails implements IsSerializable {

    private String title;

    private String description;

    private Date experimentDate;

    private Date publicReleaseDate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExperimentDate() {
        return experimentDate;
    }

    public void setExperimentDate(Date experimentDate) {
        this.experimentDate = experimentDate;
    }

    public Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public void setPublicReleaseDate(Date publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isContentEquals(ExperimentDetails that) {
        if (that == null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (experimentDate != null ? !experimentDate.equals(that.experimentDate) : that.experimentDate != null)
            return false;
        if (publicReleaseDate != null ? !publicReleaseDate.equals(that.publicReleaseDate) : that.publicReleaseDate != null)
            return false;
        return !(title != null ? !title.equals(that.title) : that.title != null);
    }


}
