package uk.ac.ebi.fg.annotare2.web.server.services;

import com.google.common.base.Predicate;
import uk.ac.ebi.fg.annotare2.configmodel.ExperimentProfileType;
import uk.ac.ebi.fg.annotare2.web.server.services.utils.EfoGraph;

import static com.google.common.base.Predicates.alwaysFalse;

/**
 * @author Olga Melnichuk
 */
@Deprecated
public interface ProtocolPredicates {

    public static final ProtocolPredicates ALWAYS_FALSE_PREDICATES =
            new ProtocolPredicates() {
                @Override
                public Predicate<EfoGraph.Node> createProtocolTypePredicate(ExperimentProfileType expType) {
                    return alwaysFalse();
                }

                @Override
                public Predicate<EfoGraph.Node> createProtocolPredicate(ExperimentProfileType expType) {
                    return alwaysFalse();
                }
            };

    Predicate<EfoGraph.Node> createProtocolTypePredicate(ExperimentProfileType expType);

    Predicate<EfoGraph.Node> createProtocolPredicate(ExperimentProfileType expType);
}
