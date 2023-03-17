package ch.progradler.rat_um_rad.server.protocol;

import ch.progradler.rat_um_rad.shared.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private final List<ServerSocketHandler> connections = new ArrayList<>(); //TODO: use arraylist or is another type better ? --> sets ??

    public void addConnection(ServerSocketHandler socketHandler) {
        connections.add(socketHandler);
    }

    public void broadcast(Message message) {
        for (ServerSocketHandler connection : connections) {
            if (!connection.getClientName().equals(message.getUsername())) { //so it doesn't send the message back to yourself
                connection.sendMessageToClient(message);
            }
        }
    }
    public void removeConnection(ServerSocketHandler socketHandler) {
        // TODO: might not work, possibly have to search by username
        connections.remove(socketHandler);
    }
}
