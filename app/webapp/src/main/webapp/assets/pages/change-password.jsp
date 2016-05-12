<%--
  ~ Copyright 2009-2016 European Molecular Biology Laboratory
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f"%>
<%@page isELIgnored="false" %>
<%@page import="uk.ac.ebi.fg.annotare2.web.server.servlets.utils.ValidationErrors"%>
<%
    ValidationErrors errors = (ValidationErrors) request.getAttribute("errors");
    if (errors != null) {
        pageContext.setAttribute("dummyErrors", errors.getErrors());
        pageContext.setAttribute("emailErrors", errors.getErrors("email"));
        pageContext.setAttribute("tokenErrors", errors.getErrors("token"));
        pageContext.setAttribute("passwordErrors", errors.getErrors("password"));
        pageContext.setAttribute("confirmPasswordErrors", errors.getErrors("confirm-password"));
    }

    String email = request.getParameter("email");
    if (null == email) {
        email = (String)session.getAttribute("email");
    }
    pageContext.setAttribute("userEmail", email == null ? "" : email);
%>
<section class="grid_12 push_6">
    <h2>Change password request</h2>
    <form method="POST">
        <div><c:out value="${sessionScope.info}" /><c:remove var="info" scope="session" /></div>
        <div class="error">${dummyErrors}</div>
        <c:choose>
            <c:when test="${requestScope.phase == 'email'}">
                <div><label for="email">Email address</label></div>
                <div>
                    <c:choose>
                        <c:when test="${pageScope.userEmail == ''}">
                            <input type="text" id="email" name="email" autofocus="autofocus"/>
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="email" name="email" value="${pageScope.userEmail}"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="error">${emailErrors}</div>
            </c:when>
            <c:when test="${requestScope.phase == 'token'}">
                <div><label for="token">Code</label></div>
                <div>
                    <input type="hidden" name="email" value="${pageScope.userEmail}"/>
                    <input type="text" id="token" name="token" autofocus="autofocus"/>
                </div>
                <div class="error">${tokenErrors}</div>
            </c:when>
            <c:otherwise>
                <div><label for="password">Password</label></div>
                <div>
                    <input type="hidden" name="email" value="${pageScope.userEmail}"/>
                    <input type="hidden" name="token" value="${param.token}"/>
                    <input type="password" id="password" name="password" autofocus="autofocus"/>
                </div>
                <div class="error">${passwordErrors}</div>
                <div><label for="confirm-password">Confirm password</label></div>
                <div><input type="password" id="confirm-password" name="confirm-password"/></div>
                <div class="error">${confirmPasswordErrors}</div>
            </c:otherwise>
        </c:choose>
        <div>
            <c:choose>
                <c:when test="${requestScope.phase == 'email' && pageScope.userEmail != ''}">
                    <input type="submit" class="submit" name="changePassword" value="Send" autofocus="autofocus"/>
                </c:when>
                <c:otherwise>
                    <input type="submit" class="submit" name="changePassword" value="Send"/>
                </c:otherwise>
            </c:choose>
            <input type="hidden" name="phase" value="${requestScope.phase}"/>
        </div>
    </form>
</section>