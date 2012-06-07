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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Olga Melnichuk
 */
class ServletUtils {

    private static final Pattern GWT_SRV_PARAM = Pattern.compile(".*?(gwt\\.codesvr=[0-9.:]+).*");

    public static void redirectToApp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirect("/index.html", request, response);
    }

    public static void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirect("/login", request, response);
    }

    public static void forwardToLogin(ServletContext context, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        context.getRequestDispatcher(newUrl("/login.jsp", request)).forward(request, response);
    }

    private static void redirect(String url, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(response.encodeRedirectURL(newUrl(url, request)));
    }

    private static String newUrl(String url, HttpServletRequest request) {
        StringBuilder newUrl = new StringBuilder(request.getContextPath()).append(url);
        String reqUrl = requestUrl(request);
        Matcher m = GWT_SRV_PARAM.matcher(reqUrl);
        if (m.matches()) {
            newUrl.append("?").append(m.group(1));
        }
        return newUrl.toString();
    }

    private static String requestUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        return isNullOrEmpty(queryString) ? uri : uri + "?" + queryString;
    }
}
