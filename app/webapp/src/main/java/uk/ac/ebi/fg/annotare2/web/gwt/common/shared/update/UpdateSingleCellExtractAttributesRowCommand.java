package uk.ac.ebi.fg.annotare2.web.gwt.common.shared.update;

import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SingleCellExtractAttributesRow;

public class UpdateSingleCellExtractAttributesRowCommand implements ExperimentUpdateCommand {
    private SingleCellExtractAttributesRow row;

    @SuppressWarnings("unused")
    UpdateSingleCellExtractAttributesRowCommand() {
        /* used by GWT serialization */
    }

    public UpdateSingleCellExtractAttributesRowCommand(SingleCellExtractAttributesRow row) {
        this.row = row;
    }

    @Override
    public void execute(ExperimentUpdatePerformer performer) {
        performer.updateSingleCellExtractAttributes(row);
    }

    @Override
    public boolean isCritical() {
        return false;
    }
}
