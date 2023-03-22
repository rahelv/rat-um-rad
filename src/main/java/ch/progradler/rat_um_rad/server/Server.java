package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.server.protocol.ClientConnectionsHandler;
import ch.progradler.rat_um_rad.server.protocol.CommandHandler;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.ChatMessageCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.PacketCoder;

public class Server {
    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);

        ClientConnectionsHandler connectionsHandler = new ClientConnectionsHandler(getPacketCoder());
        CommandHandler commandHandler = new CommandHandler(connectionsHandler.connectionPool);
        connectionsHandler.start(port, commandHandler);
    }

    private static Coder<Packet> getPacketCoder() {
        return new PacketCoder(new ChatMessageCoder());
    }
}
