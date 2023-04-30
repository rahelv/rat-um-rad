package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.PlayerColor;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCardDeck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.progradler.rat_um_rad.shared.models.game.GameStatus.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TakeWheelCardsHandlerTest {
    @Mock
    IGameRepository mockGameRepository;
    @Mock
    IUserRepository mockUserRepository;
    @Mock
    OutputPacketGateway mockOutputPacketGateway;

    private TakeWheelCardsActionHandler takeWheelCardsActionHandler;

    @BeforeEach
    void setUp() {
        takeWheelCardsActionHandler = new TakeWheelCardsActionHandler(mockGameRepository, mockUserRepository, mockOutputPacketGateway, new GameEndUtil());
    }

    @Test
    void updateGameStateWorksCorrectly() {
        //preparation
        WheelCard wheelCard1 = new WheelCard(1);
        WheelCard wheelCard2 = new WheelCard(2);
        WheelCard wheelCard3 = new WheelCard(3);
        WheelCard wheelCard4 = new WheelCard(4);
        List<WheelCard> playerWheelCards = new ArrayList<>();
        List<WheelCard> gameWheelCards = new ArrayList<>();
        playerWheelCards.add(wheelCard1);
        gameWheelCards.add(wheelCard2);
        gameWheelCards.add(wheelCard3);
        gameWheelCards.add(wheelCard4);
        WheelCardDeck gameWheelCardDeck = new WheelCardDeck(gameWheelCards);

        List<WheelCard> expectedInPlayer = new ArrayList<>();
        expectedInPlayer.add(wheelCard1);
        expectedInPlayer.add(wheelCard2);
        expectedInPlayer.add(wheelCard3);
        List<WheelCard> expectedInGame = new ArrayList<>();
        expectedInGame.add(wheelCard4);

        String playerIp = "playerIp";
        Player player = new Player("playerIp", PlayerColor.PINK, 0, 0, 0, playerWheelCards, null, null);
        Map<String, Player> playerMap = new HashMap<>();
        playerMap.put(playerIp, player);
        Game game = new Game("gameId", STARTED, GameMap.defaultMap(), "creator", 0, playerMap);
        game.getDecksOfGame().setWheelCardDeck(gameWheelCardDeck);

        //test
        takeWheelCardsActionHandler.updateGameState(game, playerIp, "");

        assertEquals(expectedInGame, game.getDecksOfGame().getWheelCardDeck().getDeckOfCards());
        assertEquals(expectedInPlayer, player.getWheelCards());
    }


}
