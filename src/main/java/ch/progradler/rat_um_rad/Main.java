package ch.progradler.rat_um_rad;

import ch.progradler.rat_um_rad.client.Client;
import ch.progradler.rat_um_rad.server.Server;

public class Main {
    public static final int DEFAULT_PORT = 8090;
    public static final String LOCAL_HOST = "localhost";

    private static final String SERVER_COMMAND = "server";
    private static final String CLIENT_COMMAND = "client";

    public static void main(String[] args) {
        if (args.length == 0) {
            printInvalidArgsAndExit();
        }

        String serverOrClient = args[0];
        System.out.println(args[0]); // TODO: remove

        int port = getPort(args);
        if (serverOrClient.equals(SERVER_COMMAND)) {
            startServer(port);
        } else if (serverOrClient.equals(CLIENT_COMMAND)) {
            startClient(args, port);
        } else {
            printInvalidArgsAndExit();
        }
    }

    private static void startServer(int port) {
        Server server = new Server();
        server.start(port);
    }

    private static void startClient(String[] args, int port) {
        String host = getHost(args);
        Client client = new Client();
        client.start(host, port);
    }

    private static int getPort(String[] args) {

        //case distinction depending on whether user wants to start server or client
        String portString = ""; //this string won't be used when args[0] ist equal to either SERVER_COMMAND or CLIENT_COMMAND
        if (args[0].equals(SERVER_COMMAND)) {
            if (args.length < 2) {
                System.out.format("No port defined in args. Using default port %d\n", DEFAULT_PORT);
                return DEFAULT_PORT;
            }
            portString = args[1];
        } else if (args[0].equals(CLIENT_COMMAND)) {
            if (args.length < 2) {
                System.out.format("Not enough arguments given. Using default port %d\n", DEFAULT_PORT);
                return DEFAULT_PORT;
            }
            portString = args[2];
        }

        try {
            return Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.format("Badly formatted port defined in args (%s). Using default port %d\n", portString, DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    private static String getHost(String[] args) {
        if (args.length < 3) {
            System.out.format("Not enough arguments given. Using localhost %s\n", LOCAL_HOST);
            return LOCAL_HOST;
        } else {
            return args[1]; // TODO: validate?
        }
    }

    private static void printInvalidArgsAndExit() {
        System.out.println("Invalid arguments to start application!");
        System.out.println("Please provide an argument for whether you want to start the server or client and a port and host.");
        System.exit(0);
    }
}
