package dev.aks8m.vanir.yggdrasil.config;

import java.nio.file.Path;
import java.time.Duration;

public record ServerConfig(int port, long maxBinaryMessageSize, Duration idleTimeout) {
}
