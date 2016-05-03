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
<%@page contentType="text/html; charset=UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress"%>
<compress:html enabled="true" removeComments="true" compressCss="true" compressJavaScript="false" yuiJsDisableOptimizations="true">
    <t:frontierpage>
        <jsp:attribute name="extradeferjs">
        </jsp:attribute>
        <jsp:body>
            <jsp:include page="/assets/pages/${param.pageName}"/>
        </jsp:body>
    </t:frontierpage>
</compress:html>