/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.logging.server.RemoteLoggingServiceUtil;
import com.google.gwt.logging.server.RemoteLoggingServiceUtil.RemoteLoggingException;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.logging.LogRecord;

import static com.google.common.collect.Maps.newHashMap;

public class RemoteLoggingServiceImpl extends RemoteServiceServlet implements RemoteLoggingService {

    private static Logger logger = LoggerFactory.getLogger(RemoteServiceServlet.class.getName());

    private final Map<String, StackTraceDeobfuscator> deobfuscators = newHashMap();
    public final String logOnServer(LogRecord lr) {

        String modulePath = getRequestModuleBasePath();
        String strongName = getPermutationStrongName();
        try {

            if (!deobfuscators.containsKey(modulePath)) {
                deobfuscators.put(
                        modulePath,
                        StackTraceDeobfuscator.fromUrl(
                                getServletContext().getResource("/WEB-INF/deploy" + modulePath + "symbolMaps/")
                        )
                );
            }

            RemoteLoggingServiceUtil.logOnServer(
                    lr, strongName, deobfuscators.get(modulePath), null);
        } catch (RemoteLoggingException e) {
            logger.error("Remote logging failed", e);
            return "Remote logging failed, check stack trace for details.";
        } catch (MalformedURLException e) {
            logger.error("Remote logging failed", e);
            return "Remote logging failed, check stack trace for details.";
        }
        return null;
    }
}