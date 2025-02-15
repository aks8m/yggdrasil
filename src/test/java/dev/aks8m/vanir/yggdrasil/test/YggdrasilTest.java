package dev.aks8m.vanir.yggdrasil.test;

import dev.aks8m.vanir.yggdrasil.Yggdrasil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class YggdrasilTest {

    @Test
    public void simpleAppTest() {

        Yggdrasil.main(null);
        assertTrue(true, "App failed to run");
    }

}
