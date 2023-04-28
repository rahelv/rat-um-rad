package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;

/**
 * Creates the {@link ActionHandler}s.
 */
public class ActionHandlerFactory {

    private final IGameRepository gameRepository;
    private final IUserRepository userRepository;
    private final OutputPacketGateway outputPacketGateway;

    public ActionHandlerFactory(IGameRepository gameRepository, IUserRepository userRepository, OutputPacketGateway outputPacketGateway) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.outputPacketGateway = outputPacketGateway;
    }

    public RoadActionHandler createRoadActionHandler() {
        return new RoadActionHandler(gameRepository, userRepository, outputPacketGateway, new GameEndUtil());
    }
}
