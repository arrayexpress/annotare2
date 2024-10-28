package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.strategy;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;

public class FileTypeMappingStrategyFactory {
    public static FileTypeMappingStrategy getStrategy(ExperimentProfileType experimentType){
        if(experimentType.isSequencing()){
            return new SequencingStrategy();
        } else if(experimentType.isTwoColorMicroarray()) {
            return new TwoColorMicroarrayStrategy();
        } else if(experimentType.isMicroarray()) {
            return new OneColorMicroArrayStrategy();
        } else {
            throw new IllegalArgumentException("Unsupported experiment type: " + experimentType);
        }
    }
}
