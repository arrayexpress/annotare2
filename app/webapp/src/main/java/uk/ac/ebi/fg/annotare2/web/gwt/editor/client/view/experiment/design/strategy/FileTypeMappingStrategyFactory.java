package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.strategy;

import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfileType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FileTypeMappingStrategyFactory {
    private static final Map<ExperimentProfileType, Supplier<FileTypeMappingStrategy>> strategies = new HashMap<>();

    static {
        strategies.put(ExperimentProfileType.SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.SINGLE_CELL_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.PLANT_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.SINGLE_CELL_PLANT_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.HUMAN_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.SINGLE_CELL_HUMAN_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.ANIMAL_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.SINGLE_CELL_ANIMAL_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.CELL_LINE_SEQUENCING, SequencingStrategy::new);
        strategies.put(ExperimentProfileType.SINGLE_CELL_CELL_LINE_SEQUENCING, SequencingStrategy::new);

        strategies.put(ExperimentProfileType.TWO_COLOR_MICROARRAY, TwoColorMicroarrayStrategy::new);
        strategies.put(ExperimentProfileType.PLANT_TWO_COLOR_MICROARRAY, TwoColorMicroarrayStrategy::new);
        strategies.put(ExperimentProfileType.HUMAN_TWO_COLOR_MICROARRAY, TwoColorMicroarrayStrategy::new);
        strategies.put(ExperimentProfileType.ANIMAL_TWO_COLOR_MICROARRAY, TwoColorMicroarrayStrategy::new);
        strategies.put(ExperimentProfileType.CELL_LINE_TWO_COLOR_MICROARRAY, TwoColorMicroarrayStrategy::new);

        strategies.put(ExperimentProfileType.ONE_COLOR_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.METHYLATION_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.PLANT_ONE_COLOR_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.PLANT_METHYLATION_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.HUMAN_ONE_COLOR_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.HUMAN_METHYLATION_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.ANIMAL_ONE_COLOR_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.ANIMAL_METHYLATION_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.CELL_LINE_ONE_COLOR_MICROARRAY, OneColorMicroArrayStrategy::new);
        strategies.put(ExperimentProfileType.CELL_LINE_METHYLATION_MICROARRAY, OneColorMicroArrayStrategy::new);
    }

    public static FileTypeMappingStrategy getStrategy(ExperimentProfileType experimentType){
        Supplier<FileTypeMappingStrategy> supplier = strategies.get(experimentType);
        if (supplier == null) {
            throw new IllegalArgumentException("Unsupported experiment type: " + experimentType);
        }
        return supplier.get();
    }
}
