package ch.progradler.rat_um_rad.server.services.action_handlers;

import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.Point;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import org.junit.jupiter.api.Test;

import java.util.*;

import static ch.progradler.rat_um_rad.shared.util.GameConfig.MAX_WHEELS_LEFT_TO_END_GAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameEndUtilTest {

    @Test
    void isGameEndedReturnsFalseIfNoPlayerHasLessThan6Wheels() {
        Game game = mock(Game.class);
        Player player1 = mock(Player.class);
        when(player1.getWheelsRemaining()).thenReturn(MAX_WHEELS_LEFT_TO_END_GAME + 1);

        Player player2 = mock(Player.class);
        when(player2.getWheelsRemaining()).thenReturn(MAX_WHEELS_LEFT_TO_END_GAME + 5);

        Map<String, Player> players = Map.of(
                "player1", player1, "player2", player2
        );
        when(game.getPlayers()).thenReturn(players);

        assertFalse(new GameEndUtil().isGameEnded(game));
    }

    @Test
    void isGameEndedReturnsTrueIfOnePlayerHasLessThan6Wheels() {
        Game game = mock(Game.class);
        Player player1 = mock(Player.class);
        when(player1.getWheelsRemaining()).thenReturn(MAX_WHEELS_LEFT_TO_END_GAME + 1);

        Player player2 = mock(Player.class);
        when(player2.getWheelsRemaining()).thenReturn(MAX_WHEELS_LEFT_TO_END_GAME);

        Map<String, Player> players = Map.of(
                "player1", player1, "player2", player2
        );
        when(game.getPlayers()).thenReturn(players);

        assertTrue(new GameEndUtil().isGameEnded(game));
    }

    @Test
    void updateScoresAndGetDestinationCardsResultWorksCorrectly() {
        String ip1 = "client1";
        String ip2 = "client2";

        //  1•---•2
        //  | \  |
        //  |  \ |
        // 4•---•3
        City city1 = new City("city1", "City1", new Point(0, 0));
        City city2 = new City("city2", "City2", new Point(0, 0));
        City city3 = new City("city3", "City3", new Point(0, 0));
        City city4 = new City("city4", "City4", new Point(0, 0));

        Road road12 = new Road(city1.getId(), city2.getId(), 3, WheelColor.WHITE);
        Road road23 = new Road(city2.getId(), city3.getId(), 3, WheelColor.WHITE);
        Road road34 = new Road(city3.getId(), city4.getId(), 3, WheelColor.WHITE);
        Road road41 = new Road(city4.getId(), city1.getId(), 3, WheelColor.WHITE);
        Road road13 = new Road(city1.getId(), city3.getId(), 3, WheelColor.WHITE);

        GameMap gameMap = new GameMap(Arrays.asList(city1, city2, city3, city4),
                Arrays.asList(road12, road23, road34, road41, road13));

        DecksOfGame decksOfGame = mock(DecksOfGame.class);

        // p1 connects 2 and 4
        // p2 connects 1 and 4 via 3
        Map<String, String> roadsBuilt = Map.of(
                road12.getId(), ip1,
                road41.getId(), ip1,
                road13.getId(), ip2,
                road34.getId(), ip2
        );

        int scorePlayer1 = 20;
        int scorePlayer2 = 30;

        // achieved
        DestinationCard p1ShortDestCard1 = new DestinationCard(city1, city2, 3);
        // not achieved
        DestinationCard p1ShortDestCard2 = new DestinationCard(city3, city4, 3);
        // achieved
        DestinationCard p1LongDestCard = new DestinationCard(city2, city4, 10);

        Player player1 = new Player("p1", PlayerColor.PINK, scorePlayer1, 4, 0,
                new ArrayList<>(), p1LongDestCard, Arrays.asList(p1ShortDestCard1, p1ShortDestCard2));

        // achieved
        DestinationCard p2ShortDestCard1 = new DestinationCard(city1, city3, 3);
        // achieved
        DestinationCard p2ShortDestCard2 = new DestinationCard(city3, city4, 2);
        // not achieved
        DestinationCard p2LongDestCard = new DestinationCard(city1, city2, 8);
        Player player2 = new Player("p2", PlayerColor.LILA, scorePlayer2, 6, 1,
                new ArrayList<>(), p2LongDestCard, Arrays.asList(p2ShortDestCard1, p2ShortDestCard2));


        Map<String, Player> players = Map.of(ip1, player1, ip2, player2);

        Game game = new Game("game1", GameStatus.STARTED, gameMap, new Date(), "creator",
                2, players, 20, roadsBuilt, new ArrayList<>(), decksOfGame);

        new GameEndUtil().updateScoresAndEndResult(game);

        int expectedScoredP1 = scorePlayer1 + p1ShortDestCard1.getPoints()
                - p1ShortDestCard2.getPoints()
                + p1LongDestCard.getPoints();
        assertEquals(expectedScoredP1, player1.getScore());
        PlayerEndResult result1 = player1.getEndResult();
        assertEquals(Collections.singletonList(p1ShortDestCard1), result1.getAchievedShorts());
        assertEquals(Collections.singletonList(p1ShortDestCard2), result1.getNotAchievedShorts());
        assertTrue(result1.hasAchievedLong());

        int expectedScoredP2 = scorePlayer2 + p2ShortDestCard1.getPoints()
                + p2ShortDestCard2.getPoints()
                - p2LongDestCard.getPoints();
        assertEquals(expectedScoredP2, player2.getScore());
        PlayerEndResult result2 = player2.getEndResult();
        assertEquals(Arrays.asList(p2ShortDestCard1, p2ShortDestCard2), result2.getAchievedShorts());
        assertTrue(result2.getNotAchievedShorts().isEmpty());
        assertFalse(result2.hasAchievedLong());
    }
}