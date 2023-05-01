package ch.progradler.rat_um_rad;

import ch.progradler.rat_um_rad.client.Client;
import ch.progradler.rat_um_rad.server.Server;
import ch.progradler.rat_um_rad.start.MainArgs;
import ch.progradler.rat_um_rad.start.MainArgsParser;

public class Main {
    /**
     * Depending on @param args, either the server or the client is started as specified in the requirements.
     */
    public static void main(String[] args) {
        MainArgs mainArgs = null;
        try {
            mainArgs = MainArgsParser.parseArgs(args);
        } catch (Exception e) {
            printInvalidArgsAndExit(e.getLocalizedMessage());
        }
        if (mainArgs == null) return;
        if (mainArgs.isStartServer()) {
            startServer(mainArgs.getPort());
        } else {
            startClient(mainArgs.getHost(), mainArgs.getPort(), mainArgs.getUsername());
        }
    }

    private static void startServer(int port) {
        Server server = new Server();
        server.start(port);
    }

    private static void startClient(String host, int port, String username) {
        Client client = new Client();
        client.start(host, port, username);
    }

    private static void printInvalidArgsAndExit(String error) {
        System.out.println(error);
        System.exit(0);
    }
}
