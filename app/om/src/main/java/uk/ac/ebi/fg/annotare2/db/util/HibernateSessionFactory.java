package uk.ac.ebi.fg.annotare2.db.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

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
            throw new HibernateException("no open session");
        }
        return session;
    }

    public Session openSession() {
        closeSession();
        Session session = sessionFactory.openSession();
        threadSession.set(session);
        return session;
    }

    public void closeSession() throws HibernateException {
        Session session = threadSession.get();
        if (isOpen(session)) {
            session.close();
        }
    }

    public static HibernateSessionFactory create() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.configure();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        return new HibernateSessionFactory(configuration.buildSessionFactory(serviceRegistry));
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
