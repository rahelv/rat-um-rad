package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.server.protocol.ConnectionPool;
import ch.progradler.rat_um_rad.server.protocol.ServerSocketHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ConnectionPool connectionPool = new ConnectionPool();
            System.out.format("Server listening on port %d\n", port);
            while (true) { //keeps running = keeps accepting clients
                Socket socket = serverSocket.accept();

                ServerSocketHandler socketHandler = new ServerSocketHandler(socket, connectionPool);
                connectionPool.addConnection(socketHandler);

                Thread t = new Thread(socketHandler);
                t.start();

                //Add to connection pool
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
