package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    OutputPacketGateway outputPacketGatewayMock;

    @Mock
    IUserRepository userRepositoryMock;

    private UserService userService;

    @BeforeEach
    public void initServiceTest() {
        userService = new UserService(outputPacketGatewayMock, userRepositoryMock);
    }

    @Test
    void handleNewUserAddsUsernameToRepository() {
        // prepare
        String username = "John";
        String ipAddress = "clientJ";

        doNothing().when(userRepositoryMock).addUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleNewUser(username, ipAddress);

        // assert
        verify(userRepositoryMock).addUsername(username, ipAddress);
    }

    @Test
    void handleNewUserBroadCastsThisInfoToAllClientsExceptThatUser() {
        // prepare
        String username = "John";
        String ipAddress = "clientJ";

        doNothing().when(userRepositoryMock).addUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.class));

        // execute
        userService.handleNewUser(username, ipAddress);

        // assert
        Packet packet = new Packet(Command.NEW_USER, username, ContentType.STRING);
        verify(outputPacketGatewayMock)
                .broadCast(packet, Collections.singletonList(ipAddress));
    }

    @Test
    void handleNewUserSendsConfirmationThatUser() {
        // prepare
        String username = "John";
        String ipAddress = "clientJ";

        doNothing().when(userRepositoryMock).addUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.class));

        // execute
        userService.handleNewUser(username, ipAddress);

        // assert
        Packet packet = new Packet(Command.USERNAME_CONFIRMED, new UsernameChange(username, username), ContentType.USERNAME_CHANGE);
        verify(outputPacketGatewayMock, atLeastOnce()).sendPacket(eq(ipAddress), eq(packet));
    }

    @Test
    void updateUsernameCallsUpdateUsernameOnRepository() {
        // prepare
        String username = "John";
        String ipAddress = "clientJ";

        doNothing().when(userRepositoryMock).updateUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.class));

        // execute
        userService.updateUsername(username, ipAddress);

        // assert
        verify(userRepositoryMock).updateUsername(username, ipAddress);
    }

    @Test
    void updateUsernameBroadCastsThisInfoToAllClientsExceptThatUser() {
        // prepare
        String oldUsername = "John";
        String newUsername = "Johnny";
        String ipAddress = "clientJ";

        when(userRepositoryMock.getUsername(ipAddress)).thenReturn(oldUsername);
        doNothing().when(userRepositoryMock).updateUsername(newUsername, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.class));

        // execute
        userService.updateUsername(newUsername, ipAddress);

        // assert
        Packet packet = new Packet(Command.CHANGED_USERNAME,
                new UsernameChange(oldUsername, newUsername),
                ContentType.USERNAME_CHANGE);
        verify(outputPacketGatewayMock)
                .broadCast(packet, Collections.singletonList(ipAddress));
    }

    @Test
    void updateUsernameSendsConfirmationThatUser() {
        // prepare
        String username = "John";
        String ipAddress = "clientJ";

        doNothing().when(userRepositoryMock).updateUsername(username, ipAddress);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));
        doNothing().when(outputPacketGatewayMock).sendPacket(eq(ipAddress), isA(Packet.class));

        // execute
        userService.updateUsername(username, ipAddress);

        // assert
        Packet packet = new Packet(Command.USERNAME_CONFIRMED, new UsernameChange(username, username), ContentType.USERNAME_CHANGE);
        verify(outputPacketGatewayMock, atLeastOnce()).sendPacket(eq(ipAddress), eq(packet));
    }


    @Test
    void handleDisconnectRemovesUsernameFromRepository() {
        // prepare
        String username = "John";
        String ipAddress = "clientJ";

        when(userRepositoryMock.removeUsername(ipAddress))
                .thenReturn(username);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleUserDisconnected(ipAddress);

        // assert
        verify(userRepositoryMock).removeUsername(ipAddress);
    }

    @Test
    void handleDisconnectBroadcastsThisInfo() {
        // prepare
        String username = "John";
        String ipAddress = "clientJ";

        when(userRepositoryMock.removeUsername(ipAddress))
                .thenReturn(username);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleUserDisconnected(ipAddress);

        // assert
        Packet packet = new Packet(Command.USER_DISCONNECTED, username, ContentType.STRING);
        verify(outputPacketGatewayMock)
                .broadCast(packet, Collections.singletonList(ipAddress));
    }

    @Test
    void handleMessageBroadCastsThisMessageWIthCorrectUsernameToAllClientsExceptThatUser() {
        // prepare
        String username = "John";
        String message = "Hi!";
        String ipAddress = "clientJ";

        when(userRepositoryMock.getUsername(ipAddress))
                .thenReturn(username);
        doNothing().when(outputPacketGatewayMock)
                .broadCast(isA(Packet.class), eq(Collections.singletonList(ipAddress)));

        // execute
        userService.handleMessageFromUser(message, ipAddress);

        // assert
        Packet packet = new Packet(Command.SEND_CHAT, new ChatMessage(username, message), ContentType.CHAT_MESSAGE);
        verify(outputPacketGatewayMock)
                .broadCast(packet, Collections.singletonList(ipAddress));
    }

    /**
     * checks if sugggested alternative is the next same username with number that is not already used
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
}
