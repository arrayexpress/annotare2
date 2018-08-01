package uk.ac.ebi.fg.annotare2.web.gwt.common.model;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;

public class ExpProfile extends ExperimentProfile {

    private ExpProfileType type;

    public ExpProfile(ExpProfileType type) {
        super(type.getTitle());
        this.type = type;
    }

    public ExpProfile() {
        super("");
    }

    @Override
    public ExpProfileType getType() {
        return type;
    }
}
