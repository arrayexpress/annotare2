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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@ page isELIgnored="false" %>
<%
    pageContext.setAttribute("errors", request.getAttribute("errors"));

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
    <title>Annotare 2.0 - Change password request</title>
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
                        <td><h1>Change password request</h1></td>
                    </tr>
                    <tr class="info">
                        <td></td>
                        <td><c:out value="${sessionScope.info}" /><c:remove var="info" scope="session" /></td>
                    </tr>
                    <tr class="error">
                        <td></td>
                        <td>${errors}</td>
                    </tr>
                    <tr class="row right">
                        <td>Email</td>
                        <td>
                            <c:choose>
                                <c:when test="${email != ''}">
                                    <input type="text" name="email" value="${email}" style="width:98%"/>
                                </c:when>
                                <c:otherwise>
                                    <input type="text" name="email" style="width:98%" autofocus="autofocus"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr class="row">
                        <td></td>
                        <td>
                            <c:choose>
                                <c:when test="${email != ''}">
                                    <button name="changePassword" autofocus="autofocus">Send</button>
                                </c:when>
                                <c:otherwise>
                                    <button name="changePassword">Send</button>
                                </c:otherwise>
                            </c:choose>
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


