package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.gui.javafx.game.activity.ActivityController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomController;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyController;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles incoming responses from server.
 */
public class ServerResponseHandler implements ServerInputPacketGateway {
    private final List<ServerResponseListener> listeners = new ArrayList<>();
    private final PackagePresenter presenter;
    private final ClientPingPongRunner clientPingPongRunner;

    @Override
    public void addListener(ServerResponseListener listenerToAdd) {
        this.listeners.add(listenerToAdd);
    }

    public ServerResponseHandler(PackagePresenter presenter, ClientPingPongRunner clientPingPongRunner) {
        this.presenter = presenter;
        this.clientPingPongRunner = clientPingPongRunner;
    }

    /**
     * Receives {@link Packet} from {@link ServerInputListener} sent by client. This is the implementation of the protocol.
     * <p>
     * For more information on the protocol, read the corresponding document or read the javadoc of the
     * commands in the switch cases or the used methods in the code block for each case.
     */
    @Override
    public void handleResponse(Packet packet) {
        //TODO: implement QUIT command and other commands

        switch (packet.getCommand()) {
            case PING -> {
                clientPingPongRunner.pingArrived();
            }
            case USERNAME_CONFIRMED -> {
                UsernameChange change = (UsernameChange) packet.getContent();
                notifyUsernameChangeListeners(change);
            }
            case INVALID_ACTION_FATAL -> {
                //TODO: differentiate further between fatal actions
                //this.userService.chooseAndSendUsername(this.serverOutput);
            }
            case SEND_ALL_CONNECTED_PLAYERS -> {
                //TODO: implement
            }
            case SEND_BROADCAST_CHAT -> {
                //TODO: update chatRoomModel
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyChatMessageListeners((ChatMessage) content);
            }
            case SEND_GAMES -> { //TODO: handle list of games received
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                if(contentType == ContentType.GAME_INFO_LIST) {
                    notifyGameListeners((GameBase) content);
                }
            }
            case NEW_USER -> {
                String content = (String) packet.getContent();
                notifyActivityListeners(content + " entered the game");
            }
            default -> presenter.display(packet);
            //TODO: send Activity to ActivityController when ein Spielzug passiert
        }
    }
    private void notifyUsernameChangeListeners(UsernameChange change) {
        for(ServerResponseListener<UsernameChange> listener : listeners) {
            if(listener instanceof UsernameChangeController) {
                listener.serverResponseReceived(change);
            }
        }
    }

    private void notifyChatMessageListeners(ChatMessage message) {
        for (ServerResponseListener<ChatMessage> listener : listeners) {
            if (listener instanceof ChatRoomController) {
                listener.serverResponseReceived(message);
            }
        }
    }

    private void notifyGameListeners(GameBase content) {
        for(ServerResponseListener<GameBase> listener: listeners) { //TODO: find a better way to handle the listeners of different types
            if(listener instanceof LobbyController) {
                listener.serverResponseReceived(content);
            }
        }
    }

    private void notifyActivityListeners(String activity) {
        for(ServerResponseListener listener: listeners) {
            if(listener instanceof ActivityController) {
                listener.serverResponseReceived(activity);
            }
        }
    }
}
