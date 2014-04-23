<%--
  ~ Copyright 2009-2014 European Molecular Biology Laboratory
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ page isELIgnored="false" %>
<%@ page import="uk.ac.ebi.fg.annotare2.web.server.servlets.utils.ValidationErrors" %>
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
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Annotare 2.0 - Sign In</title>
    <link type="text/css" rel="stylesheet" href="general.css">
    <link type="text/css" rel="stylesheet" href="login.css">
    <script>
        function fixEmail() {
            var email_elt = document.forms["login"]["email"];
            if (undefined != email_elt.value && email_elt.value.length > 0 && -1 == email_elt.value.indexOf("@")) {
                email_elt.value = email_elt.value + "@ebi.ac.uk";
            }
        }
    </script>
</head>
<body>

<div class="outer">
    <div class="inner">

        <div class="frame">
            <form name="login" method="POST">
                <table class="form">
                    <tr>
                        <td></td>
                        <td><h1>Annotare 2.0 <span style="color:red">beta</span></h1></td>
                    </tr>
                    <tr class="info">
                        <td></td>
                        <td><c:out value="${sessionScope.info}" /><c:remove var="info" scope="session" /></td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${dummyErrors}</td>
                    </tr>
                    <tr class="row right">
                        <td>Email address</td>
                        <td>
                            <c:choose>
                                <c:when test="${email != ''}">
                                    <input type="text" name="email" value="${email}" style="width:98%" onblur="fixEmail()"/>
                                </c:when>
                                <c:otherwise>
                                    <input type="text" name="email" style="width:98%" autofocus="autofocus" onblur="fixEmail()"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${emailErrors}</td>
                    </tr>
                    <tr class="row right">
                        <td>Password</td>
                        <td>
                            <c:choose>
                                <c:when test="${email != ''}">
                                    <input type="password" name="password" style="width:98%" autofocus="autofocus"/>
                                </c:when>
                                <c:otherwise>
                                    <input type="password" name="password" style="width:98%"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${passwordErrors}</td>
                    </tr>
                    <tr class="row">
                        <td></td>
                        <td>
                            <button name="signIn">Sign In</button>&nbsp;&nbsp;<a href="./change-password">Forgot your password?</a>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <div style="margin-top:10px;">Don't have an account? <a href="./sign-up">Sign Up</a></div>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="at-bottom copy">2014 EMBL-EBI, Functional Genomics Group</div>
</body>
</html>


