package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link IUserService}
 */
public class UserService implements IUserService {
    private final OutputPacketGateway outputPacketGateway;
    private final IUserRepository userRepository;
    private UsernameValidator usernameValidator;

    public UserService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository) {
        this.outputPacketGateway = outputPacketGateway;
        this.userRepository = userRepository;
        this.usernameValidator = new UsernameValidator();
    }

    /**
     * Constructor for Testing purposes. TODO: decide if usernamevalidator should be given as parameter in normal constructor.
     *
     * @param outputPacketGateway
     * @param userRepository
     * @param usernameValidator
     */
    public UserService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository, UsernameValidator usernameValidator) {
        this.outputPacketGateway = outputPacketGateway;
        this.userRepository = userRepository;
        this.usernameValidator = usernameValidator;
    }

    @Override
    public void handleNewUser(String username, String ipAddress) {
        System.out.println("handleNewUser angekommen");
        String chosenUsername = checkUsernameAndSuggestAlternative(username);
        if (!usernameValidator.isUsernameValid(chosenUsername)) {
            Packet errorPacket = new Packet(Command.INVALID_ACTION_FATAL, "Username invalid. Please try again", ContentType.STRING); //TODO: on client, user has to enter username again
            outputPacketGateway.sendPacket(ipAddress, errorPacket);
            return;
        }
        userRepository.addUsername(chosenUsername, ipAddress);
        Packet confirmPacket = new Packet(Command.USERNAME_CONFIRMED, new UsernameChange(username, chosenUsername), ContentType.USERNAME_CHANGE); //TODO: change packet to UsernameChangeConfirmation
        outputPacketGateway.sendPacket(ipAddress, confirmPacket);
        Packet broadCastPacket = new Packet(Command.NEW_USER, chosenUsername, ContentType.STRING);
        //TODO: send packet containing usernames of current logged in users?
        broadcastExcludingUser(broadCastPacket, ipAddress);
    }

    @Override
    public void updateUsername(String username, String ipAddress) {
        String oldName = userRepository.getUsername(ipAddress);
        if (!usernameValidator.isUsernameValid(username)) {
            Packet errorPacket = new Packet(Command.INVALID_ACTION_WARNING, "The chosen username was invalid. Please try again", ContentType.STRING); //on client: user has to trigger usernamechange dialog again
            outputPacketGateway.sendPacket(ipAddress, errorPacket);
            return;
        }
        String chosenUsername = checkUsernameAndSuggestAlternative(username);
        userRepository.updateUsername(chosenUsername, ipAddress);
        Packet confirmPacket = new Packet(Command.USERNAME_CONFIRMED, new UsernameChange(username, chosenUsername), ContentType.USERNAME_CHANGE); //TODO: change packet to UsernameChangeConfirmation
        outputPacketGateway.sendPacket(ipAddress, confirmPacket);
        Packet broadCastPacket = new Packet(Command.CHANGED_USERNAME,
                new UsernameChange(oldName, chosenUsername),
                ContentType.USERNAME_CHANGE);
        broadcastExcludingUser(broadCastPacket, ipAddress);
    }

    String checkUsernameAndSuggestAlternative(String username) {
        String usernameToBeChecked = username;
        Integer counter = 0;
        while (userRepository.hasDuplicate(usernameToBeChecked)) {
            counter++;
            usernameToBeChecked = username + counter;
        }
        return usernameToBeChecked;
    }

    @Override
    public void handleUserDisconnected(String ipAddress) {
        String username = userRepository.removeUsername(ipAddress);
        if (username == null) username = ipAddress;
        Packet packet = new Packet(Command.USER_DISCONNECTED, username, ContentType.STRING);
        broadcastExcludingUser(packet, ipAddress);
    }

    @Override
    public void handleMessageFromUser(String message, String ipAddress) {
        String username = userRepository.getUsername(ipAddress);
        Packet packet = new Packet(Command.SEND_CHAT, new ChatMessage(username, message), ContentType.CHAT_MESSAGE);
        broadcastExcludingUser(packet, ipAddress);
    }

    private void broadcastExcludingUser(Packet packet, String userIpAddress) {
        List<String> excludeFromBroadCast = Collections.singletonList(userIpAddress);
        outputPacketGateway.broadCast(packet, excludeFromBroadCast);
    }
}
