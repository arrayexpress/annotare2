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
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
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
<t:analytics></t:analytics>
<script>
    function acceptPolicy(){
        var acceptCheck = document.getElementById("acceptPolicy");
        if(acceptCheck.checked){
            document.getElementById("register").disabled = false;
        } else {
            document.getElementById("register").disabled = true;
        }
    }
</script>
<style>
    #register:disabled,
    #register[disabled]{
        border: 1px solid #999999;
        background-color: #666666;
        color: white;
    }
</style>
<section class="row columns form">
    <h2>Please register with Annotare</h2>
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
        <div>
            <input type="checkbox" name="policyAccept" id="acceptPolicy" onchange="document.getElementById('register').disabled = !this.checked;"/>
            By signing up you agree to our <a target="_blank" href="https://www.ebi.ac.uk/data-protection/privacy-notice/annotare">Privacy Policy</a> and <a target="_blank" href="https://www.ebi.ac.uk/about/terms-of-use">Terms of Use</a>.
        </div>
        <div class="btn-row">
            <div>Already registered? Please <a href="${pageContext.request.contextPath}/login/">log in</a>.</div>
            <div class="right"><input type="submit" class="submit" name="signup" value="Register" id="register" disabled /></div>
        </div>
    </form>
</section>