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

package uk.ac.ebi.fg.annotare2.web.server.servlets;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.web.server.AllRpcServicePaths;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountServiceException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.LOGIN;
import static uk.ac.ebi.fg.annotare2.web.server.servlets.ServletNavigation.PRIVACY_NOTICE;

/**
 * @author Olga Melnichuk
 */
public class SecurityFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    @Inject
    private AccountService accountService;

    @Inject
    private AllRpcServicePaths rpcServicePaths;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if (!accountService.isLoggedIn(request)) {
                forceLogin(request, (HttpServletResponse) servletResponse);
                return;
            }
            try {
                if(!accountService.isPrivacyNoticeAccepted(request)){
                    PRIVACY_NOTICE.redirect(request, (HttpServletResponse) servletResponse);
                    return;
                }
            } catch (AccountServiceException e) {
                e.printStackTrace();
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


        if (isRpcServicePath(request)) {
            log.debug("Is an RPC service path; so returning unauthorised ({}) code..", HttpServletResponse.SC_UNAUTHORIZED);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        log.debug("Not an RPC service path");
        LOGIN.saveAndRedirect(request, response);
    }

    private boolean isRpcServicePath(HttpServletRequest request) {
       return rpcServicePaths.recognizeUri(request.getRequestURI());
    }
}
