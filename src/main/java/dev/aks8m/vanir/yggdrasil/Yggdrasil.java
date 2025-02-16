package dev.aks8m.vanir.yggdrasil;


import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Yggdrasil {

    private static final Logger LOG = LoggerFactory.getLogger(Yggdrasil.class);

    public void start (int port) {
        Javalin.create(javalinConfig -> {
//            javalinConfig.router.mount(router -> {

//                router.ws("/ygg", ws -> {
//                    ws.onConnect(ctx -> LOG.info("Connected to the chat server"));
//                    ws.onClose(ctx -> LOG.info("Disconnected from the chat server"));
//                    ws.onMessage(ctx -> LOG.info("Received message from the chat server"));
//                });
//            });
        }).get("/", ctx -> ctx.result("Hello World"))
                .start(7070);
    }
}
