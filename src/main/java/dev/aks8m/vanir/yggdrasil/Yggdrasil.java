package dev.aks8m.vanir.yggdrasil;


import io.javalin.Javalin;
import io.javalin.websocket.WsBinaryMessageContext;
import io.javalin.websocket.WsMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class Yggdrasil {

    private static final Logger LOG = LoggerFactory.getLogger(Yggdrasil.class);

    private final Javalin server;
    private final ByteBuffer receivedBytes = ByteBuffer.allocate(1024);

    public Yggdrasil() {
        server = Javalin.create();

        server.ws("/ygg", ws -> {
            ws.onConnect(ctx -> {
                LOG.info("Connected to yggdrasil server");
                ctx.session.setIdleTimeout(Duration.ZERO);
                ctx.session.getPolicy().setMaxBinaryMessageSize(1024 * 1024 *1024);
            });
            ws.onMessage(ctx-> {
                LOG.info("Received message: {}", ctx.message());
            });
            ws.onBinaryMessage(ctx -> {
                LOG.info("Received binary message: " + ctx.data().length / (1024 * 1024) + " MB");
                ByteBuffer byteBuffer = ByteBuffer.wrap(ctx.data());
                try {
                    Path output = Paths.get("/home/aks8m/Downloads", "/ygg.webm");
                    Files.write(output, byteBuffer.array());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            ws.onClose(ctx -> {
                LOG.info("Closing connection");
            });
            ws.onError(e -> {
                LOG.error("Error: ", e.error());
            });
        });
    }
    public void start(int port) {
        server.start(port);
    }

    public Javalin getServer() {
        return server;
    }

    public static void main(String[] args) {
        Yggdrasil yggdrasil = new Yggdrasil();
        yggdrasil.start(7070);
    }
}
