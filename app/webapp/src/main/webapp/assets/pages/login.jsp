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
<%@page isELIgnored="false"%>
<%@page import="uk.ac.ebi.fg.annotare2.web.server.servlets.utils.ValidationErrors"%>
<%
    ValidationErrors errors = (ValidationErrors) request.getAttribute("errors");
    if (errors != null) {
        pageContext.setAttribute("dummyErrors", errors.getErrors());
        pageContext.setAttribute("emailErrors", errors.getErrors("email"));
        pageContext.setAttribute("passwordErrors", errors.getErrors("password"));
        pageContext.setAttribute("tokenErrors", errors.getErrors("token"));
    }

    String email = request.getParameter("email");
    if (null == email) {
        email = (String)session.getAttribute("email");
    }
    pageContext.setAttribute("email", email == null ? "" : email);
%>
<script>
    function fixEmail() {
        var email_elt = document.forms["login"]["email"];
        if (undefined != email_elt.value && email_elt.value.length > 0 && -1 == email_elt.value.indexOf("@")) {
            email_elt.value = email_elt.value + "@ebi.ac.uk";
        }
    }
</script>
<section class="grid_12 alpha">
    <form name="login" method="POST">
        <div>
            <c:out value="${sessionScope.info}"/>
            <c:remove var="info" scope="session"/>
        </div>
        <div>${dummyErrors}</div>
        <div>
            <label for="email">Email address</label>
        </div>
        <div>
            <c:choose>
                <c:when test="${email != ''}">
                    <input type="text" id="email" name="email" value="${email}" style="width:98%" onblur="fixEmail()"/>
                </c:when>
                <c:otherwise>
                    <input type="text" id="email" name="email" style="width:98%" autofocus="autofocus" onblur="fixEmail()"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div>${emailErrors}</div>
        <div><label for="password">Password</label></div>
        <div>
            <c:choose>
                <c:when test="${email != ''}">
                    <input type="password" name="password" id="password" style="width:98%" autofocus="autofocus"/>
                </c:when>
                <c:otherwise>
                    <input type="password" name="password" id="password" style="width:98%"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div>${passwordErrors}</div>
        <div><button name="signIn">Sign In</button></div>
        <div><a href="${pageContext.request.contextPath}/change-password/">Forgot your password?</a></div>
        <div>Don't have an account? <a href="${pageContext.request.contextPath}/sign-up/">Sign Up</a></div>
    </form>
</section>