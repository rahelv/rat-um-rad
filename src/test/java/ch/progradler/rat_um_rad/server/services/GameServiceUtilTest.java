package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.client.models.VisiblePlayer;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCard;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.util.GameConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceUtilTest {

    @Mock
    IUserRepository mockUserRepository;

    @Test
    void toClientGame() {
        String gameId = "gameA";
        GameStatus status = GameStatus.STARTED;
        GameMap map = GameMap.defaultMap();
        Date createdAt = new Date(2023, Calendar.JUNE, 3);
        String creatorIp = "clientCreator";
        int requiredPlayerCount = 5;
        int turn = 20;

        Player creator = new Player("player A", WheelColor.RED, 100, 10, 2);
        Player otherPlayer = new Player("player B", WheelColor.BLUE, 50, 15, 1);
        String otherPlayerIp = "clientB";
        Map<String, Player> players = Map.of(
                creatorIp, creator,
                otherPlayerIp, otherPlayer
        );

        Game game = new Game(gameId, status, map, createdAt, creatorIp, requiredPlayerCount, players, turn);
        String forPlayerIp = otherPlayerIp;

        ClientGame expected = new ClientGame(gameId,
                status,
                map,
                createdAt,
                creatorIp,
                requiredPlayerCount,
                Collections.singletonList(GameServiceUtil.toVisiblePlayer(creator, creatorIp)),
                otherPlayer,
                turn);

        assertEquals(expected, GameServiceUtil.toClientGame(game, forPlayerIp));
    }

    @Test
    void createNewPlayer() {
        String ipAddress = "clientA";
        String name = "John";

        when(mockUserRepository.getUsername(ipAddress)).thenReturn(name);

        Player player = GameServiceUtil.createNewPlayer(ipAddress, mockUserRepository);
        assertEquals(name, player.getName());
        assertNotNull(player.getColor());
        assertEquals(0, player.getScore());
        assertEquals(0, player.getPlayingOrder());
        assertEquals(GameConfig.STARTING_WHEELS_PER_PLAYER, player.getWheelsRemaining());
        assertNull(player.getLongDestinationCard());
        assertTrue(player.getShortDestinationCards().isEmpty());
        assertTrue(player.getWheelCards().isEmpty());
    }

    @Test
    void toVisiblePlayer() {
        String ipAddress = "playerA";
        Player player = new Player("John", WheelColor.WHITE, 30, 20, 3,
                Collections.singletonList(new WheelCard(3)), null, new ArrayList<>());

        VisiblePlayer expected = new VisiblePlayer(player.getName(), player.getColor(),
                player.getScore(), player.getWheelsRemaining(), ipAddress,
                player.getWheelCards().size(),
                player.getShortDestinationCards().size(),
                player.getPlayingOrder());

        assertEquals(expected, GameServiceUtil.toVisiblePlayer(player, ipAddress));
    }
}