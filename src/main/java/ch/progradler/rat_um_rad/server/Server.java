package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.server.protocol.ClientConnectionsHandler;
import ch.progradler.rat_um_rad.server.protocol.CommandHandler;

public class Server {
    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);

        ClientConnectionsHandler connectionsHandler = new ClientConnectionsHandler();
        CommandHandler commandHandler = new CommandHandler(connectionsHandler.connectionPool);
        connectionsHandler.start(port, commandHandler);
    }
}
