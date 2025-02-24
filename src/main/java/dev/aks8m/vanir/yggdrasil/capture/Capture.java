package dev.aks8m.vanir.yggdrasil.capture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

public class Capture {

    Logger LOG = LoggerFactory.getLogger(Capture.class);

    private Path media;
    private BufferedOutputStream bufferedOutputStream;
    private State state;
    private Instant startTime;
    private Instant endTime;
    private long mediaSize;

    public Capture() {
        this.state = State.INITIALIZING;
    }

    public State getState() {
        return state;
    }

    public void init(Path media) throws FileNotFoundException {
        assert state == State.INITIALIZING;
        this.media = media;
        FileOutputStream fileOutputStream = new FileOutputStream(media.toFile());
        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        state = State.READY;
    }

    public void capture(byte[] data) throws IOException {
        assert state == State.READY || state == State.CAPTURING;
        if (state == State.READY) {
            startTime = Instant.now();
        }
        mediaSize += data.length;
        bufferedOutputStream.write(data);
        state = State.CAPTURING;
    }

    public void complete() throws IOException {
        assert state == State.CAPTURING;
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        endTime = Instant.now();
        state = State.COMPLETED;
    }

    public void close() {
        assert state == State.COMPLETED;
        LOG.info("\t\tCapture Summary\n" +
                "---------------------------------" + "\n" +
                "\t- Artifact Location: " + media.toString() + "\n" +
                "\t- Artifact Name: " + media.getFileName().toString() + "\n" +
                "\t- Artifact Size: " + mediaSize / (1024 * 1024) + "\n" +
                "\t- Capture Duration: " + Duration.between(startTime, endTime) + "\n");
        state = State.CLOSED;
    }

    public void error() throws IOException {
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        media.toFile().delete();
        state = State.ERROR;
    }
}
