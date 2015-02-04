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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class CharResponseWrapper extends HttpServletResponseWrapper {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private final CharArrayWriter output;

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new CharArrayWriter();
    }

    @Override
    public String toString() {
        return output.toString();
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(output);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new OutputStreamToWriter(output);
    }

    @Override
    public void setContentLength(int contentLength) {
        // do just nothing
    }

    public static class OutputStreamToWriter extends ServletOutputStream {

        private final Writer writer;

        public OutputStreamToWriter(Writer writer) {
            this.writer = writer;
        }

        public void close() throws IOException {
            writer.close();
        }

        public void flush() throws IOException {
            writer.flush();
        }

        public void write(int b) throws IOException {
            writer.write(b);
        }
    }
}
