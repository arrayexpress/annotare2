package uk.ac.ebi.fg.annotare2.integration;

public interface RtProperties {

    boolean isRtIntegrationEnabled();

    String getRtIntegrationUrl();

    String getRtIntegrationUser();

    String getRtIntegrationPassword();

    String getRtIntegrationSubjectTemplate();

    String getRtIntegrationBodyTemplate();

    String getRtQueueName();
}
