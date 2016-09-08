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
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
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
<t:analytics></t:analytics>
<script>
    function fixEmail() {
        var email_elt = document.forms["login"]["email"];
        if (undefined != email_elt.value && email_elt.value.length > 0 && -1 == email_elt.value.indexOf("@")) {
            email_elt.value = email_elt.value + "@ebi.ac.uk";
        }
    }
</script>
<section class="grid_16">
    <div class='tableauPlaceholder' id='viz1471872169308' style='position: relative'>
        <noscript><a href='http:&#47;&#47;localhost:8090&#47;annotare&#47;login&#47;'><img
                alt='Annotare Submitter Satisfaction per Country '
                src='http:&#47;&#47;public.tableau.com&#47;static&#47;images&#47;59&#47;5979S2Y5M&#47;1_rss.png'
                style='border: none'/></a></noscript>
        <object class='tableauViz' style='display:none;'>
            <param name='host_url' value='http%3A%2F%2Fpublic.tableau.com%2F'/>
            <param name='path' value='shared&#47;5979S2Y5M'/>
            <param name='toolbar' value='yes'/>
            <param name='static_image'
                   value='http:&#47;&#47;public.tableau.com&#47;static&#47;images&#47;59&#47;5979S2Y5M&#47;1.png'/>
            <param name='animate_transition' value='yes'/>
            <param name='display_static_image' value='yes'/>
            <param name='display_spinner' value='yes'/>
            <param name='display_overlay' value='yes'/>
            <param name='display_count' value='yes'/>
        </object>
    </div>
    <script type='text/javascript'>                    var divElement = document.getElementById('viz1471872169308');
    var vizElement = divElement.getElementsByTagName('object')[0];
    vizElement.style.width = '804px';
    vizElement.style.height = '669px';
    var scriptElement = document.createElement('script');
    scriptElement.src = 'https://public.tableau.com/javascripts/api/viz_v1.js';
    vizElement.parentNode.insertBefore(scriptElement, vizElement);                </script>
</section>
<section class="grid_7 form">
    <div id="login-box">
        <h2>Please log in to Annotare</h2>
        <form name="login" method="POST">
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
                        <input type="text" id="email" name="email" value="${email}" onblur="fixEmail()"/>
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
                <div class="right"><input type="submit" name="signIn" value="Log in" class="submit"/></div>
            </div>
            <div class="last-row">Don't have an account? Please <a href="${pageContext.request.contextPath}/sign-up/">register</a>.
            </div>

        </form>
    </div>
    <div id="twitter-box">
        <a class="twitter-timeline" data-height="375" data-chrome="nofooter noborders transparent" data-dnt="true" data-theme="light" data-link-color="#6C8CD5"
           href="https://twitter.com/ArrayExpressEBI">Tweets by ArrayExpressEBI</a>
        <script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>
    </div>
</section>