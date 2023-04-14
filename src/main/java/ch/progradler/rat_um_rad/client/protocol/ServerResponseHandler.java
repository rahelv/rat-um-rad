package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomModel;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.ShowAllGamesController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyController;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles incoming responses from server.
 */
public class ServerResponseHandler implements ServerInputPacketGateway {
    private final List<ServerResponseListener<?>> listeners = new ArrayList<>();
    private final PackagePresenter presenter;
    private final ClientPingPongRunner clientPingPongRunner;

    public ServerResponseHandler(PackagePresenter presenter, ClientPingPongRunner clientPingPongRunner) {
        this.presenter = presenter;
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
    public void handleResponse(Packet packet) {
        //TODO: implement QUIT command and other commands

        switch (packet.getCommand()) {
            case PING -> {
                clientPingPongRunner.pingArrived();
            }
            case USERNAME_CONFIRMED -> {
                UsernameChange change = (UsernameChange) packet.getContent();
                notifyListenersOfType(change, UsernameChangeController.class, packet.getContentType());
            }
            case INVALID_ACTION_FATAL -> {
                //TODO: differentiate further between fatal actions
                //this.userService.chooseAndSendUsername(this.serverOutput);
            }
            case SEND_ALL_CONNECTED_PLAYERS -> {
                //TODO: implement
                List<String> allOnlinePlayers = (List<String>)packet.getContent();
                notifyListenersOfType(allOnlinePlayers, ChatRoomController.AllPlayersListener.class, packet.getContentType());
                notifyListenersOfType(allOnlinePlayers,LobbyController.AllOnlinePlayersListener.class, packet.getContentType());
            }
            case SEND_BROADCAST_CHAT -> {
                //TODO: update chatRoomModel
                ChatMessage message = (ChatMessage) packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType(message, ChatRoomController.class, packet.getContentType());
            }
            case SEND_WHISPER_CHAT -> {
                ChatMessage message = (ChatMessage)packet.getContent();
                notifyListenersOfType(message, ChatRoomController.class,packet.getContentType());
            }
            case SEND_GAMES -> {
                System.out.println("sendgames " + packet);
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                if(contentType == ContentType.GAME_INFO_LIST) {
                    // TODO: Lobby should only get list when it's calling for it
                    notifyListenersOfType((List<GameBase>) content, LobbyController.class, packet.getContentType());
                    notifyListenersOfType((List<GameBase>) content, ShowAllGamesController.class, packet.getContentType());
                } else {
                    notifyListenersOfType((List<GameBase>) content, ShowAllGamesController.class, packet.getContentType());
                }
            }
            case NEW_USER -> {
                String content = (String) packet.getContent();
               // notifyListenersOfType(content + " entered the game", ActivityController.class, packet.getContentType());
            }
            case GAME_CREATED -> {
                ClientGame content = (ClientGame) packet.getContent();
                //notifyListenersOfType(content, CreateGameController.class, packet.getContentType());
            }
            default -> presenter.display(packet);
            //TODO: send Activity to ActivityController when ein Spielzug passiert
        }
    }

    private <T> void notifyListenersOfType(T event, Class<? extends ServerResponseListener<T>> cls, ContentType contentType) {
        for (ServerResponseListener<?> listener : listeners) {
            if (listener.getClass().equals(cls)) {
                ((ServerResponseListener<T>) listener).serverResponseReceived(event, contentType);
            }
        }
    }
}
