package ch.progradler.rat_um_rad.server;

public class Main {

    /**
     * Starts the server.
     */
    public static void main(String[] args) {
        System.out.println("Server running...");
        Server server = new Server();
        server.start(ch.progradler.rat_um_rad.Main.DEFAULT_PORT);
        System.out.println("Server stopped!");
    }
}
