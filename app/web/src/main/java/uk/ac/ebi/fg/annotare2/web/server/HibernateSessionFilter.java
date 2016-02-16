/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server;

import com.google.inject.Inject;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Olga Melnichuk
 */
public class HibernateSessionFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HibernateSessionFilter.class);

    @Inject
    private HibernateSessionFactory sessionFactory;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("Init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            sessionFactory.openSession();
            log.debug("Hibernate session has been opened");

            chain.doFilter(request, response);
        } catch (HibernateException e) {
            throw new ServletException(e);
        } finally {
            try {
                sessionFactory.closeSession();
                log.debug("Hibernate session has been closed");
            } catch (HibernateException e) {
                log.error("Unable to close hibernate session", e);
            }
        }
    }

    @Override
    public void destroy() {
        log.debug("Destroy");
    }
}
