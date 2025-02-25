package dev.aks8m.vanir.yggdrasil.message;

public class CompleteCapturePayload {
    private String status;
    private long dataSize;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }
}
