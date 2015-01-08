/*
 * Copyright 2009-2015 European Molecular Biology Laboratory
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
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.gwt.user.client.rpc.RpcRequestBuilder.MODULE_BASE_HEADER;

public class RemoteLoggingServiceImpl extends RemoteServiceServlet implements RemoteLoggingService {

    private static Logger logger = LoggerFactory.getLogger(RemoteServiceServlet.class);

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

            logOnServer(
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

    private void logOnServer(LogRecord lr, String strongName, StackTraceDeobfuscator deobfuscator, String loggerNameOverride) throws RemoteLoggingServiceUtil.RemoteLoggingException {
        if (deobfuscator != null) {
            lr = deobfuscateLogRecord(deobfuscator, lr, strongName);
        }

        String loggerName = (null == loggerNameOverride) ? lr.getLoggerName() : loggerNameOverride;
        Logger logger = LoggerFactory.getLogger(loggerName);
        int julLevelValue = lr.getLevel().intValue();
        if (julLevelValue <= Level.FINEST.intValue()) {
            logger.trace(lr.getMessage(), lr.getThrown());
        } else if (julLevelValue <= Level.FINE.intValue()) {
            logger.debug(lr.getMessage(), lr.getThrown());
        } else if (julLevelValue <= Level.INFO.intValue()) {
            logger.info(lr.getMessage(), lr.getThrown());
        } else if (julLevelValue <= Level.WARNING.intValue()) {
            logger.warn(lr.getMessage(), lr.getThrown());
        } else {
            logger.error(lr.getMessage(), lr.getThrown());
        }
    }

    private LogRecord deobfuscateLogRecord(StackTraceDeobfuscator deobfuscator, LogRecord lr, String strongName) {
        if (lr.getThrown() != null && strongName != null) {
            deobfuscator.deobfuscateStackTrace(lr.getThrown(), strongName);
        }

        return lr;
    }

    private String getRequestModuleBasePath() {
        try {
            String header = getThreadLocalRequest().getHeader(MODULE_BASE_HEADER);
            if (header == null) {
                return null;
            }
            String path = new URL(header).getPath();
            String contextPath = getThreadLocalRequest().getContextPath();
            if (!path.startsWith(contextPath)) {
                return null;
            }
            return path.substring(contextPath.length());
        } catch (MalformedURLException e) {
            return null;
        }
    }
}