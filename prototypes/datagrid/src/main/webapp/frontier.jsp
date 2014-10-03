<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:page>
    <jsp:body>
        <!-- If you require a breadcrumb trail, its root should be your service.
        You don't need a breadcrumb trail on the homepage of your service... -->
        <nav id="breadcrumb">
            <p>
                <a href="[service-url]">[service-name]</a> &gt;
                [page-title]
            </p>
        </nav>

        <!-- Example layout containers -->

        <section>
            <h2>[page-title]</h2>
            <p>Your content</p>
        </section>

        <section>
            <h3>Level 3 heading</h3>
            <p>More content in a full-width container.</p>

            <h4>Level 4 heading</h4>
            <p>More content in a full-width container.</p>
        </section>
        <!-- End example layout containers -->
    </jsp:body>
</t:page>