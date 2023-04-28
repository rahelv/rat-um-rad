package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.Activity;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.*;
import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.ROAD_BUILT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadActionHandlerTest {

    @Mock
    IGameRepository mockGameRepository;
    @Mock
    IUserRepository mockUserRepository;
    @Mock
    OutputPacketGateway mockOutputPacketGateway;

    private RoadActionHandler roadActionHandler;

    @BeforeEach
    void setUp() {
        roadActionHandler = new RoadActionHandler(mockGameRepository, mockUserRepository, mockOutputPacketGateway, new GameEndUtil());
    }

    @Test
    void validateReturnsCorrectErrorIfAlreadyBuiltOn() {
        String roadId = "road1";
        String ipAddress = "clientA";
        String playerBIpAddress = "clientB";

        Game game = mock(Game.class);
        when(game.getRoadsBuilt()).thenReturn(Map.of(roadId, playerBIpAddress));

        String error = roadActionHandler.validate(game, ipAddress, roadId);
        assertEquals(ROAD_ALREADY_BUILT_ON, error);
    }

    @Test
    void validateReturnsCorrectErrorIfPlayerHasNotEnoughWheels() {
        String roadId = "road1";
        String ipAddress = "clientA";

        Player player = new Player("PlayerA", null, 0, 7, 0);

        Game game = mock(Game.class);

        int requiredWheels = 8;
        Road toBuild = new Road(roadId, "city1", "city2", requiredWheels, WheelColor.RED);

        GameMap map = mock(GameMap.class);
        when(game.getMap()).thenReturn(map);
        when(map.getRoads()).thenReturn(Collections.singletonList(toBuild));
        when(game.getPlayers()).thenReturn(Map.of(ipAddress, player));

        String error = roadActionHandler.validate(game, ipAddress, roadId);
        assertEquals(NOT_ENOUGH_WHEELS_TO_BUILD_ROAD, error);
    }

    @Test
    void validateReturnsCorrectErrorIfPlayerHasNotEnoughCardsOfCorrectColor() {
        String roadId = "road1";
        String ipAddress = "clientA";

        List<WheelCard> wheelCards = Arrays.asList(
                new WheelCard(WheelColor.RED.ordinal() * 10),
                new WheelCard(WheelColor.GREEN.ordinal() * 10),
                new WheelCard(WheelColor.BLACK.ordinal() * 10)
        );
        Player player = new Player("PlayerA", null, 0, 20, 0, wheelCards, null, new ArrayList<>());

        Game game = mock(Game.class);

        int requiredWheels = 2;
        WheelColor color = WheelColor.RED;
        Road toBuild = new Road(roadId, "city1", "city2", requiredWheels, color);

        GameMap map = mock(GameMap.class);
        when(game.getMap()).thenReturn(map);
        when(map.getRoads()).thenReturn(Collections.singletonList(toBuild));
        when(game.getPlayers()).thenReturn(Map.of(ipAddress, player));


        String error = roadActionHandler.validate(game, ipAddress, roadId);
        assertEquals(NOT_ENOUGH_CARDS_OF_REQUIRED_COLOR_TO_BUILD_ROAD, error);
    }

    @Test
    void updateGameState() {
        String roadId = "road1";
        String ipAddress = "clientA";

        List<WheelCard> wheelCards = new ArrayList<>(Arrays.asList(
                new WheelCard(WheelColor.RED.ordinal() * 10),
                new WheelCard(WheelColor.RED.ordinal() * 10 + 1),
                new WheelCard(WheelColor.RED.ordinal() * 10 + 2),
                new WheelCard(WheelColor.GREEN.ordinal() * 10),
                new WheelCard(WheelColor.BLACK.ordinal() * 10)
        ));
        int score = 10;
        int wheelsRemaining = 20;
        Player player = new Player("PlayerA", null, score, wheelsRemaining, 0, wheelCards, null, new ArrayList<>());

        int requiredWheels = 2;
        WheelColor color = WheelColor.RED;
        Road toBuild = new Road(roadId, "city1", "city2", requiredWheels, color);

        GameMap map = mock(GameMap.class);
        when(map.getRoads()).thenReturn(Collections.singletonList(toBuild));

        Map<String, String> roadsBuilt = new HashMap<>();
        List<Activity> activities = new ArrayList<>();

        //TODO: test and implement if cards needed to build are re-added (randomly distributed to deck)

        Game game = new Game("game1", GameStatus.STARTED, map, new Date(),
                "creator", 3,
                Map.of(ipAddress, player), 4, roadsBuilt, activities, mock(DecksOfGame.class));

        // act
        roadActionHandler.updateGameState(game, ipAddress, roadId);

        // assert
        // check wheel cards removed from player
        List<Integer> expectedTakenWheelCards = Arrays.asList(WheelColor.RED.ordinal() * 10,
                WheelColor.RED.ordinal() * 10 + 1); // 2 reds
        List<Integer> playerRemainingWheelCardIds = player.getWheelCards().stream()
                .map(WheelCard::getCardID).toList();
        assertFalse(playerRemainingWheelCardIds.contains(expectedTakenWheelCards.get(0)));
        assertFalse(playerRemainingWheelCardIds.contains(expectedTakenWheelCards.get(1)));

        // more assertions
        assertEquals(wheelsRemaining - requiredWheels, player.getWheelsRemaining());
        assertEquals(ipAddress, game.getRoadsBuilt().get(roadId));
        assertEquals(score + GameConfig.scoreForRoadBuild(requiredWheels), player.getScore());
    }

    @Test
    void canEndGame() {
        assertTrue(roadActionHandler.canEndGame());
    }

    @Test
    void getCommand() {
        assertEquals(ROAD_BUILT, roadActionHandler.getActivityCommand());
    }
}