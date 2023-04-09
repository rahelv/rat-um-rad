package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DecksOfGame;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCardDeck;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelCardDeck;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock
    OutputPacketGateway mockOutputPacketGateway;
    @Mock
    IGameRepository mockGameRepository;
    @Mock
    IUserRepository mockUserRepository;

    GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(mockOutputPacketGateway, mockGameRepository, mockUserRepository);
    }

    @Test
    void createGameCreatesNewGameInstance() throws IGameRepository.DuplicateIdException {
        String creator = "clientA";
        int requiredPlayers = 4;

        // act
        gameService.createGame(creator, requiredPlayers);

        // assert
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(mockGameRepository).addGame(gameCaptor.capture());

        Game game = gameCaptor.getValue();
        DecksOfGame decks = game.getDecksOfGame();

        boolean createdAtIsJustNow = ((new Date()).getTime() -
                game.getCreatedAt().getTime()) <= 100; // created at less than 50 milliseconds ago;

        assertTrue(createdAtIsJustNow);
        assertTrue(game.getId().length() >= 10);
        assertEquals(creator, game.getCreatorPlayerIpAddress());
        assertEquals(requiredPlayers, game.getRequiredPlayerCount());
        assertSame(game.getStatus(), GameStatus.WAITING_FOR_PLAYERS);
        assertTrue(decks.getDiscardDeck().getDeckOfCards().isEmpty());
        assertArrayEquals(
                decks.getWheelCardDeck().getDeckOfCards().toArray(),
                WheelCardDeck.full().getDeckOfCards().toArray());
        assertArrayEquals(
                decks.getLongDestinationCardDeck().getCardDeck().toArray(),
                DestinationCardDeck.longDestinations().getCardDeck().toArray());
        assertArrayEquals(
                decks.getShortDestinationCardDeck().getCardDeck().toArray(),
                DestinationCardDeck.shortDestinations().getCardDeck().toArray());
    }

    @Test
    void createGameRetriesWithOtherIdUntilNoErrorThrown() throws IGameRepository.DuplicateIdException {
        String creator = "clientA";
        int requiredPlayers = 4;

        // throw, throw, do not throw
        boolean[] repoShouldThrow = new boolean[]{true, true, false};
        final int[] mockIndex = {0};

        doThrow(IGameRepository.DuplicateIdException.class)
                .when(mockGameRepository).addGame(argThat((g) -> {
            mockIndex[0]++;
            return repoShouldThrow[mockIndex[0] - 1];
        }));
        gameService.createGame(creator, requiredPlayers);

        verify(mockGameRepository, times(3)).addGame(isA(Game.class));
    }

    @Test
    void createGameAddsCreatorPlayer() throws IGameRepository.DuplicateIdException {
        String creatorIp = "clientA";
        String creatorName = "John";

        when(mockUserRepository.getUsername(creatorIp)).thenReturn(creatorName);

        gameService.createGame(creatorIp, 4);

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(mockGameRepository).addGame(gameCaptor.capture());

        Game game = gameCaptor.getValue();
        assertEquals(1, game.getPlayers().size());
        Player creator = game.getPlayers().get(creatorIp);
        Player expected = GameServiceUtil.createNewPlayer(creatorIp, mockUserRepository);
        expected.setColor(creator.getColor()); // color is randomly generated so has to be the same

        assertEquals(expected, creator);
    }

    @Test
    void createGameSendsGameInstanceToCreator() throws IGameRepository.DuplicateIdException {
        String creatorIp = "clientA";

        // act
        gameService.createGame(creatorIp, 4);

        // assert
        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(mockGameRepository).addGame(gameCaptor.capture());
        Game createdGame = gameCaptor.getValue();

        ClientGame sentGame = GameServiceUtil.toClientGame(createdGame, creatorIp);//new ClientGame(createdGame.getId())
        Packet packet = new Packet(Command.GAME_CREATED, sentGame, ContentType.GAME);
        verify(mockOutputPacketGateway).sendPacket(creatorIp, packet);
    }

    @Test
    void handlesWaitingGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getWaitingGames()).thenReturn(null);

        gameService.getWaitingGames(ipAddress);

        List<Game> waitingGames = verify(mockGameRepository).getWaitingGames();
        Packet packet = new Packet(Command.SEND_GAMES, waitingGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesStartedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getStartedGames()).thenReturn(null);

        gameService.getStartedGames(ipAddress);

        List<Game> startedGames = verify(mockGameRepository).getStartedGames();
        Packet packet = new Packet(Command.SEND_GAMES, startedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }

    @Test
    void handlesFinishedGamesRequest() {
        String ipAddress = "ipAddressA";
        when(mockGameRepository.getFinishedGames()).thenReturn(null);

        gameService.getFinishedGames(ipAddress);

        List<Game> finishedGames = verify(mockGameRepository).getFinishedGames();
        Packet packet = new Packet(Command.SEND_GAMES, finishedGames, ContentType.GAME_INFO_LIST);
        verify(mockOutputPacketGateway).sendPacket(ipAddress, packet);
    }
}