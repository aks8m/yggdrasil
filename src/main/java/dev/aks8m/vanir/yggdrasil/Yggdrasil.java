package dev.aks8m.vanir.yggdrasil;


import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Yggdrasil {

    private static final Logger LOG = LoggerFactory.getLogger(Yggdrasil.class);

    public static void main(String[] args) {
        Javalin app = Javalin.create();

        LOG.info("Application info");
        LOG.debug("Application debug");
        LOG.error("Application error");

    }
}
