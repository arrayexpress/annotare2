<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
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
            <jsp:include page="/static/pages/${param.pageName}"/>
        </jsp:body>
    </t:frontierpage>
</compress:html>
