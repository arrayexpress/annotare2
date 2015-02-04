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
<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" isErrorPage="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress"%>
<compress:html enabled="true" removeComments="true" compressCss="true" compressJavaScript="true" yuiJsDisableOptimizations="true">
    <t:frontierpage>
        <jsp:attribute name="extracss">
        </jsp:attribute>
        <jsp:attribute name="extrajs">
            <script type="text/javascript" src="${pageContext.request.contextPath}/frontier.gwt/frontier.gwt.nocache.js"></script>
        </jsp:attribute>
        <jsp:body>
            <section>
                <h2 class="alert">We’re sorry that the page or file you’ve requested is not available</h2>
                <p>The resource located at <span class="alert">${requestScope.originalRequestUri}</span> may have been removed, had its name changed, or has restricted access.</p>
                <h3>Still confused?</h3>
                <p>If you require further assistance locating missing page or file, please <a href="#" class="feedback">contact us</a> and we will look into it for you.</p>
            </section>
        </jsp:body>
    </t:frontierpage>
</compress:html>
