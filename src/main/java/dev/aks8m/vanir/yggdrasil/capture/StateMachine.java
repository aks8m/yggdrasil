package dev.aks8m.vanir.yggdrasil.capture;

public class StateMachine {

    public State next(Capture capture) {
        for (int i = 0; i < State.values().length - 1; i++) {
            if (capture.getState() == State.values()[i]) {
                return State.values()[i + 1];
            }
        }
        return State.CLOSED;
    }

    public State previous(Capture capture) {
        for (int i = State.values().length - 1; i > 0; i--) {
            if (capture.getState() == State.values()[i]) {
                return State.values()[i - 1];
            }
        }
        return State.READY;
    }

    public boolean interrogate(Capture capture) {
        return true;
    }
}
