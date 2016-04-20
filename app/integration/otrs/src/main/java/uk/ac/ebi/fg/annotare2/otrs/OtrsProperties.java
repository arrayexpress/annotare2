package uk.ac.ebi.fg.annotare2.otrs;

public interface OtrsProperties {

    boolean isOtrsIntegrationEnabled();

    String getOtrsIntegrationUrl();

    String getOtrsIntegrationUser();

    String getOtrsIntegrationPassword();
}
