package ch.progradler.rat_um_rad.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(8090);
            ConnectionPool cp = new ConnectionPool();
            System.out.println("Server listening on port 8090");
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
