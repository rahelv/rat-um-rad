package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.GameServiceUtil;
import ch.progradler.rat_um_rad.server.services.HighscoreManager;
import ch.progradler.rat_um_rad.server.validation.SelectDestinationCardsValidator;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.util.List;

import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.SELECTED_SHORT_DESTINATION_CARDS_INVALID;

/**
 * Handles selecting destination cards action if game already started
 */
public class SelectDestinationCardsActionHandler extends ActionHandler<List<String>> {
    private final SelectDestinationCardsValidator validator;

    public SelectDestinationCardsActionHandler(IGameRepository gameRepository, IUserRepository userRepository, OutputPacketGateway outputPacketGateway, GameEndUtil gameEndUtil, HighscoreManager highscoreManager, SelectDestinationCardsValidator validator) {
        super(gameRepository, userRepository, outputPacketGateway, gameEndUtil, highscoreManager);
        this.validator = validator;
    }

    @Override
    protected String validate(Game game, String ipAddress, List<String> cardsIds) {
        Player player = game.getPlayers().get(ipAddress);
        if (!validator.validate(player, cardsIds)) {
            return SELECTED_SHORT_DESTINATION_CARDS_INVALID;
        }
        return null;
    }

    @Override
    protected void updateGameState(Game game, String ipAddress, List<String> cardIds) {
        GameServiceUtil.updateGameStateForShortDestCardsSelectionGeneral(
                cardIds, game, game.getPlayers().get(ipAddress));
    }

    @Override
    protected boolean canEndGame() {
        return false;
    }

    @Override
    protected ServerCommand getActivityCommand() {
        return ServerCommand.DESTINATION_CARDS_SELECTED;
    }
}
