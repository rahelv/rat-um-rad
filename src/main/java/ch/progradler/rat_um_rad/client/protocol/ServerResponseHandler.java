package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.client.utils.listeners.IListener;
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
    private List<IListener> listeners = new ArrayList<IListener>();
    private final PackagePresenter presenter;
    private final ClientPingPongRunner clientPingPongRunner;
    private UsernameChangeController usernameChangeController;

    @Override
    public void addListener(IListener listenerToAdd) {
        this.listeners.add(listenerToAdd);
    }

    public ServerResponseHandler(PackagePresenter presenter, ClientPingPongRunner clientPingPongRunner) {
        this.presenter = presenter;
        this.clientPingPongRunner = clientPingPongRunner;
    }

    @Override
    public void setUsernameChangeController(UsernameChangeController usernameChangeController) {
        this.usernameChangeController = usernameChangeController;
    }

    /**
     * Receives {@link Packet} from {@link ServerInputListener} sent by client. This is the implementation of the protocol.
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
                usernameChangeController.setConfirmedUsername(change.getNewName()); //TODO: implement using Listener
            }
            case INVALID_ACTION_FATAL -> {
                //TODO: differentiate further between fatal actions
                //this.userService.chooseAndSendUsername(this.serverOutput);
            }
            case SEND_CHAT -> {
                //TODO: update chatRoomModel
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                if (contentType == ContentType.CHAT_MESSAGE) {
                    for(IListener<ChatMessage> listener : listeners) {
                        listener.serverResponseReceived((ChatMessage) content);
                    }
                }
            }
            case SEND_GAMES -> {
                Object content = packet.getContent();
                ContentType contentType = packet.getContentType();
                if(contentType == ContentType.GAME_INFO_LIST) {
                    for(IListener<GameBase> listener: listeners) {
                        listener.serverResponseReceived((GameBase) content);
                    }
                    //Packet packet = new Packet(SEND_GAMES, gameRepository.getWaitingGames(), GAME_INFO_LIST);
                }

            }
            default -> presenter.display(packet);
        }
    }
}
