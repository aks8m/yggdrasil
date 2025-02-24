package dev.aks8m.vanir.yggdrasil;

import dev.aks8m.vanir.yggdrasil.config.MediaConfig;
import dev.aks8m.vanir.yggdrasil.config.PathConfig;
import dev.aks8m.vanir.yggdrasil.config.ServerConfig;
import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.time.Duration;

public class App {

    private static final Options options = new Options();

    private static final Option portOption = Option.builder()
            .longOpt("port")
            .option("p")
            .desc("Specific port server is listening on")
            .hasArg()
            .required(true)
            .build();

    private static final Option maxBinaryMessageSizeOption = Option.builder()
            .longOpt("maxBinaryMessageSize")
            .option("x")
            .desc("Max binary message size (GB) allowed by server")
            .hasArg()
            .required(true)
            .build();

    private final static Option idleTimeoutOption = Option.builder()
            .longOpt("idleTimeout")
            .option("t")
            .desc("Idle timeout (seconds) allowed by server")
            .hasArg()
            .required(true)
            .build();

    private final static Option webMPathOption = Option.builder()
            .longOpt("webMPath")
            .option("w")
            .desc("Relative URL for webM path")
            .hasArg()
            .required(true)
            .build();

    private final static Option outputDirectoryOption = Option.builder()
            .longOpt("outputDirectory")
            .option("o")
            .desc("Output directory for media files to be written by server")
            .hasArg()
            .required(true)
            .build();

    public static void main(String[] args) throws ParseException {
        options.addOption(portOption);
        options.addOption(maxBinaryMessageSizeOption);
        options.addOption(idleTimeoutOption);
        options.addOption(webMPathOption);
        options.addOption(outputDirectoryOption);


        CommandLineParser cliParser = new DefaultParser();
        CommandLine cli = cliParser.parse(options, args);

        int port = convertOptionToInt(portOption, cli);
        int maxBinaryMessageSize = convertOptionToInt(maxBinaryMessageSizeOption, cli) * 1024 * 1024 * 1024;
        Duration idleTimeout = Duration.ofSeconds(convertOptionToInt(idleTimeoutOption, cli));
        Path outputDirectory = Path.of(convertOptionToString(outputDirectoryOption, cli));
        String parsedWebMPath = convertOptionToString(webMPathOption, cli);

        ServerConfig serverConfig = new ServerConfig(port, maxBinaryMessageSize, idleTimeout);
        PathConfig pathConfig = new PathConfig(parsedWebMPath);
        MediaConfig mediaConfig = new MediaConfig(outputDirectory);

        new Yggdrasil(serverConfig, pathConfig, mediaConfig)
                .init()
                .start();
    }

    private static int convertOptionToInt(Option option, CommandLine cli) throws ParseException {
        return Integer.parseInt(cli.getParsedOptionValue(option));
    }

    private static String convertOptionToString(Option option, CommandLine cli) throws ParseException {
        return cli.getParsedOptionValue(option);
    }
}
