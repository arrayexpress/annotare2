package uk.ac.ebi.fg.annotare.prototype.upload.resumable.server.rpc;


import com.google.gwt.core.server.StackTraceDeobfuscator;
import com.google.gwt.logging.server.RemoteLoggingServiceUtil;
import com.google.gwt.logging.shared.RemoteLoggingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.google.gwt.user.client.rpc.RpcRequestBuilder.MODULE_BASE_HEADER;

public class RemoteLoggingServiceImpl extends RemoteServiceServlet implements RemoteLoggingService {

    private final Map<String, StackTraceDeobfuscator> deobfuscators = new HashMap<>();

    private final Logger log = Logger.getLogger("RemoteLoggingService");

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
        } catch (RemoteLoggingServiceUtil.RemoteLoggingException e) {
            log.log(Level.SEVERE, "Remote logging failed", e);
            return "Remote logging failed, check stack trace for details.";
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Remote logging failed", e);
            return "Remote logging failed, check stack trace for details.";
        }
        return null;
    }

    private void logOnServer(LogRecord lr, String strongName, StackTraceDeobfuscator deobfuscator, String loggerNameOverride) throws RemoteLoggingServiceUtil.RemoteLoggingException {
        if (deobfuscator != null) {
            lr = deobfuscateLogRecord(deobfuscator, lr, strongName);
        }

        String loggerName = (null == loggerNameOverride) ? lr.getLoggerName() : loggerNameOverride;
        Logger log = Logger.getLogger(loggerName);
        log.log(lr);
    }

    private LogRecord deobfuscateLogRecord(StackTraceDeobfuscator deobfuscator, LogRecord lr, String strongName) {
        if (lr.getThrown() != null && strongName != null) {
            deobfuscator.deobfuscateStackTrace(lr.getThrown(), strongName);
        }

        return lr;
    }

    protected String getRequestModuleBasePath() {
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