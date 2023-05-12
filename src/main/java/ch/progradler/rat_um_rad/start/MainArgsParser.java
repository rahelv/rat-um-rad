package ch.progradler.rat_um_rad.start;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Responsible for parsing the input arguments to start the application into a {@link MainArgs} object.
 */
public class MainArgsParser {

    public static final int DEFAULT_PORT = 8090;
    public static final String LOCAL_HOST = "localhost";

    private static final String SERVER_COMMAND = "server";
    private static final String CLIENT_COMMAND = "client";
    public static final Logger LOGGER = LogManager.getLogger();

    public static MainArgs parseArgs(String[] args) {
        if (args.length == 0) throw new IllegalArgumentException("No args given!");
        String serverOrClient = args[0];

        if (serverOrClient.equals(SERVER_COMMAND)) {
            return parseServerArgs(args);
        } else if (serverOrClient.equals(CLIENT_COMMAND)) {
            return parseClientArgs(args);
        }
        throw new IllegalArgumentException("First argument was not \"" + SERVER_COMMAND
                + "\" or \"" + CLIENT_COMMAND + "\"!");
    }

    private static MainArgs parseClientArgs(String[] args) {
        String hostAndPortString = args.length < 2 ? "" : args[1];
        String[] hostAndPort = hostAndPortString.split(":");
        String host = hostAndPort[0];
        if (host.isEmpty()) {
            LOGGER.warn("No host address defined in args. Using default host " + LOCAL_HOST);
            System.out.println("No host address defined in args. Using default host " + LOCAL_HOST);
            host = LOCAL_HOST;
        }

        int port;
        if (hostAndPort.length < 2) {
            LOGGER.warn("No port defined in args. Using default port %d\n", DEFAULT_PORT);
            System.out.format("No port defined in args. Using default port %d\n", DEFAULT_PORT);
            port = DEFAULT_PORT;
        } else port = getPort(hostAndPort[1]);
        String username = args.length < 3 ? null : args[2];
        return new MainArgs(false, port, host, username);
    }

    private static MainArgs parseServerArgs(String[] args) {
        int port;
        if (args.length < 2) {
            LOGGER.warn("No port defined in args. Using default port %d\n", DEFAULT_PORT);
            System.out.format("No port defined in args. Using default port %d\n", DEFAULT_PORT);
            port = DEFAULT_PORT;
        } else port = getPort(args[1]);
        return new MainArgs(true, port);
    }

    private static int getPort(String portString) {
        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            LOGGER.warn("Badly formatted port defined in args (%s). Using default port %d\n", portString, DEFAULT_PORT);
            System.out.format("Badly formatted port defined in args (%s). Using default port %d\n", portString, DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}
