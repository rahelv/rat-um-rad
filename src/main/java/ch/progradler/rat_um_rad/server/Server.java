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
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;
import ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks.DestinationCardCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.cards_and_decks.WheelCardCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.CityCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.GameMapCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.PointCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.game.RoadCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.player.PlayerCoder;
import ch.progradler.rat_um_rad.shared.protocol.coder.player.VisiblePlayerCoder;

public class Server {
    public void start(int port) {
        System.out.format("Starting Server on %d\n", port);

        ClientConnectionsHandler connectionsHandler = new ClientConnectionsHandler(getPacketCoder());
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
        return new GameService(outputPacketGateway, gameRepository, userRepository);
    }

    private static Coder<Packet> getPacketCoder() {
        Coder<GameMap> gameMapCoder = new GameMapCoder(new CityCoder(new PointCoder()), new RoadCoder());
        return new PacketCoder(new ChatMessageCoder(),
                new UsernameChangeCoder(),
                new GameBaseCoder(gameMapCoder),
                new ClientGameCoder(gameMapCoder, new VisiblePlayerCoder(), new PlayerCoder(new WheelCardCoder(), new DestinationCardCoder(new CityCoder(new PointCoder())))));
    }
}
