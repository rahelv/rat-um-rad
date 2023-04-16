package ch.progradler.rat_um_rad.server.models;

import ch.progradler.rat_um_rad.shared.models.game.Player;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {
    @Test
    void reachedRequiredPlayers() {
        int requiredPlayerCount = 2;
        Player player1 = new Player("Player1", null, 0, 0, 0);
        Map<String, Player> players = new HashMap<>();
        players.put("player1", player1);
        Game game = new Game("game1", null, null, null, requiredPlayerCount, players);

        assertFalse(game.hasReachedRequiredPlayers());

        Player player2 = new Player("Player1", null, 0, 0, 0);
        players.put("player2", player2);
        assertTrue(game.hasReachedRequiredPlayers());
    }
}