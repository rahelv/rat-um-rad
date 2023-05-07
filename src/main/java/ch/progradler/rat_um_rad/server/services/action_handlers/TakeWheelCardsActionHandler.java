package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.HighscoreManager;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCardDeck;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;

import java.util.List;

/**
 * Handles take wheel cards from deck action.
 */
public class TakeWheelCardsActionHandler extends ActionHandler<Object> {
    public TakeWheelCardsActionHandler(IGameRepository gameRepository, IUserRepository userRepository, OutputPacketGateway outputPacketGateway, GameEndUtil gameEndUtil, HighscoreManager highscoreManager) {
        super(gameRepository, userRepository, outputPacketGateway, gameEndUtil, highscoreManager);
    }

    /**
     * @param dontUseActionData is not used in this method.
     */
    @Override
    protected String validate(Game game, String ipAddress, Object dontUseActionData) {
        WheelCardDeck wheelCardDeck = game.getDecksOfGame().getWheelCardDeck();
        if (wheelCardDeck.getDeckOfCards().size() < 2) { //if there are not enough wheelCards in deck
            //TODO: implement and test
        }
        return null;
    }

    @Override
    protected void updateGameState(Game game, String ipAddress, Object dontUseActionData) {
        Player player = game.getPlayers().get(ipAddress);
        List<WheelCard> wheelCardDeck = game.getDecksOfGame().getWheelCardDeck().getDeckOfCards();
        for (int i = 0; i < 2; i++) {
            WheelCard wheelCard = wheelCardDeck.get(0);
            wheelCardDeck.remove(0);
            player.getWheelCards().add(wheelCard);
        }
        game.getDecksOfGame().setWheelCardDeck(new WheelCardDeck(wheelCardDeck));
        game.getPlayers().put(ipAddress, player);
    }

    @Override
    protected boolean canEndGame() {
        return false;
    }

    @Override
    protected ServerCommand getActivityCommand() {
        return ServerCommand.WHEEL_CARDS_CHOSEN;
    }
}
