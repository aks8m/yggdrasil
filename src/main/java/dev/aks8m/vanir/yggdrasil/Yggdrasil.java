package dev.aks8m.vanir.yggdrasil;


import dev.aks8m.vanir.yggdrasil.config.MediaConfig;
import dev.aks8m.vanir.yggdrasil.config.PathConfig;
import dev.aks8m.vanir.yggdrasil.config.ServerConfig;
import dev.aks8m.vanir.yggdrasil.capture.CaptureManager;
import dev.aks8m.vanir.yggdrasil.message.WebSocketMessage;
import io.javalin.Javalin;
import io.javalin.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Yggdrasil {

    private static final Logger LOG = LoggerFactory.getLogger(Yggdrasil.class);

    private final ServerConfig serverConfig;
    private final PathConfig pathConfig;
    private final MediaConfig mediaConfig;

    private final Javalin server;
    private final CaptureManager captureManager;

    public Yggdrasil(ServerConfig serverConfig, PathConfig pathConfig, MediaConfig  mediaConfig) {
        this.serverConfig = serverConfig;
        this.pathConfig = pathConfig;
        this.mediaConfig = mediaConfig;
        this.captureManager = new CaptureManager(this.mediaConfig.outputDirectory());
        server = Javalin.create();
    }

    public Yggdrasil init() {
        server.ws(pathConfig.webM(), ws -> {
            ws.onConnect(this::handleOnConnect);
            ws.onMessage(this::handleMessage);
            ws.onBinaryMessage(this::handleWebMBinaryMessage);
            ws.onClose(this::handleOnClose);
            ws.onError(this::handleOnError);
        });
        return this;
    }

    private void handleOnConnect(WsConnectContext ctx) {
        ctx.session.setIdleTimeout(serverConfig.idleTimeout());
        ctx.session.getPolicy().setMaxBinaryMessageSize(serverConfig.maxBinaryMessageSize());
        captureManager.createCapture(ctx.sessionId());
        LOG.info("Connected to yggdrasil server");
    }

    private void handleOnClose(WsCloseContext ctx) {
        captureManager.closeCapture(ctx.sessionId());
        LOG.info("Closing connection");
    }

    private void handleOnError(WsErrorContext ctx) {
        captureManager.manageError(ctx.sessionId());
        LOG.error("Error: ", ctx.error());
    }

    private void handleMessage(WsMessageContext ctx) {
        WebSocketMessage webSocketMessage = ctx.messageAsClass(WebSocketMessage.class);
        // TODO-aks8m: Need to see how jackson? is working when converting. Can I have getter/setters rather than public fields??

        LOG.info("Received message: {}", ctx.message());
    }

    private void handleWebMBinaryMessage(WsBinaryMessageContext ctx) {
        captureManager.manageData(ctx.sessionId(), ctx.data());
        LOG.info("Received binary message: {} MB", ctx.data().length / (1024 * 1024));
    }

    public void start() {
        server.start(serverConfig.port());
    }

    public void stop() {
        server.stop();
    }
}
