package dev.aks8m.vanir.yggdrasil.test;

import dev.aks8m.vanir.yggdrasil.Application;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    @Test
    public void simpleAppTest() {

        Application.main(null);
        assertTrue(true, "App failed to run");
    }

}
