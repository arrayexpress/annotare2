/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package uk.ac.ebi.fg.annotare2.web.server.filter;

import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;
import com.googlecode.htmlcompressor.compressor.YuiCssCompressor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(
        filterName = "CompressResponseFilter",
        urlPatterns = { "*.css", "*.js" }
)
public class CompressResponseFilter implements Filter {

    private YuiCssCompressor cssCompressor;
    private ClosureJavaScriptCompressor jsCompressor;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {

        CharResponseWrapper responseWrapper = new CharResponseWrapper(
                (HttpServletResponse) resp);

        chain.doFilter(req, responseWrapper);

        String compressed;
        String contentType = resp.getContentType();
        if (null != contentType && contentType.contains("/css")) {
            compressed = cssCompressor.compress(responseWrapper.toString());
        } else if (null != contentType && resp.getContentType().contains("/javascript")) {
            compressed = jsCompressor.compress(responseWrapper.toString());
        } else {
            compressed = responseWrapper.toString();
        }
        resp.getWriter().write(compressed);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        cssCompressor = new YuiCssCompressor();
        jsCompressor = new ClosureJavaScriptCompressor();
    }

    @Override
    public void destroy() {
    }

}