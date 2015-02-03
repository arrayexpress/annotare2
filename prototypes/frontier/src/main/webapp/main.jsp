<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:frontierpage>
    <jsp:attribute name="extracss">
    </jsp:attribute>
    <jsp:attribute name="extrajs">
        <script type="text/javascript" src="${pageContext.request.contextPath}/frontier.gwt/frontier.gwt.nocache.js"></script>
    </jsp:attribute>
    <jsp:body/>
</t:frontierpage>
