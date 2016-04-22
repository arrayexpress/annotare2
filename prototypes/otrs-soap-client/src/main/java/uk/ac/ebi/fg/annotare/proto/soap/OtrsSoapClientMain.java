package uk.ac.ebi.fg.annotare.proto.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OtrsSoapClientMain {

    private final static Logger log = LoggerFactory.getLogger(OtrsSoapClientMain.class);

    public static void main(String[] args) {
        log.info("So, what is going on?");
        try {
            OtrsConnector otrs = new OtrsConnector(
                    "http://www.ebi.ac.uk/microarray-srv/otrs/rpc.pl",
                    "otrsrpc",
                    "xMXSySEYAU"
            );

            Map<String, Object> params = new HashMap<>();
            params.put("TypeID", 1);
            params.put("QueueID", 62);
            params.put("LockID", 1);
            params.put("Title", "Annotare HTS submission #12345");
            params.put("OwnerID", 1);
            params.put("UserID", 1);
            params.put("PriorityID", 3);
            params.put("State", "new");
            params.put("CustomerID", "kolais@ebi.ac.uk");
            params.put("CustomerUser", "kolais@ebi.ac.uk");

            //SOAPMessage response = otrs.dispatchCall("TicketObject", "TicketCreate", params);
            //Object[] result = new OtrsSoapMessageParser().nodesToArray(response);
            //if (null != result && result.length > 0) {
                //log.info("Created ticket {}", result[0]);
                int ticketId = 87043; // (Integer)result[0];
/*
"TicketObject",   "ArticleCreate",
		"TicketID",       $TicketID,
		"ArticleType",    "webrequest",
		"SenderType",     "customer",
		"HistoryType",    "WebRequestCustomer",
		"HistoryComment", "created from PHP",
		"From",           $email,
		"Subject",        $title,
		"ContentType",    "text/plain; charset=ISO-8859-1",
		"Body",           $description,
		"UserID",         1,
		"Loop",           0,
		"AutoResponseType", 'auto reply',
		"OrigHeader", array(
			'From' => $email,
			'To' => $from,
			'Subject' => $title,
			'Body' => $description
		),
 */
//                params.clear();
//                params.put("TicketID", ticketId);
//                params.put("ArticleType", "email-internal");
//                params.put("SenderType", "customer");
//                params.put("HistoryType", "EmailCustomer");
//                params.put("HistoryComment", "Sent from Annotare submission #12345");
//                params.put("From", "\"Nikolay Kolesnikov\" <kolais@ebi.ac.uk>");
//                params.put("To", "\"Annotare\" <annotare@ebi.ac.uk>");
//                params.put("Subject", "Submission comment");
//                params.put("Type", "text/plain");
//                params.put("Charset", "UTF-8");
//                params.put("Body", "Hello, world!\n1\n2\n3\nRegards, Nikolay.");
//                params.put("UserID", 1);
//                params.put("Loop", 0);
//                params.put("AutoResponseType", "auto reply");
//
//                SOAPMessage response = otrs.dispatchCall("TicketObject", "ArticleSend", params);
//                Object[] result = new OtrsSoapMessageParser().nodesToArray(response);
//                if (null != result && result.length > 0) {
//                    log.info("Created article {}", result[0]);
//                }

                params.clear();
                params.put("TicketID", ticketId);
                params.put("ArticleType", "email-external");
                params.put("SenderType", "agent");
                params.put("HistoryType", "FollowUp");
                params.put("HistoryComment", "Sent from Annotare submission #12345");
                params.put("To", "\"Nikolay Kolesnikov\" <kolais@ebi.ac.uk>");
                params.put("From", "\"Annotare\" <annotare@ebi.ac.uk>");
                params.put("Subject", "Submission update");
                params.put("Type", "text/plain");
                params.put("Charset", "UTF-8");
                params.put("Body", "Nikolay,\n\nYour submission rules, please submit more.\n\nKind regards, Annotare.");
                params.put("UserID", 1);
                params.put("AutoResponseType", "auto follow up");

                SOAPMessage  response = otrs.dispatchCall("TicketObject", "ArticleSend", params);
                Object[] result = new OtrsSoapMessageParser().nodesToArray(response);
                if (null != result && result.length > 0) {
                    log.info("Sent article {}", result[0]);
                }
            //}
        } catch (Throwable x) {
            log.error("Something went wrong", x);
        }
    }
}
