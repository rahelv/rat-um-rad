package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.ClientConnectionsHandler;
import ch.progradler.rat_um_rad.server.protocol.CommandHandler;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.repositories.GameRepository;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.repositories.UserRepository;
import ch.progradler.rat_um_rad.server.services.GameService;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.server.services.UserService;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.coder.*;

public class Server {
    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);

        ClientConnectionsHandler connectionsHandler = new ClientConnectionsHandler(getPacketCoder());
        ServerPingPongRunner serverPingPongRunner = new ServerPingPongRunner(connectionsHandler.connectionPool);
        new Thread(serverPingPongRunner).start();
        OutputPacketGateway outputPacketGateway = connectionsHandler.connectionPool;

        IUserRepository userRepository = new UserRepository();
        CommandHandler commandHandler = new CommandHandler(
                serverPingPongRunner,
                getUserService(outputPacketGateway, userRepository),
                getGameService(outputPacketGateway, userRepository));
        connectionsHandler.start(port, commandHandler, serverPingPongRunner);
    }

    private static IUserService getUserService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository) {
        return new UserService(outputPacketGateway, userRepository);
    }

    private static IGameService getGameService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository) {
        IGameRepository gameRepository = new GameRepository();
        return new GameService(outputPacketGateway, gameRepository, userRepository);
    }

    private static Coder<Packet> getPacketCoder() {
        Coder<GameMap> gameMapCoder = new Coder<>() {
            @Override
            public String encode(GameMap object, int level) {
                return null;
            }

            @Override
            public GameMap decode(String encoded, int level) {
                return null;
            }
        }; // TODO: implement correctly
        return new PacketCoder(new ChatMessageCoder(),
                new UsernameChangeCoder(),
                new GameBaseCoder(gameMapCoder));
    }
}
