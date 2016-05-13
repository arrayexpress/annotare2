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
        pageContext.setAttribute("tokenErrors", errors.getErrors("token"));
    }

    String email = request.getParameter("email");
    if (null == email) {
        email = (String)session.getAttribute("email");
    }
    pageContext.setAttribute("userEmail", email == null ? "" : email);
%>
<section class="grid_12 push_6 form">
    <h2>Email verification</h2>
    <form method="POST">
        <div><c:out value="${sessionScope.info}" /><c:remove var="info" scope="session" /></div>
        <div class="error">${dummyErrors}</div>
        <div><label for="token">Code</label></div>
        <div>
            <input type="hidden" name="email" value="${pageScope.userEmail}"/>
            <input type="text" id="token" name="token" autofocus="autofocus"/>
        </div>
        <div class="error">${tokenErrors}</div>
        <div class="btn-row">
            <div><a href="?resend">Resend the code?</a></div>
            <div align="right"><input type="submit" class="submit" name="verify" value="Verify"/></div>
        </div>
    </form>
</section>