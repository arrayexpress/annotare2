package uk.ac.ebi.fg.annotare.proto.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            params.put("Result", "ARRAY");
            params.put("Limit", 100);
            params.put("UserID", 1);
            params.put("StateType", Arrays.asList(new String[]{"open"}));
            SOAPMessage response = otrs.dispatchCall("TicketObject", "TicketSearch", params);
            Object[] result = new OtrsSoapMessageParser().nodesToArray(response);
            for (Object item : result) {
                log.info("{}", item);
            }
        } catch (Throwable x) {
            log.error("Something went wrong", x);
        }
    }
}
