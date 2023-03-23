package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link IUserService}
 */
public class UserService implements IUserService {
    private final OutputPacketGateway outputPacketGateway;
    private final IUserRepository userRepository;

    public UserService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository) {
        this.outputPacketGateway = outputPacketGateway;
        this.userRepository = userRepository;
    }

    @Override
    public void handleNewUser(String username, String ipAddress) {
        userRepository.addUsername(username, ipAddress);
        Packet confirmPacket = new Packet(Command.USERNAME_CONFIRMED, username, ContentType.USERNAME);
        outputPacketGateway.sendMessage(ipAddress, confirmPacket);
        Packet broadCastPacket = new Packet(Command.NEW_USER, username, ContentType.USERNAME);
        broadcastExcludingUser(broadCastPacket, ipAddress);
    }

    @Override
    public void updateUsername(String username, String ipAddress) {
        String oldName = userRepository.getUsername(ipAddress);
        userRepository.updateUsername(username, ipAddress);
        Packet confirmPacket = new Packet(Command.USERNAME_CONFIRMED, username, ContentType.USERNAME);
        outputPacketGateway.sendMessage(ipAddress, confirmPacket);
        Packet broadCastPacket = new Packet(Command.CHANGED_USERNAME,
                new UsernameChange(oldName, username),
                ContentType.USERNAME_CHANGE);
        broadcastExcludingUser(broadCastPacket, ipAddress);
    }

    @Override
    public void handleUserDisconnected(String ipAddress) {
        String username = userRepository.removeUsername(ipAddress);
        if (username == null) username = ipAddress;
        Packet packet = new Packet(Command.USER_DISCONNECTED, username, ContentType.USERNAME);
        broadcastExcludingUser(packet, ipAddress);
    }

    @Override
    public void handleMessageFromUser(ChatMessage message, String ipAddress) {
        Packet packet = new Packet(Command.SEND_CHAT, message, ContentType.CHAT_MESSAGE);
        broadcastExcludingUser(packet, ipAddress);
    }

    private void broadcastExcludingUser(Packet packet, String userIpAddress) {
        List<String> excludeFromBroadCast = Collections.singletonList(userIpAddress);
        outputPacketGateway.broadCast(packet, excludeFromBroadCast);
    }
}
