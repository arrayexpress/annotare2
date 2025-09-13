package uk.ac.ebi.fg.annotare2.web.server.servlets;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.fg.annotare2.core.properties.AnnotareProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

public class GlobusProxyServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(GlobusProxyServlet.class);

    private String GlobusTransferServiceBaseUrl;

    @Inject
    public GlobusProxyServlet(AnnotareProperties annotareProperties) {
        this.GlobusTransferServiceBaseUrl = annotareProperties.getGlobusTransferAPIURL();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        proxyRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        proxyRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        proxyRequest(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        proxyRequest(request, response);
    }

    private void proxyRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String targetPath = (pathInfo != null) ? pathInfo : "";
        String queryString = request.getQueryString();
        String GlobusTransferServiceUrl = GlobusTransferServiceBaseUrl + targetPath; // Use the injected property
        if (queryString != null && !queryString.isEmpty()) {
            GlobusTransferServiceUrl += "?" + queryString;
        }
        URL url = new URL(GlobusTransferServiceUrl);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.getMethod());
            connection.setUseCaches(false);
            connection.setDoInput(true); // Always read response
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Avoid problematic headers that might be set by the servlet container or are specific to the client-proxy connection
                if (!"Host".equalsIgnoreCase(headerName) &&
                        !"Connection".equalsIgnoreCase(headerName) &&
                        !"Content-Length".equalsIgnoreCase(headerName) && // Content-Length will be set automatically if output stream is used
                        !"Transfer-Encoding".equalsIgnoreCase(headerName)) {
                    connection.setRequestProperty(headerName, request.getHeader(headerName));
                }
            }

            // Handle request body for POST/PUT requests
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                connection.setDoOutput(true); // Allow sending a body
                try (InputStream clientInputStream = request.getInputStream();
                     OutputStream proxyOutputStream = connection.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = clientInputStream.read(buffer)) != -1) {
                        proxyOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            int responseCode = connection.getResponseCode();
            response.setStatus(responseCode);

            // Copy response headers from internal service to client
            // Preserve Content-Encoding and Content-Length so the client can decode compressed content correctly.
            for (String headerKey : connection.getHeaderFields().keySet()) {
                if (headerKey != null &&
                        !"Transfer-Encoding".equalsIgnoreCase(headerKey)) { // Hop-by-hop; let the container decide framing
                    for (String headerValue : connection.getHeaderFields().get(headerKey)) {
                        response.addHeader(headerKey, headerValue);
                    }
                }
            }

            // Copy response body from internal service to client
            try (InputStream proxyInputStream = (responseCode >= 200 && responseCode < 400) ? connection.getInputStream() : connection.getErrorStream();
                 OutputStream clientOutputStream = response.getOutputStream()) {
                if (proxyInputStream != null) { // Ensure there's a stream to read from
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = proxyInputStream.read(buffer)) != -1) {
                        clientOutputStream.write(buffer, 0, bytesRead);
                    }
                    clientOutputStream.flush();
                }
            }
        } catch (Exception e) {
            log.error("Error proxying request: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error proxying request.");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
