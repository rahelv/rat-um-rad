package ch.progradler.rat_um_rad;

import ch.progradler.rat_um_rad.client.Client;
import ch.progradler.rat_um_rad.server.Server;

public class Main {
    public static int defaultPort = 8090;
    public static String localhost = "localhost";


    public static void main(String[] args) {
        // TODO: use port and client host from args

        System.out.println(args[0]); // TODO: remove
        if (args[0].equals("server")) {
            Server server = new Server();
            server.start(defaultPort);
        } else if (args[0].equals("client")) {
            Client client = new Client();
            client.start(localhost, defaultPort);
        } else {
            System.out.println("Invalid command to start application!");
        }
    }
}
