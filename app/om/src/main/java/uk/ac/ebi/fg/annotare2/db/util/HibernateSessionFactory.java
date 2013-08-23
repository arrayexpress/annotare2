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

    private HibernateSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getCurrentSession(boolean forceCreate) throws HibernateException {
        try {
            return getCurrentSession();
        } catch (HibernateException e) {
            if (forceCreate) {
                return sessionFactory.openSession();
            }
            throw e;
        }
    }

    public Session getCurrentSession() throws HibernateException {
        return sessionFactory.getCurrentSession();
    }

    public void closeSession() throws HibernateException {
        Session currentSession = null;
        try {
            currentSession = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            // no session to close
        }

        if (currentSession != null && currentSession.isOpen()) {
            currentSession.close();
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
}
