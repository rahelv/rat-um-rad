package ch.progradler.rat_um_rad.server.services;

import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.server.repositories.IGameRepository;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.ErrorResponse;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link IUserService}
 */
public class UserService implements IUserService {
    public static final Logger LOGGER = LogManager.getLogger();

    private final OutputPacketGateway outputPacketGateway;
    private final IUserRepository userRepository;
    private final IGameRepository gameRepository;
    private final UsernameValidator usernameValidator;

    /**
     * Constructor for Testing purposes.
     *
     * @param outputPacketGateway
     * @param userRepository
     * @param gameRepository
     * @param usernameValidator
     */
    public UserService(OutputPacketGateway outputPacketGateway, IUserRepository userRepository, IGameRepository gameRepository, UsernameValidator usernameValidator) {
        this.outputPacketGateway = outputPacketGateway;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.usernameValidator = usernameValidator;
    }

    @Override
    public void handleNewUser(String username, String ipAddress) {
        LOGGER.info("Registering user " + ipAddress + ", " + username);
        String chosenUsername = checkUsernameAndSuggestAlternative(username);
        if (!usernameValidator.isUsernameValid(chosenUsername)) {
            Packet.Server errorPacket = new Packet.Server(
                    ServerCommand.INVALID_ACTION_FATAL, ErrorResponse.USERNAME_INVALID, ContentType.STRING);
            outputPacketGateway.sendPacket(ipAddress, errorPacket);
            return;
        }
        userRepository.addUsername(chosenUsername, ipAddress);
        Packet.Server confirmPacket = new Packet.Server(ServerCommand.USERNAME_CONFIRMED, new UsernameChange(username, chosenUsername), ContentType.USERNAME_CHANGE);
        outputPacketGateway.sendPacket(ipAddress, confirmPacket);
        Packet.Server broadCastPacket = new Packet.Server(ServerCommand.NEW_USER, chosenUsername, ContentType.STRING);
        //TODO: possibly send packet containing usernames of current logged in users?
        broadcastExcludingUser(broadCastPacket, ipAddress);

        //update online players on client
        List<String> listOfUsernames = userRepository.getAllUsernames();
        broadcastExcludingUser(new Packet.Server(ServerCommand.SEND_ALL_CONNECTED_PLAYERS, listOfUsernames, ContentType.STRING_LIST), ipAddress);
    }

    @Override
    public void updateUsername(String username, String ipAddress) {
        LOGGER.info("Updating username for ip" + ipAddress + ": " + username);
        String oldName = userRepository.getUsername(ipAddress);
        if (!usernameValidator.isUsernameValid(username)) {
            Packet.Server errorPacket = new Packet.Server(ServerCommand.INVALID_ACTION_WARNING, "The chosen username was invalid. Please try again", ContentType.STRING); //on client: user has to trigger usernamechange dialog again
            outputPacketGateway.sendPacket(ipAddress, errorPacket);
            return;
        }
        String chosenUsername = checkUsernameAndSuggestAlternative(username);
        userRepository.updateUsername(chosenUsername, ipAddress);
        Packet.Server confirmPacket = new Packet.Server(ServerCommand.USERNAME_CONFIRMED, new UsernameChange(username, chosenUsername), ContentType.USERNAME_CHANGE);
        outputPacketGateway.sendPacket(ipAddress, confirmPacket);
        Packet.Server broadCastPacket = new Packet.Server(ServerCommand.CHANGED_USERNAME,
                new UsernameChange(oldName, chosenUsername),
                ContentType.USERNAME_CHANGE);
        broadcastExcludingUser(broadCastPacket, ipAddress);
    }

    String checkUsernameAndSuggestAlternative(String username) {
        String usernameToBeChecked = username;
        int counter = 0;
        while (userRepository.hasDuplicate(usernameToBeChecked)) {
            counter++;
            usernameToBeChecked = username + counter;
        }
        return usernameToBeChecked;
    }

    @Override
    public void handleUserDisconnected(String ipAddress) {
        LOGGER.info("handling user socket disconnect for ip" + ipAddress);
        String username = userRepository.removeUsername(ipAddress);
        if (username == null) username = ipAddress;
        Packet.Server packet = new Packet.Server(ServerCommand.USER_DISCONNECTED, username, ContentType.STRING);
        broadcastExcludingUser(packet, ipAddress);

        //update online players on client
        List<String> listOfUsernames = userRepository.getAllUsernames();
        broadcastExcludingUser(new Packet.Server(ServerCommand.SEND_ALL_CONNECTED_PLAYERS, listOfUsernames, ContentType.STRING_LIST), ipAddress);
    }

    @Override
    public void handleBroadCastMessageFromUser(String message, String ipAddress) {
        LOGGER.info("Broadcasting message from" + ipAddress + ": " + message);
        String username = userRepository.getUsername(ipAddress);
        Packet.Server packet = new Packet.Server(ServerCommand.BROADCAST_CHAT_SENT, new ChatMessage(username, message), ContentType.CHAT_MESSAGE);
        broadcastExcludingUser(packet, ipAddress);
    }

    @Override
    public void handleGameInternalMessageFromUser(String message, String ipAddress) {
        Game currentPlayerGame = GameServiceUtil.getCurrentGameOfPlayer(ipAddress, gameRepository);
        if (currentPlayerGame == null) {
            LOGGER.info("Failed to send game internal message. User " + ipAddress + "is in no current game");
            return;
        }

        List<String> otherPlayers = new ArrayList<>(currentPlayerGame.getPlayerIpAddresses());
        otherPlayers.remove(ipAddress);

        LOGGER.info("Broadcasting game internal message from" + ipAddress + ": " + message);
        String username = userRepository.getUsername(ipAddress);
        Packet.Server packet = new Packet.Server(ServerCommand.GAME_INTERNAL_CHAT_SENT, new ChatMessage(username, message), ContentType.CHAT_MESSAGE);
        outputPacketGateway.broadCastOnly(packet, otherPlayers);
    }

    @Override
    public void handleWhisperMessageFromUser(String message, String toUsername, String ipAddress) {
        LOGGER.info("Broadcasting game internal message from" + ipAddress + ": " + message);
        String toIpAddress = userRepository.getIpAddress(toUsername);
        String senderName = userRepository.getUsername(ipAddress);
        Packet.Server packet = new Packet.Server(ServerCommand.WHISPER_CHAT_SENT, new ChatMessage(senderName, message), ContentType.CHAT_MESSAGE);
        outputPacketGateway.sendPacket(toIpAddress, packet);
    }

    private void broadcastExcludingUser(Packet.Server packet, String userIpAddress) {
        List<String> excludeFromBroadCast = Collections.singletonList(userIpAddress);
        outputPacketGateway.broadCastExclude(packet, excludeFromBroadCast);
    }

    @Override
    public void requestOnlinePlayers(String ipAddress) {
        LOGGER.info("Sending all online players to" + ipAddress);
        List<String> listOfUsernames = userRepository.getAllUsernames();
        outputPacketGateway.sendPacket(ipAddress, new Packet.Server(ServerCommand.SEND_ALL_CONNECTED_PLAYERS, listOfUsernames, ContentType.STRING_LIST));
    }
}
