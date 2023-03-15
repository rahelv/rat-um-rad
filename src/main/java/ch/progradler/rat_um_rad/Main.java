package ch.progradler.rat_um_rad;

import ch.progradler.rat_um_rad.client.Client;
import ch.progradler.rat_um_rad.server.Server;

public class Main {
    public static void main(String[] args) {
        System.out.println(args[0]);
        if(args[0].equals("server")) {
            Server server = new Server();
            server.start();
        }
        if (args[0].equals("client")) {
            Client client = new Client();
            client.start();
        }
    }
}
