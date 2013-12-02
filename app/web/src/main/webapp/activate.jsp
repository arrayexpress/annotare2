<%--
  ~ Copyright 2009-2013 European Molecular Biology Laboratory
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
<%--
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ page isELIgnored="false" %>
<%@ page import="uk.ac.ebi.fg.annotare2.web.server.servletsets.utils.ValidationErrors" %>
<%
    ValidationErrors errors = (ValidationErrors) request.getAttribute("errors");
    if (errors != null) {
        pageContext.setAttribute("dummyErrors", errors.getErrors());
        pageContext.setAttribute("emailErrors", errors.getErrors("email"));
        pageContext.setAttribute("passwordErrors", errors.getErrors("password"));
    }

    String[] values = request.getParameterValues("email");
    pageContext.setAttribute("email", values == null ? "" : values[0]);
%>
--%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Annotare 2.0 - New user registration</title>
    <link type="text/css" rel="stylesheet" href="general.css">
    <link type="text/css" rel="stylesheet" href="login.css">
</head>
<body>

<div class="outer">
    <div class="inner">

        <div class="frame">
            <form method="POST">
                <table class="form">
                    <tr>
                        <td></td>
                        <td><h1>Annotare 2.0</h1></td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${dummyErrors}</td>
                    </tr>
                    <tr class="row right">
                        <td>Email</td>
                        <td><input type="text" name="email" value="${email}" style="width:98%"/></td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${emailErrors}</td>
                    </tr>
                    <tr class="row right">
                        <td>Password</td>
                        <td><input type="password" name="password" style="width:98%"/></td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${passwordErrors}</td>
                    </tr>
                    <tr class="row">
                        <td></td>
                        <td>
                            <button name="signIn">Sign In</button>&nbsp;&nbsp;<a href="#" onclick="return false;">Forgot your password?</a>
                        </td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <div style="margin-top:10px;">Don't have an account? <a href="#" onclick="return false;">Sign Up</a></div>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="at-bottom copy">2013 EMBL-EBI, Functional Genomics Group</div>
</body>
</html>


