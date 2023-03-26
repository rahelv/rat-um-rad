package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.ClientConnectionsHandler;
import ch.progradler.rat_um_rad.server.protocol.CommandHandler;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.repositories.UserRepository;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.server.services.UserService;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.ChatMessageCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.Coder;
import ch.progradler.rat_um_rad.shared.protocol.coder.PacketCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.UsernameChangeCoder;

public class Server {
    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);

        ClientConnectionsHandler connectionsHandler = new ClientConnectionsHandler(getPacketCoder());
        ServerPingPongRunner serverPingPongRunner = new ServerPingPongRunner(connectionsHandler.connectionPool);
        new Thread(serverPingPongRunner).start();
        OutputPacketGateway outputPacketGateway = connectionsHandler.connectionPool;
        CommandHandler commandHandler = new CommandHandler(
                serverPingPongRunner, getUserService(outputPacketGateway));
        connectionsHandler.start(port, commandHandler, serverPingPongRunner);
    }

    private static IUserService getUserService(OutputPacketGateway outputPacketGateway) {
        IUserRepository userRepository = new UserRepository();
        return new UserService(outputPacketGateway, userRepository);
    }

    private static Coder<Packet> getPacketCoder() {
        return new PacketCoder(new ChatMessageCoder(), new UsernameChangeCoder());
    }
}
