package dev.aks8m.vanir.yggdrasil.capture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CaptureManager {

    private static final Logger LOG = LoggerFactory.getLogger(CaptureManager.class);

    private final Path mediaDirectory;
    private final Map<String, Capture> captureMap;

    public CaptureManager(Path mediaDirectory) {
        this.captureMap = new HashMap<>();
        this.mediaDirectory = mediaDirectory;
    }

    public void createCapture(String sessionId) {
        captureMap.put(sessionId, new Capture());
    }

    public void manageFileName(String sessionId, String mediaFile) {
        final Capture capture = getCapture(sessionId);
        if (capture.getState() == State.INITIALIZING) {
            try {
                Path media = mediaDirectory.resolve(mediaFile);
                capture.init(media);
            } catch (FileNotFoundException e) {
                manageError(sessionId);
                throw new RuntimeException(e);
            }
        } else {
            manageError(sessionId);
            LOG.error("Capture not in correct state (INITIALIZING) to handle file name message");
        }
    }

    public void manageData(String sessionId, byte[] data) {
        final Capture capture = getCapture(sessionId);
        if (capture.getState() == State.READY || capture.getState() == State.CAPTURING) {
            try {
                capture.capture(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            manageError(sessionId);
            LOG.error("Capture not in correct state (READY or CAPTURING) to handle capturing data");
        }
    }

    public void manageEndOfData(String sessionId) {
        final Capture capture = getCapture(sessionId);
        if (capture.getState() == State.CAPTURING) {
            try {
                capture.complete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            manageError(sessionId);
            LOG.error("Capture not in correct state (CAPTURING) to handle completing data capture");
        }
    }

    public void closeCapture(String sessionId) {
        final Capture capture = getCapture(sessionId);
        if (capture.getState() == State.COMPLETED) {
            capture.close();
        } else {
            manageError(sessionId);
            LOG.error("Capture not in correct state (COMPLETE) to handle closing data capture");
        }
    }

    public void manageError(String sessionId) {
        Capture capture = getCapture(sessionId);
        try {
            capture.error();
        } catch (IOException e) {
            LOG.error("Error trying to perform error for CaptureManager", e);
            throw new RuntimeException(e);
        }
    }

    private Capture getCapture(String sessionId) {
        if (!captureMap.containsKey(sessionId)) {
            throw new RuntimeException("Capture Id " + sessionId + " not found in Capture Manager");
        }
        return captureMap.get(sessionId);
    }
}
