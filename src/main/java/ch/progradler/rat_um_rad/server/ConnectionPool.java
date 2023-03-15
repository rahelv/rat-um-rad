package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.shared.Message;

import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private List<ServerSocketHandler> connections = new ArrayList<>(); //TODO: use arraylist or is another type better ? --> sets ??

    public void addConnection(ServerSocketHandler ssh) {
        connections.add(ssh);
    }

    public void broadcast(Message msg) {
        for(ServerSocketHandler connection : connections) {
            if(!connection.getClientName().equals(msg.getUsername())) { //so it doesn't send the message back to yourself
                connection.sendMessageToClient(msg);
            }
        }
    }


    public void removeConnection(ServerSocketHandler ssh) {
        connections.remove(ssh);
    }
}
