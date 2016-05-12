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
<%@page import="uk.ac.ebi.fg.annotare2.web.server.servlets.utils.ValidationErrors"%>
<%
    ValidationErrors errors = (ValidationErrors) request.getAttribute("errors");
    if (errors != null) {
        pageContext.setAttribute("dummyErrors", errors.getErrors());
        pageContext.setAttribute("nameErrors", errors.getErrors("name"));
        pageContext.setAttribute("emailErrors", errors.getErrors("email"));
        pageContext.setAttribute("passwordErrors", errors.getErrors("password"));
        pageContext.setAttribute("confirmPasswordErrors", errors.getErrors("confirm-password"));
    }

    String[] values = request.getParameterValues("name");
    pageContext.setAttribute("userName", values == null ? "" : values[0]);

    values = request.getParameterValues("email");
    pageContext.setAttribute("userEmail", values == null ? "" : values[0]);
%>
<section class="grid_12 push_6">
    <h2>Please register to Annotare</h2>
    <form method="POST">
        <div class="error">${dummyErrors}</div>
        <div><label for="name">Full name</label></div>
        <div><input type="text" id="name" name="name" value="${pageScope.userName}"/></div>
        <div class="error">${nameErrors}</div>
        <div><label for="email">Email address</label></div>
        <div><input type="text" id="email" name="email" value="${pageScope.userEmail}"/></div>
        <div class="error">${emailErrors}</div>
        <div><label for="password">Password</label></div>
        <div><input type="password" id="password" name="password"/></div>
        <div class="error">${passwordErrors}</div>
        <div><label for="confirm-password">Confirm password</label></div>
        <div><input type="password" id="confirm-password" name="confirm-password"/></div>
        <div class="error">${confirmPasswordErrors}</div>
        <div class="btn-row">
            <div class="btn"><input type="submit" class="submit" name="signup" value="Register"/></div>
            <div class="alt-link">Already registered? Please <a href="${pageContext.request.contextPath}/login/">login</a></div>
        </div>
    </form>
</section>