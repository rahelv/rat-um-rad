package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.GameController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.StartupPageController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.createGame.CreateGameController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.gameOverview.GameOverviewController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyModel;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import java.util.ArrayList;
import java.util.List;

import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.JOINING_NOT_POSSIBLE;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.USERNAME_INVALID;

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
                notifyListenersOfType(change, UsernameChangeController.class, packet.getCommand());
            }
            case INVALID_ACTION_FATAL -> {
                //TODO: differentiate further between fatal actions
                //this.userService.chooseAndSendUsername(this.serverOutput);
                switch((String) packet.getContent()) {
                    case JOINING_NOT_POSSIBLE -> {
                        //TODO: implement
                    }
                    case USERNAME_INVALID -> {
                        //this.usernameHandler.chooseAndSendUsername(userService);
                    }
                }
            }
            case SEND_ALL_CONNECTED_PLAYERS -> {
                //TODO: implement
            }
            case SEND_GAME_INTERNAL_CHAT -> {
                //TODO: update chatRoomModel
                ChatMessage message = (ChatMessage) packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType(message, ChatRoomController.class, packet.getCommand());
            }
            case SEND_WAITING_GAMES -> {
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType((List<GameBase>) content, LobbyModel.class, packet.getCommand());
            }
            case SEND_STARTED_GAMES, SEND_FINISHED_GAMES -> {
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                notifyListenersOfType((List<GameBase>) content, GameOverviewController.class, packet.getCommand());
            }
            case NEW_USER -> {
                String content = (String) packet.getContent();
            }
            case GAME_CREATED -> {
                ClientGame content = (ClientGame) packet.getContent();
                notifyListenersOfType(content, CreateGameController.class, packet.getCommand());
            }
            case GAME_JOINED -> {
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, StartupPageController.class, packet.getCommand());
            }
            case NEW_PLAYER-> {
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, GameController.class, packet.getCommand()); //updated ClientGame is sent to Controller, so it can display the new state
            }
            case GAME_STARTED_SELECT_DESTINATION_CARDS -> {
                ClientGame clientGame = (ClientGame) packet.getContent();
                notifyListenersOfType(clientGame, GameController.class, packet.getCommand()); //TODO: differentiate between actions (instead of contentTypes)
            }
            default -> presenter.display(packet);
        }
    }

    private <T> void notifyListenersOfType(T event, Class<? extends ServerResponseListener<T>> cls, Command command) {
        for (ServerResponseListener<?> listener : listeners) {
            if (listener.getClass().equals(cls)) {
                ((ServerResponseListener<T>) listener).serverResponseReceived(event, command);
            }
        }
    }
}
