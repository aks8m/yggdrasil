package dev.aks8m.vanir.yggdrasil.message;

public class WebSocketMessage {
    private String event;
    private Object payload;

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
