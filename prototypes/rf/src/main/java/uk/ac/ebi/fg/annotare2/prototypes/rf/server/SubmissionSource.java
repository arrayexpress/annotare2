package uk.ac.ebi.fg.annotare2.prototypes.rf.server;

import com.google.common.collect.ImmutableList;
import uk.ac.ebi.fg.annotare2.prototypes.rf.model.Submission;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Olga Melnichuk
 */
public class SubmissionSource {

    private final ConcurrentMap<Long, Submission> submissions;

    public SubmissionSource() {
        submissions = new ConcurrentHashMap<Long, Submission>();
        Submission s = new Submission();
        s.getInfo().setTitle("submission 1");
        submissions.put(s.getId(), s);
    }

    public Submission find(Long id) {
        return submissions.get(id);
    }

    public void persist(Submission s) {
        submissions.put(s.getId(), s);
    }

    public List<Submission> getAll() {
        return ImmutableList.copyOf(submissions.values());
    }
}
