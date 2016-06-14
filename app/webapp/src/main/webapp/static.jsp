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
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress"%>
<%
    String pageName = request.getParameter("pageName");
    String pageTitle = "";

    if (pageName.equals("about.html")) {
        pageContext.setAttribute("aboutClass", " active");
        pageTitle = "About Annotare";
    } else if (pageName.equals("login.jsp")) {
        pageContext.setAttribute("loginClass", " active");
        pageTitle = "Login";
    } else if (pageName.equals("sign-up.jsp")) {
        pageContext.setAttribute("signUpClass", " active");
        pageTitle = "Register";
    } else if ("help/getting_started.html".equals(pageName)) {
        pageTitle = "Getting Started";
    } else if ("help/accepted_processed_ma_file_formats.html".equals(pageName)) {
        pageTitle = "Accepted Processed Microarray Files Formats";
    } else if ("help/accepted_raw_ma_file_formats.html".equals(pageName)) {
        pageTitle = "Accepted Raw Microarray Files Formats";
    } else if ("help/describe_exp.html".equals(pageName)) {
        pageTitle = "Describe Experiment";
    } else if ("help/file_upload.html".equals(pageName)) {
        pageTitle = "Upload Files and Assign to Samples";
    } else if ("help/sample_attributes.html".equals(pageName)) {
        pageTitle = "Sample Attributes";
    } else if ("help/seq_lib_spec.html".equals(pageName)) {
        pageTitle = "Sequencing Library Information";
    } else if ("help/strict_mtab_matrix.html".equals(pageName)) {
        pageTitle = "Strict MAGE-TAB Format for Matrix Data";
    } else if ("help/submit_exp.html".equals(pageName)) {
        pageTitle = "Submit Experiment";
    } else if ("help/time_saving_features.html".equals(pageName)) {
        pageTitle = "Time Saving Features";
    } else if ("help/two_color_ma.html".equals(pageName)) {
        pageTitle = "Two-colour Microarrays";
    } else if ("help/validate_exp.html".equals(pageName)) {
        pageTitle = "Validate Experiment";
    } else if ("help/experiment_types.html".equals(pageName)) {
        pageTitle = "Experiment Types";
    }
    if (!pageTitle.isEmpty()) {
        pageTitle = pageTitle + " < ";
    }

    if (pageName.startsWith("help/")) {
        pageContext.setAttribute("helpClass", " class=\"active\"");
        pageTitle = pageTitle + "Guide < ";
    }

    pageContext.setAttribute("pageTitle", pageTitle);
%>
<compress:html enabled="true" removeComments="true" compressCss="true" compressJavaScript="false" yuiJsDisableOptimizations="true">
    <t:frontierpage>
        <jsp:attribute name="title">${pageTitle}Annotare &lt; EMBL-EBI</jsp:attribute>
        <jsp:attribute name="localnav">
            <ul class="grid_24" id="local-nav">
                <li class="first"><a href="${pageContext.request.contextPath}/" title="Annotare ${project.version} rev.${buildNumber}">Home</a></li>
                <li${helpClass}><a href="${pageContext.request.contextPath}/help">Help</a></li>
                <li class="last${aboutClass}"><a href="${pageContext.request.contextPath}/about">About Annotare</a></li>
                <!-- If you need to include functional (as opposed to purely navigational) links in your local menu,
                     add them here, and give them a class of "functional". Remember: you'll need a class of "last" for
                     whichever one will show up last...
                     For example: -->
                <c:choose>
                    <c:when test="${sessionScope.loggedin != null}">
                        <li class="functional last"><a href="${pageContext.request.contextPath}/logout/" class="icon icon-functional login" data-icon="l">Logout</a></li>
                        <li class="functional"><a href="${pageContext.request.contextPath}/account/" class="icon icon-generic account" data-icon="M">${sessionScope.email}</a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="functional last${loginClass}"><a href="${pageContext.request.contextPath}/login/" class="icon icon-functional login" data-icon="l">Login</a></li>
                        <li class="functional${signUpClass}"><a href="${pageContext.request.contextPath}/sign-up/" class="icon icon-functional register" data-icon="7">Register</a></li>
                    </c:otherwise>
                </c:choose>
                    <%--<li class="functional"><a href="#" class="icon icon-generic feedback" data-icon="\">Feedback</a></li>--%>
            </ul>

        </jsp:attribute>
        <jsp:body>
            <jsp:include page="/assets/pages/${param.pageName}"/>
        </jsp:body>
    </t:frontierpage>
</compress:html>