package dev.aks8m.vanir.yggdrasil;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aks8m.vanir.yggdrasil.capture.CaptureManager;
import dev.aks8m.vanir.yggdrasil.config.MediaConfig;
import dev.aks8m.vanir.yggdrasil.config.PathConfig;
import dev.aks8m.vanir.yggdrasil.config.ServerConfig;
import dev.aks8m.vanir.yggdrasil.message.CompleteCapturePayload;
import dev.aks8m.vanir.yggdrasil.message.InitCapturePayload;
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
        server.ws(pathConfig.webM(), wsConfig -> {
            wsConfig.onConnect(this::handleOnConnect);
            wsConfig.onMessage(this::handleOnMessage);
            wsConfig.onBinaryMessage(this::handleOnBinaryMessage);
            wsConfig.onClose(this::handleOnClose);
            wsConfig.onError(this::handleOnError);
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

    private void handleOnMessage(WsMessageContext ctx) {
        WebSocketMessage webSocketMessage = ctx.messageAsClass(WebSocketMessage.class);
        if (webSocketMessage != null) {
            ObjectMapper  mapper = new ObjectMapper();
            if (webSocketMessage.getEvent().equals("init_capture")) {
                InitCapturePayload initCapturePayload = mapper.convertValue(webSocketMessage.getPayload(), InitCapturePayload.class);
                if (initCapturePayload  != null) {
                    captureManager.manageInit(ctx.sessionId(), initCapturePayload);
                } else {
                    LOG.error("Error with Init Capture Payload: {}", webSocketMessage.getPayload());
                }
            } else if (webSocketMessage.getEvent().equals("complete_capture")) {
                CompleteCapturePayload completeCapturePayload = mapper.convertValue(webSocketMessage.getPayload(), CompleteCapturePayload.class);
                if  (completeCapturePayload != null) {
                    captureManager.manageComplete(ctx.sessionId(), completeCapturePayload);
                } else {
                    LOG.error("Error with Complete Capture Payload: {}", webSocketMessage.getPayload());
                }
            }
        }
        LOG.info("Received message: {}", ctx.message());
    }

    private void handleOnBinaryMessage(WsBinaryMessageContext ctx) {
        captureManager.manageCapture(ctx.sessionId(), ctx.data());
        LOG.info("Received binary message: {} MB", ctx.data().length / (1024 * 1024));
    }

    public void start() {
        server.start(serverConfig.port());
    }

    public void stop() {
        server.stop();
    }
}
