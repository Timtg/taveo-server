package no.timesaver.domain;


public class StoreSettings {

    private Long storeId;
    private Boolean manualTimeVerification;
    private Boolean automaticPrintDialog;

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Boolean getManualTimeVerification() {
        return manualTimeVerification;
    }

    public void setManualTimeVerification(Boolean manualTimeVerification) {
        this.manualTimeVerification = manualTimeVerification;
    }

    public Boolean getAutomaticPrintDialog() {
        return automaticPrintDialog;
    }

    public void setAutomaticPrintDialog(Boolean automaticPrintDialog) {
        this.automaticPrintDialog = automaticPrintDialog;
    }
}
