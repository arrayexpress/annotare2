<%--
  ~ Copyright 2009-2012 European Molecular Biology Laboratory
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
<%@ page import="uk.ac.ebi.fg.annotare2.web.server.servlet.utils.ValidationErrors" %>
<%
    ValidationErrors errors = (ValidationErrors) request.getAttribute("errors");
    if (errors != null) {
        pageContext.setAttribute("dummyErrors", errors.getErrors());
        pageContext.setAttribute("usernameErrors", errors.getErrors("username"));
        pageContext.setAttribute("passwordErrors", errors.getErrors("password"));
    }

    String[] values = request.getParameterValues("username");
    pageContext.setAttribute("username", values == null ? "" : values[0]);
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Annotare 2.0 - Sign In</title>
    <style type="text/css">
        .error {
            color: red;
        }
    </style>
</head>
<body>

<form method="POST">
    <p class="error">${dummyErrors}</p>
    <table>
        <tr>
            <td>username:</td>
            <td><input type="text" name="username" value="${username}"/></td>
            <td class="error">${usernameErrors}</td>
        </tr>
        <tr>
            <td>password:</td>
            <td><input type="password" name="password"/></td>
            <td class="error">${passwordErrors}</td>
        </tr>
        <tr>
            <td colspan="3">
                <button name="signIn">sign in</button>
            </td>
        </tr>
    </table>
</form>

</body>
</html>


