package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.arraydesign.ArrayDesignDetailsDto;

/**
 * @author Olga Melnichuk
 */
public class UpdateArrayDesignDetailsCommand implements ArrayDesignUpdateCommand {

    private ArrayDesignDetailsDto details;

    @SuppressWarnings("unused")
    UpdateArrayDesignDetailsCommand() {
     /*used By GWT serialization */
    }

    public UpdateArrayDesignDetailsCommand(ArrayDesignDetailsDto details) {
        this.details = details;
    }

    @Override
    public void execute(ArrayDesignUpdatePerformer performer) {
        performer.updateDetails(details);
    }

    @Override
    public boolean isCritical() {
        return false;
    }
}
