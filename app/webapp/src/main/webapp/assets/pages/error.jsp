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
<%@page import="com.google.inject.Injector"%>
<%@page import="uk.ac.ebi.fg.annotare2.core.components.Messenger"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:analytics></t:analytics>
<h2 class="alert">Oops, there is a problem with Annotare</h2>
<p>This problem means that the service you are trying to access is currently unavailable. We'll bring back the service as soon as possible. We’re very sorry.</p>
<h3>Still confused?</h3>
<p>If you require further assistance locating missing page or file, please <a href="mailto:annotare@ebi.ac.uk">contact
    us</a> and we will look into it for you.</p>

<%
    ServletContext sc = request.getServletContext();
    Injector injector = (Injector) sc.getAttribute(Injector.class.getName());
    Messenger messenger = injector.getInstance(Messenger.class);

    Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String message = (String) request.getAttribute("javax.servlet.error.message");
    String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

    String note = "Error" + (null != requestUri ? " while accessing " + requestUri : "")
            + (null != message ? ": " + message : "")
            + (null != statusCode ? " [HTTP " + String.valueOf(statusCode) + "]" : "");
    messenger.send(note, throwable);
%>