package uk.ac.ebi.fg.annotare2.prototypes.rf.server.services;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.prototypes.rf.server.SubmissionSource;
import uk.ac.ebi.fg.annotare2.prototypes.rf.model.Submission;

import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SubmissionService {

    private final SubmissionSource source;

    @Inject
    public SubmissionService(SubmissionSource source) {
        this.source = source;
    }

    private List<Submission> findAllSubmissions() {
        return source.getAll();
    }

    public void persist(Submission sub) {
        source.persist(sub);
    }
}
