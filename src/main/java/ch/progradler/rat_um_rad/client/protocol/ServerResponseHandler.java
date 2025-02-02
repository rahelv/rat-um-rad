package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.Highscore;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static ch.progradler.rat_um_rad.shared.protocol.ServerCommand.PING;

/**
 * Handles incoming responses from server.
 */
public class ServerResponseHandler implements ServerInputPacketGateway {
    public static final Logger LOGGER = LogManager.getLogger();

    private final List<ServerResponseListener<?>> listeners = new ArrayList<>();
    private final ClientPingPongRunner clientPingPongRunner;

    public ServerResponseHandler(ClientPingPongRunner clientPingPongRunner) {
        this.clientPingPongRunner = clientPingPongRunner;
    }

    @Override
    public void addListener(ServerResponseListener<?> listenerToAdd) {
        this.listeners.add(listenerToAdd);
    }

    /**
     * Receives {@link Packet} from {@link ServerInputListener} sent by client. This is the implementation of the protocol.
     * <p>
     * For more information on the protocol, read the corresponding document or read the javadoc of the
     * commands in the switch cases or the used methods in the code block for each case.
     */
    @Override
    public void handleResponse(Packet<ServerCommand> packet) {
        if (packet.getCommand() != PING)
            LOGGER.info("Received server command:  " + packet.getCommand());
        //TODO: implement QUIT command and other commands

        switch (packet.getCommand()) {
            case PING -> {
                clientPingPongRunner.pingArrived();
            }
            case USERNAME_CONFIRMED -> {
                UsernameChange change = (UsernameChange) packet.getContent();
                notifyListenersOfType(change, packet.getCommand());
            }
            case INVALID_ACTION_FATAL -> {
                String error = (String) packet.getContent();
                notifyListenersOfType(error, ServerCommand.INVALID_ACTION_FATAL);
            }
            case SEND_ALL_CONNECTED_PLAYERS -> {
                List<String> allOnlinePlayers = (List<String>) packet.getContent();
                notifyListenersOfType(allOnlinePlayers, packet.getCommand());
            }
            case GAME_INTERNAL_CHAT_SENT -> {
                //TODO: update chatRoomModel
                ChatMessage message = (ChatMessage) packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType(message, packet.getCommand());
            }
            case WHISPER_CHAT_SENT -> {
                ChatMessage whisperMessage = (ChatMessage) packet.getContent();
                notifyListenersOfType(whisperMessage, packet.getCommand());
            }
            case BROADCAST_CHAT_SENT -> {
                ChatMessage broadcastMessage = (ChatMessage) packet.getContent();
                notifyListenersOfType(broadcastMessage, packet.getCommand());
            }
            case SEND_WAITING_GAMES, SEND_STARTED_GAMES, SEND_FINISHED_GAMES -> {
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType((List<GameBase>) content, packet.getCommand());
            }
            case NEW_USER -> {
                String content = (String) packet.getContent();
            }
            case GAME_CREATED -> {
                ClientGame content = (ClientGame) packet.getContent();
                notifyListenersOfType(content, ServerCommand.GAME_CREATED);
            }
            case GAME_JOINED -> {
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, ServerCommand.GAME_JOINED);
            }
            case NEW_PLAYER, GAME_UPDATED, ROAD_BUILT, WHEEL_CARDS_CHOSEN -> { //TODO: what happens if game is updated the same time you're doing something (can this even happen?)
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, ServerCommand.GAME_UPDATED); //updated ClientGame is sent to Controller, so it can display the new state
            }
            case GAME_STARTED_SELECT_DESTINATION_CARDS -> {
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType(content, ServerCommand.GAME_STARTED_SELECT_DESTINATION_CARDS);
            }
            case REQUEST_SHORT_DESTINATION_CARDS_RESULT -> {
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType(content, ServerCommand.REQUEST_SHORT_DESTINATION_CARDS_RESULT);
            }
            case DESTINATION_CARDS_SELECTED -> {
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, ServerCommand.DESTINATION_CARDS_SELECTED);
            }
            case GAME_ENDED -> {
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, ServerCommand.GAME_ENDED);
            }
            case SEND_HIGHSCORES -> {
                List<Highscore> highScores = (List<Highscore>) packet.getContent();
                notifyListenersOfType(highScores, ServerCommand.SEND_HIGHSCORES);
            }
            case GAME_ENDED_BY_PLAYER_DISCONNECTION -> {
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, ServerCommand.GAME_ENDED_BY_PLAYER_DISCONNECTION);
            }
            default -> {
                LOGGER.info("A packet with content " + packet.getContent() + " arrived in Server without matching any Client-Commands.");
                //Printing so that developers can see it.
                System.out.println(packet.getContent());
            }
        }
    }

    private <T> void notifyListenersOfType(T event, ServerCommand command) {
        for (ServerResponseListener<?> listener : listeners) {
            if (listener.forCommand() == command) {
                ((ServerResponseListener<T>) listener).serverResponseReceived(event);
            }
        }
    }
}
