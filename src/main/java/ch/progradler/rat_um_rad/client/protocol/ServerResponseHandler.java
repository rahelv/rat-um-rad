package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.UsernameHandler;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.JOINING_NOT_POSSIBLE;
import static ch.progradler.rat_um_rad.shared.protocol.ErrorResponse.USERNAME_INVALID;

/**
 * Handles incoming responses from server.
 */
public class ServerResponseHandler implements ServerInputPacketGateway {
    private final PackagePresenter presenter;
    private final ClientPingPongRunner clientPingPongRunner;
    private final UsernameHandler usernameHandler;

    private final IUserService userService;

    public ServerResponseHandler(PackagePresenter presenter, ClientPingPongRunner clientPingPongRunner, UsernameHandler usernameHandler, IUserService userService) {
        this.presenter = presenter;
        this.clientPingPongRunner = clientPingPongRunner;
        this.usernameHandler = usernameHandler;
        this.userService = userService;
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
                this.usernameHandler.setConfirmedUsername(change.getNewName());
                presenter.display(packet);
            }
            case INVALID_ACTION_FATAL -> {
                switch((String) packet.getContent()) {
                    case JOINING_NOT_POSSIBLE -> {
                        //TODO: implement
                    }
                    case USERNAME_INVALID -> {
                        this.usernameHandler.chooseAndSendUsername(userService);
                    }
                }
            }
            case SEND_ALL_CONNECTED_PLAYERS -> {
                //TODO: implement
            }
            case SEND_GAMES -> {
                //TODO: implement
            }
            case NEW_PLAYER -> {
                //TODO: implement
            }
            case GAME_STARTED_SELECT_DESTINATION_CARDS -> {
                //TODO: implement
            }
            default -> presenter.display(packet);
        }
    }
}
