package uk.ac.ebi.fg.annotare2.db.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import uk.ac.ebi.fg.annotare2.db.model.FilterNames;

/**
 * @author Olga Melnichuk
 */
public class HibernateSessionFactory {

    private SessionFactory sessionFactory;

    private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();

    private HibernateSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getCurrentSession() throws HibernateException {
        Session session = get();
        if (!isOpen(session)) {
            throw new HibernateException("No open session");
        }
        return session;
    }

    public boolean hasOpenSession() throws HibernateException {
        return isOpen(get());
    }

    public Session openSessionWithFilters(String... filterNames) {
        closeSession();
        Session session = sessionFactory.openSession();
        threadSession.set(session);

        for (String filterName : filterNames) {
            session.enableFilter(filterName);
        }
        return session;
    }

    public Session openSession() {
        return openSessionWithFilters(FilterNames.NOT_DELETED_SUBMISSION_FILTER,
                FilterNames.NOT_DELETED_DATA_FILE_FILTER,
                FilterNames.NOT_FIXED_SUBMISSION_EXCEPTION_FILTER);
    }

    public void closeSession() throws HibernateException {
        Session session = threadSession.get();
        if (isOpen(session)) {
            session.close();
            threadSession.set(null);
        }
    }

    public static HibernateSessionFactory create() throws HibernateException {
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        Metadata metadata = new MetadataSources( standardRegistry )
                .getMetadataBuilder()
                .build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
                .build();
        return new HibernateSessionFactory(sessionFactory);
    }

    public void close() throws HibernateException {
        sessionFactory.close();
    }

    private Session get() {
        return threadSession.get();
    }

    private boolean isOpen(Session session) {
        return session != null && session.isOpen();
    }
}
