<!DOCTYPE html>
<html>
<body>
<ul> All prototypes
    <li><a href="layout.html?<%=request.getQueryString().replaceAll("(^.*\\?)","")%>">Layout</a></li>
    <li><a href="editor.html?<%=request.getQueryString().replaceAll("(^.*\\?)","")%>">Editor</a></li>
    <li>etc...</li>
</ul>
</body>
</html>