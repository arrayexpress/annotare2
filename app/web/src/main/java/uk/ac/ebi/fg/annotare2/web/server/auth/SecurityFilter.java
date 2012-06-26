/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.auth;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.ac.ebi.fg.annotare2.web.server.auth.ServletUtil.redirectToLogin;

/**
 * @author Olga Melnichuk
 */
public class SecurityFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Inject
    private AuthService authService;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if (!authService.isLoggedIn(request)) {
                forceLogin(request, (HttpServletResponse) servletResponse);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @SuppressWarnings("unchecked")
    private void forceLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("Unauthorised access; request headers: ");
        for(Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
            String name = e.nextElement();
            log.debug("--> {}: {}", name, request.getHeader(name));
        }


        if (isHtmlAccepted(request)) {
            log.debug("Client accepts HTML; redirecting to login page..");
            redirectToLogin(request, response, true);
            return;
        }
        log.debug("Client doesn't accept HTML; returning unauthorised ({}) code..", HttpServletResponse.SC_UNAUTHORIZED);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private boolean isHtmlAccepted(HttpServletRequest request) {
        String accept = request.getHeader("Accept").split(";")[0];
        boolean accepted = !isNullOrEmpty(accept) && (accept.contains("text/html")) ;

        String url = request.getRequestURL().toString();
        return accepted || url.contains(".html");
    }
}
