package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IHighscoreRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.HighscoreManager;
import ch.progradler.rat_um_rad.server.validation.SelectDestinationCardsValidator;

/**
 * Creates the {@link ActionHandler}s.
 */
public class ActionHandlerFactory {

    private final IGameRepository gameRepository;
    private final IUserRepository userRepository;
    private final IHighscoreRepository highscoreRepository;
    private final OutputPacketGateway outputPacketGateway;

    public ActionHandlerFactory(IGameRepository gameRepository, IUserRepository userRepository, IHighscoreRepository highscoreRepository, OutputPacketGateway outputPacketGateway) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.highscoreRepository = highscoreRepository;
        this.outputPacketGateway = outputPacketGateway;
    }

    public RoadActionHandler createRoadActionHandler() {
        return new RoadActionHandler(gameRepository, userRepository, outputPacketGateway, new GameEndUtil(), new HighscoreManager(highscoreRepository));
    }

    public TakeWheelCardsActionHandler createTakeWheelCardsActionHandler() {
        return new TakeWheelCardsActionHandler(gameRepository, userRepository, outputPacketGateway, new GameEndUtil(), new HighscoreManager(highscoreRepository));
    }

    public SelectDestinationCardsActionHandler createSelectDestinationCardsActionHandler() {
        return new SelectDestinationCardsActionHandler(gameRepository,
                userRepository,
                outputPacketGateway,
                new GameEndUtil(),
                new HighscoreManager(highscoreRepository),
                new SelectDestinationCardsValidator());
    }
}
