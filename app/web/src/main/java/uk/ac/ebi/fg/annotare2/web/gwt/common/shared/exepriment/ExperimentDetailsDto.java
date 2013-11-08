package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment;

import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.configmodel.OntologyTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author Olga Melnichuk
 */
public class ExperimentDetailsDto implements IsSerializable {

    private String title;

    private String description;

    private Date experimentDate;

    private Date publicReleaseDate;

    private String aeExperimentType;

    private List<OntologyTerm> experimentalDesigns;

    ExperimentDetailsDto() {
        /*used by GWT serialization*/
    }

    public ExperimentDetailsDto(String title, String description, Date experimentDate, Date publicReleaseDate,
                                String aeExperimentType, Collection<OntologyTerm> experimentalDesigns) {
        this.title = title;
        this.description = description;
        this.experimentDate = experimentDate;
        this.publicReleaseDate = publicReleaseDate;
        this.aeExperimentType = aeExperimentType;
        this.experimentalDesigns = new ArrayList<OntologyTerm>(experimentalDesigns);
    }

    public String getDescription() {
        return description;
    }

    public Date getExperimentDate() {
        return experimentDate;
    }

    public Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getAeExperimentType() {
        return aeExperimentType;
    }

    public Collection<OntologyTerm> getExperimentalDesigns() {
        return unmodifiableCollection(experimentalDesigns);
    }
}
