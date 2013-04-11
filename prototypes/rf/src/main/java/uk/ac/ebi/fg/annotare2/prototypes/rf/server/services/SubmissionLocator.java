package uk.ac.ebi.fg.annotare2.prototypes.rf.server.services;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Locator;
import uk.ac.ebi.fg.annotare2.prototypes.rf.server.SubmissionSource;
import uk.ac.ebi.fg.annotare2.prototypes.rf.model.Submission;

/**
 * @author Olga Melnichuk
 */
public class SubmissionLocator extends Locator<Submission, Long> {

    private final SubmissionSource source;

    @Inject
    public SubmissionLocator(SubmissionSource source) {
        this.source = source;
    }

    @Override
    public Submission create(Class<? extends Submission> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Submission find(Class<? extends Submission> clazz, Long id) {
        return source.find(id);
    }

    @Override
    public Class<Submission> getDomainType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getId(Submission domainObject) {
        return domainObject.getId();
    }

    @Override
    public Class<Long> getIdType() {
        return Long.class;
    }

    @Override
    public Object getVersion(Submission domainObject) {
        return domainObject.getVersion();
    }

}
