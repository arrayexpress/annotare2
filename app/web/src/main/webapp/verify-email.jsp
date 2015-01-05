<%--
  ~ Copyright 2009-2015 European Molecular Biology Laboratory
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
        pageContext.setAttribute("tokenErrors", errors.getErrors("token"));
    }

    String email = request.getParameter("email");
    if (null == email) {
        email = (String)session.getAttribute("email");
    }
    pageContext.setAttribute("email", email == null ? "" : email);
%>
<!doctype html>
<!--[if lt IE 7 ]><html class="ie6"><![endif]-->
<!--[if IE 7 ]><html class="ie7"><![endif]-->
<!--[if IE 8 ]><html class="ie8"><![endif]-->
<!--[if IE 9 ]><html class="ie9"><![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--><html class=""><!--<![endif]-->
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Annotare 2.0 - Email verification</title>
    <link type="text/css" rel="stylesheet" href="general.css">
    <link type="text/css" rel="stylesheet" href="login.css">
</head>
<body>
<div class="on-top warning noie67">Attention: Annotare does not support Internet Explorer versions 6 and 7.<br/>
    Please open Annotare in a different browser.<br/>Apologies for the inconvenience.</div>
<div class="outer">
    <div class="inner">
        <div class="frame">
            <form method="POST">
                <table class="form">
                    <tr>
                        <td></td>
                        <td><h1>Email verification</h1></td>
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
                        <td>Code</td>
                        <td>
                            <input type="hidden" name="email" value="${email}"/>
                            <input type="text" name="token" style="width:98%" autofocus="autofocus"/>
                        </td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${tokenErrors}</td>
                    </tr>
                    <tr class="row">
                        <td></td>
                        <td>
                            <button name="signIn">Verify</button>&nbsp;&nbsp;<a href="?resend">Resend the code?</a>
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


