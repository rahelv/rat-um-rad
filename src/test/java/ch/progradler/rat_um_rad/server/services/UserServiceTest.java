package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.WheelColor;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    OutputPacketGateway outputPacketGatewayMock;

    @Mock
    IUserRepository userRepositoryMock;

    @Mock
    IGameRepository gameRepositoryMock;

    @Mock
    UsernameValidator usernameValidatorMock;

    private UserService userService;

    @BeforeEach
    public void initServiceTest() {
        userService = new UserService(outputPacketGatewayMock, userRepositoryMock, gameRepositoryMock, usernameValidatorMock);
    }

    @Test
    void handleNewUserAddsUsernameToRepository() {
        // prepare
        String username = "Johnny";
        String ipAddress = "clientJ";

        when(usernameValidatorMock.isUsernameValid(username)).thenReturn(true);
        doNothing().when(userRepositoryMock).addUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleNewUser(username, ipAddress);

        // assert
        verify(userRepositoryMock).addUsername(username, ipAddress);
    }

    @Test
    void handleNewUserSendsErrorMessageWhenNameInvalid() {
        // prepare
        String username = "5Johnny";
        String ipAddress = "clientJ";
        Packet.Server errorPacket = new Packet.Server(ServerCommand.INVALID_ACTION_FATAL, ErrorResponse.USERNAME_INVALID, ContentType.STRING);
        when(usernameValidatorMock.isUsernameValid(username)).thenReturn(false);
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), eq(errorPacket));

        // execute
        userService.handleNewUser(username, ipAddress);

        // assert
        verify(outputPacketGatewayMock, atLeastOnce()).sendPacket(eq(ipAddress), eq(errorPacket));
    }

    @Test
    void handleNewUserBroadCastsThisInfoToAllClientsExceptThatUser() {
        // prepare
        String username = "Johnny";
        String ipAddress = "clientJ";

        when(usernameValidatorMock.isUsernameValid(username)).thenReturn(true);
        doNothing().when(userRepositoryMock).addUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.Server.class));

        // execute
        userService.handleNewUser(username, ipAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.NEW_USER, username, ContentType.STRING);
        verify(outputPacketGatewayMock)
                .broadCastExclude(packet, Collections.singletonList(ipAddress));
    }

    @Test
    void handleNewUserSendsConfirmationThatUser() {
        // prepare
        String username = "Johnny";
        String ipAddress = "clientJ";

        when(usernameValidatorMock.isUsernameValid(username)).thenReturn(true);
        doNothing().when(userRepositoryMock).addUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.Server.class));

        // execute
        userService.handleNewUser(username, ipAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.USERNAME_CONFIRMED, new UsernameChange(username, username), ContentType.USERNAME_CHANGE);
        verify(outputPacketGatewayMock, atLeastOnce()).sendPacket(eq(ipAddress), eq(packet));
    }

    @Test
    void updateUsernameCallsUpdateUsernameOnRepository() {
        // prepare
        String username = "Johnny";
        String ipAddress = "clientJ";

        when(usernameValidatorMock.isUsernameValid(username)).thenReturn(true);
        doNothing().when(userRepositoryMock).updateUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.Server.class));

        // execute
        userService.updateUsername(username, ipAddress);

        // assert
        verify(userRepositoryMock).updateUsername(username, ipAddress);
    }

    @Test
    void updateUsernameSendsErrorMessageWhenNameInvalid() {
        // prepare
        String username = "5Johnny";
        String ipAddress = "clientJ";
        Packet.Server errorPacket = new Packet.Server(ServerCommand.INVALID_ACTION_WARNING, "The chosen username was invalid. Please try again", ContentType.STRING); //on client: user has to trigger usernamechange dialog again
        when(usernameValidatorMock.isUsernameValid(username)).thenReturn(false);
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), eq(errorPacket));

        // execute
        userService.updateUsername(username, ipAddress);

        // assert
        verify(outputPacketGatewayMock, atLeastOnce()).sendPacket(eq(ipAddress), eq(errorPacket));
    }

    @Test
    void updateUsernameBroadCastsThisInfoToAllClientsExceptThatUser() {
        // prepare
        String oldUsername = "Johnny";
        String newUsername = "Johnny5";
        String ipAddress = "clientJ";

        when(usernameValidatorMock.isUsernameValid(newUsername)).thenReturn(true);
        when(userRepositoryMock.getUsername(ipAddress)).thenReturn(oldUsername);
        doNothing().when(userRepositoryMock).updateUsername(newUsername, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.Server.class));

        // execute
        userService.updateUsername(newUsername, ipAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.CHANGED_USERNAME,
                new UsernameChange(oldUsername, newUsername),
                ContentType.USERNAME_CHANGE);
        verify(outputPacketGatewayMock)
                .broadCastExclude(packet, Collections.singletonList(ipAddress));
    }

    @Test
    void updateUsernameSendsConfirmationThatUser() {
        // prepare
        String username = "Johnny";
        String ipAddress = "clientJ";

        when(usernameValidatorMock.isUsernameValid(username)).thenReturn(true);
        doNothing().when(userRepositoryMock).updateUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.Server.class));

        // execute
        userService.updateUsername(username, ipAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.USERNAME_CONFIRMED, new UsernameChange(username, username), ContentType.USERNAME_CHANGE);
        verify(outputPacketGatewayMock, atLeastOnce()).sendPacket(eq(ipAddress), eq(packet));
    }


    @Test
    void handleDisconnectRemovesUsernameFromRepository() {
        // prepare
        String username = "Johnny";
        String ipAddress = "clientJ";

        when(userRepositoryMock.removeUsername(ipAddress))
                .thenReturn(username);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleUserDisconnected(ipAddress);

        // assert
        verify(userRepositoryMock).removeUsername(ipAddress);
    }

    @Test
    void handleDisconnectBroadcastsThisInfo() {
        // prepare
        String username = "Johnny";
        String ipAddress = "clientJ";

        when(userRepositoryMock.removeUsername(ipAddress))
                .thenReturn(username);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleUserDisconnected(ipAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.USER_DISCONNECTED, username, ContentType.STRING);
        verify(outputPacketGatewayMock)
                .broadCastExclude(packet, Collections.singletonList(ipAddress));
    }

    @Test
    void handleBroadCastMessageBroadCastsThisMessageWIthCorrectUsernameToAllClientsExceptThatUser() {
        // prepare
        String username = "Johnny";
        String message = "Hi!";
        String ipAddress = "clientJ";

        when(userRepositoryMock.getUsername(ipAddress))
                .thenReturn(username);
        doNothing().when(outputPacketGatewayMock)
                .broadCastExclude(isA(Packet.Server.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleBroadCastMessageFromUser(message, ipAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.BROADCAST_CHAT_SENT, new ChatMessage(username, message), ContentType.CHAT_MESSAGE);
        verify(outputPacketGatewayMock)
                .broadCastExclude(packet, Collections.singletonList(ipAddress));
    }

    @Test
    void handleGameInternalMessageBroadCastsThisMessageWIthCorrectUsernameToAllPlayersInCurrentGameOfPlayerExceptTharPlayer() {
        // prepare
        String username = "Johnny";
        String message = "Hi!";
        String ipAddress = "clientJ";

        String ipAddressPlayerB = "clientB";
        String ipAddressPlayerC = "clientC";

        Player playerA = new Player("player A", WheelColor.RED, 100, 10, 2);
        Player playerB = new Player("player B", WheelColor.BLUE, 50, 15, 1);
        Player playerC = new Player("player C", WheelColor.PINK, 50, 15, 1);
        Map<String, Player> players = Map.of(
                ipAddress, playerA,
                ipAddressPlayerB, playerB,
                ipAddressPlayerC, playerC
        );

        Game game1 = new Game("gameA", null, GameMap.defaultMap(), null, "playerB", 4, players, 0, new HashMap<>(), new ArrayList<>());

        when(gameRepositoryMock.getAllGames()).thenReturn(Collections.singletonList(game1));

        when(userRepositoryMock.getUsername(ipAddress))
                .thenReturn(username);

        // execute
        userService.handleGameInternalMessageFromUser(message, ipAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.GAME_INTERNAL_CHAT_SENT, new ChatMessage(username, message), ContentType.CHAT_MESSAGE);
        ArgumentCaptor<List<String>> clientsArgCaptor = ArgumentCaptor.forClass(List.class);
        verify(outputPacketGatewayMock)
                .broadCastOnly(eq(packet), clientsArgCaptor.capture());
        List<String> clients = clientsArgCaptor.getValue();
        List<String> expectedClients = Arrays.asList(ipAddressPlayerB, ipAddressPlayerC);
        Collections.sort(expectedClients);
        Collections.sort(clients);
        assertEquals(expectedClients, clients);
    }

    @Test
    void handleGameInternalMessageDoesNotBroadCastsThisMessageIfPlayerInNoGameYet() {
        // prepare
        String message = "Hi!";
        String ipAddress = "clientJ";

        Map<String, Player> players = Map.of();
        Game game1 = new Game("gameA", null, GameMap.defaultMap(), null, "playerB", 4, players, 0, new HashMap<>(), new ArrayList<>());

        when(gameRepositoryMock.getAllGames()).thenReturn(Collections.singletonList(game1));

        // execute
        userService.handleGameInternalMessageFromUser(message, ipAddress);

        // assert
        verifyNoInteractions(outputPacketGatewayMock);
    }

    @Test
    void handleWhisperMessageSendsThisMessageWIthCorrectUsernameToUserWithThatUsername() {
        // prepare
        String senderName = "Sendername";
        String toName = "to name";
        String message = "Hi!";
        String senderIpAddress = "clientA";
        String toIpAddress = "clientB";

        when(userRepositoryMock.getUsername(senderIpAddress))
                .thenReturn(senderName);
        when(userRepositoryMock.getIpAddress(toName))
                .thenReturn(toIpAddress);

        // execute
        userService.handleWhisperMessageFromUser(message, toName, senderIpAddress);

        // assert
        Packet.Server packet = new Packet.Server(ServerCommand.WHISPER_CHAT_SENT, new ChatMessage(senderName, message), ContentType.CHAT_MESSAGE);
        verify(outputPacketGatewayMock).sendPacket(toIpAddress, packet);
    }

    /**
     * checks if suggested alternative is the next same username with number that is not already used
     */
    @Test
    void checkUsernameAndSuggestAlternative() {
        when(userRepositoryMock.hasDuplicate("rahel")).thenReturn(true);
        when(userRepositoryMock.hasDuplicate("rahel1")).thenReturn(true);
        when(userRepositoryMock.hasDuplicate("rahel2")).thenReturn(true);
        when(userRepositoryMock.hasDuplicate("rahel3")).thenReturn(true);
        when(userRepositoryMock.hasDuplicate("rahel4")).thenReturn(true);

        assertEquals("rahel5", userService.checkUsernameAndSuggestAlternative("rahel"));
    }

    @Test
    void requestOnlinePlayersTest() {
        List<String> names = new LinkedList<>();
        names.add("name1");
        names.add("name2");
        when(userRepositoryMock.getAllUsernames()).thenReturn(names);

        String ipAddress = "ipAddressA";
        Packet.Server packet = new Packet.Server(ServerCommand.SEND_ALL_CONNECTED_PLAYERS, names, ContentType.STRING_LIST);

        userService.requestOnlinePlayers(ipAddress);
        verify(outputPacketGatewayMock).sendPacket(ipAddress, packet);
    }

    @Test
    void requestOnlinePlayersWithEmptyNamelistTest() {
        List<String> names = new LinkedList<>();
        when(userRepositoryMock.getAllUsernames()).thenReturn(names);

        String ipAddress = "ipAddressA";
        Packet.Server packet = new Packet.Server(ServerCommand.SEND_ALL_CONNECTED_PLAYERS, names, ContentType.STRING_LIST);

        userService.requestOnlinePlayers(ipAddress);
        verify(outputPacketGatewayMock).sendPacket(ipAddress, packet);
    }
}
