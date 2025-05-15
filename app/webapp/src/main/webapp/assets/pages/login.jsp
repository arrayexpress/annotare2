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
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="f" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@page isELIgnored="false" %>
<%@page import="uk.ac.ebi.fg.annotare2.web.server.servlets.utils.ValidationErrors" %>
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
        email = (String) session.getAttribute("email");
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
<section id="homepage-left-section" class="column medium-8" style="padding-top:31px;padding-left: 26px;">
    <div class="row">
        <h4>Welcome to Annotare</h4>
        <p>Annotare is a tool for submitting functional genomics data to the
            <a href="https://www.ebi.ac.uk/biostudies/arrayexpress" target="_blank">ArrayExpress collection in BioStudies</a>.
            It generates
            <a href="http://www.fged.org/projects/mage-tab/">MAGE-TAB</a> format and supports high metadata standards in
            compliance with <a href="http://www.fged.org/projects/miame/">MIAME</a>/<a href="http://www.fged.org/projects/minseqe/">MINSEQE</a>/<a href="https://doi.org/10.1038/s41587-020-00744-z">MINSCE</a> guidelines.</p>
    </div>
    <div class="row">
        <h4>Overview of the submission process</h4>
        <p style="border: 1px solid #d7d7d7;">
            <img src="../assets/images/Submission_overview_BioStudies.png" alt="submission_overview"/>
        </p>
    </div>
    <div class="row">
        <h4>Start here</h4>
        <div id="further_info" class="row columns">
            <div class="column medium-4">
                <p>I am here for the <b>first time</b> or have a <b>new data type</b> to submit</p>
                <p><a href="${pageContext.request.contextPath}/help" target="_blank" class="readmore">Submission
                    Guide</a></p>
            </div>
            <div class="column medium-4">
                <p>I want to <b>change the release date</b> or <b>add publication details</b></p>
                <p><a href="https://www.ebi.ac.uk/fg/annotare/help/after_sub_modify_data.html" target="_blank" class="readmore">Read more</a>
                </p>
            </div>
            <div class="column medium-4">
                <p>I want to <b>add or delete data</b> from my experiment</p>
                <p><a href="${pageContext.request.contextPath}/help/after_sub_modify_data.html" target="_blank"
                      class="readmore">Read more</a></p>
            </div>
        </div>
    </div>
    <div class="row" style="margin-top: 0.75rem;">
        <h4>Questions?</h4>
        <p>
            <a href="${pageContext.request.contextPath}/help" target="_blank">See full submission guide</a> or
            <a href="https://www.youtube.com/watch?v=ANr2PTVy7JA"
               target="_blank">watch the video tutorial</a>
        </p>
    </div>
</section>
<section class="column medium-4 form">
        <div id="login-box">
            <h2>Please log in to Annotare</h2>
            <form id = "form-login" name="login" method="POST">
                <div>
                    <c:out value="${sessionScope.info}"/>
                    <c:remove var="info" scope="session"/>
                </div>
                <div class="error">${dummyErrors}</div>
                <div>
                    <label for="email">Email address</label>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${email != ''}">
                            <input type="text" id="email" name="email" value="${f:escapeXml(email)}"
                                   onblur="fixEmail()"/>
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="email" name="email" autofocus="autofocus" onblur="fixEmail()"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="error">${emailErrors}</div>
                <div><label for="password">Password</label></div>
                <div>
                    <c:choose>
                        <c:when test="${email != ''}">
                            <input type="password" name="password" id="password" autofocus="autofocus"/>
                        </c:when>
                        <c:otherwise>
                            <input type="password" name="password" id="password"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="error">${passwordErrors}</div>
                <div class="btn-row">
                    <div><a href="${pageContext.request.contextPath}/change-password/">Forgot your password?</a></div>
                    <div class="right">
                        <input type="submit" name="signIn" value="Log in" class="button secondary float-right"/>
                    </div>
                </div>
                <div class="last-row">Don't have an account? Please <a
                        href="${pageContext.request.contextPath}/sign-up/">register</a>.
                </div>

            </form>
    </div>
        <div id="twitter-box">
            <a class="twitter-timeline" data-height="375" data-chrome="nofooter noborders transparent" data-dnt="true"
               data-theme="light" data-link-color="#6C8CD5"
               href="https://twitter.com/ArrayExpressEBI">Tweets by ArrayExpressEBI</a>
            <script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>
        </div>
</section>