package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.server.protocol.ConnectionPool;
import ch.progradler.rat_um_rad.server.protocol.ServerSocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static ch.progradler.rat_um_rad.Main.DEFAULT_PORT;

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        server.start(DEFAULT_PORT);
    }

    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ConnectionPool cp = new ConnectionPool();
            System.out.format("Server listening on port %d\n", port);
            //TODO: create connection pool
            while (true) { //keeps running = keeps accepting clients
                Socket socket = serverSocket.accept();

                ServerSocketHandler ssh = new ServerSocketHandler(socket, cp);
                cp.addConnection(ssh);

                Thread t = new Thread(ssh);
                t.start();

                //Add to connection pool
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
