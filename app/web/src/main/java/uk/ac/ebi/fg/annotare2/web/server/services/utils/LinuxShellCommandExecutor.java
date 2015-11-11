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

package uk.ac.ebi.fg.annotare2.web.server.services.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LinuxShellCommandExecutor {

    private final static String EOL = System.getProperty("line.separator");

    private static final Logger log = LoggerFactory.getLogger(LinuxShellCommandExecutor.class);

    private String output;
    private String errors;

    public LinuxShellCommandExecutor() {
        output = "";
        errors = "";
    }

    public boolean execute(String command) throws IOException {
        log.debug("Executing {}", command);

        List<String> commandParams = new ArrayList<>();
        commandParams.add("/bin/sh");
        commandParams.add("-c");
        commandParams.add(command);

        try {
            ProcessBuilder pb = new ProcessBuilder(commandParams);
            Map<String, String> env = pb.environment();
            env.put("LC_ALL", "en_US.UTF-8");
            env.put("LANG", "en_US.UTF-8");
            env.put("LANGUAGE", "en_US.UTF-8");

            Process process = pb.start();

            InputStream stdOut = process.getInputStream();
            InputStream stdErr = process.getErrorStream();

            output = streamToString(stdOut, "UTF-8");
            errors = streamToString(stdErr, "UTF-8");

            return 0 == process.waitFor();
        } catch (InterruptedException x) {
            throw new IOException(x);
        }
    }

    public String getOutput() {
        return output;
    }

    public String getErrors() {
        return errors;
    }

    private String streamToString(InputStream is, String encoding) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(EOL);
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
