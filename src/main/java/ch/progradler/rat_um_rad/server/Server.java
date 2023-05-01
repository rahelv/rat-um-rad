package ch.progradler.rat_um_rad.server;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.protocol.ClientConnectionsHandler;
import ch.progradler.rat_um_rad.server.protocol.CommandHandler;
import ch.progradler.rat_um_rad.server.protocol.pingpong.ServerPingPongRunner;
import ch.progradler.rat_um_rad.server.repositories.*;
import ch.progradler.rat_um_rad.server.services.GameService;
import ch.progradler.rat_um_rad.server.services.IGameService;
import ch.progradler.rat_um_rad.server.services.IUserService;
import ch.progradler.rat_um_rad.server.services.UserService;
import ch.progradler.rat_um_rad.shared.database.Database;
import ch.progradler.rat_um_rad.shared.database.FileStorage;
import ch.progradler.rat_um_rad.shared.database.HighscoresDatabase;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import ch.progradler.rat_um_rad.shared.protocol.coder.HighscoreCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.packet.PacketCoder;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;

import java.util.List;

public class Server {
    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);

        ClientConnectionsHandler connectionsHandler = new ClientConnectionsHandler(
                PacketCoder.defaultServerPacketCoder(),
                PacketCoder.defaultClientPacketCoder());
        ServerPingPongRunner serverPingPongRunner = new ServerPingPongRunner(connectionsHandler.connectionPool);
        new Thread(serverPingPongRunner).start();
        OutputPacketGateway outputPacketGateway = connectionsHandler.connectionPool;

        IUserRepository userRepository = new UserRepository();
        IGameRepository gameRepository = new GameRepository();
        CommandHandler commandHandler = new CommandHandler(
                serverPingPongRunner,
                getUserService(outputPacketGateway, userRepository, gameRepository),
                getGameService(outputPacketGateway, userRepository, gameRepository));
        connectionsHandler.start(port, commandHandler, serverPingPongRunner);
    }

    private static IUserService getUserService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository, IGameRepository gameRepository) {
        return new UserService(outputPacketGateway, userRepository, gameRepository, new UsernameValidator());
    }

    private static IGameService getGameService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository, IGameRepository gameRepository) {
        Database<List<Highscore>> database = new HighscoresDatabase(new FileStorage(), new HighscoreCoder());
        return new GameService(outputPacketGateway, gameRepository, userRepository, new HighscoreRepository(database));
    }
}
