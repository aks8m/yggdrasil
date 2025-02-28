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

    public void init(Path media) throws FileNotFoundException {
        this.media = media;
        FileOutputStream fileOutputStream = new FileOutputStream(media.toFile());
        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
    }

    public void capture(byte[] data) throws IOException {
        mediaSize += data.length;
        bufferedOutputStream.write(data);
    }

    public void complete() throws IOException {
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    public void close() {
        LOG.info("\t\tCapture Summary\n" +
                "---------------------------------" + "\n" +
                "\t- Artifact Location: " + media.toString() + "\n" +
                "\t- Artifact Name: " + media.getFileName().toString() + "\n" +
                "\t- Artifact Size: " + mediaSize / (1024 * 1024) + "\n" +
                "\t- Capture Duration: " + Duration.between(startTime, endTime) + "\n");
    }

    public void error() throws IOException {
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        boolean wasDeleted = media.toFile().delete();
        if (wasDeleted) {
            LOG.info("File deleted: " + media.toFile().getAbsolutePath());
        } else {
            LOG.info("File not deleted: " + media.toFile().getAbsolutePath());
        }
    }


    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
