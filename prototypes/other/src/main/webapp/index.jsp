<!DOCTYPE html>
<html>
<body>
<ul> All prototypes
    <%
        String query = request.getQueryString();
        if (query != null) {
            query =  "?" + query.replaceAll("(^.*\\?)", "");
        } else {
            query = "";
        }
        pageContext.setAttribute("query", query);
    %>
    <li><a href="layout.jsp<%=pageContext.getAttribute("query")%>">Layout</a></li>
    <li><a href="editor.jsp<%=pageContext.getAttribute("query")%>">Editor</a></li>
    <li>etc...</li>
</ul>
</body>
</html>