package dev.aks8m.vanir.yggdrasil.test;

import dev.aks8m.vanir.yggdrasil.Yggdrasil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class YggdrasilIT {

    @Test
    public void simpleAppTest() {

        Yggdrasil app = new Yggdrasil();
        app.start(7070);
    }

}
