package uk.ac.ebi.fg.annotare2.integration;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtrsMessengerService {
    private static final Logger log = LoggerFactory.getLogger(OtrsMessengerService.class);

    private final ExtendedAnnotareProperties properties;

    @Inject
    public OtrsMessengerService(ExtendedAnnotareProperties properties) {
        this.properties = properties;
    }



    private void send(String recipients, String hiddenRecipients, String subject, String message, String from) {

    }
}
