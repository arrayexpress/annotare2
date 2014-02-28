package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;



abstract class ErrorReportingRemoteServiceServlet extends RemoteServiceServlet {

    private final EmailSender email;

    public ErrorReportingRemoteServiceServlet(EmailSender emailSender) {
        this.email = emailSender;
    }

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        email.sendException("Unexpected exception in RPC call", e);
        super.doUnexpectedFailure(e);
    }
}