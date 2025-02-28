package dev.aks8m.vanir.yggdrasil.message;

public class CompleteCapturePayload {
    private String status;
    private long captureSize;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCaptureSize() {
        return captureSize;
    }

    public void setCaptureSize(long captureSize) {
        this.captureSize = captureSize;
    }
}
