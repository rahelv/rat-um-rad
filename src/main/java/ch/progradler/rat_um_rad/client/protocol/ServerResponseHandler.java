package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.controllers.UserController;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Handles incoming responses from server.
 */
public class ServerResponseHandler implements ServerInputPacketGateway {
    private final PackagePresenter presenter;
    private final ClientPingPongRunner clientPingPongRunner;
    private final UserController userController;

    private final ServerOutput serverOutput;

    public ServerResponseHandler(PackagePresenter presenter, ClientPingPongRunner clientPingPongRunner, UserController userController, ServerOutput serverOutput) {
        this.presenter = presenter;
        this.clientPingPongRunner = clientPingPongRunner;
        this.userController = userController;
        this.serverOutput = serverOutput;
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
                this.userController.changeUsername(change.getNewName());
                presenter.display(packet);
            }
            case INVALID_ACTION_FATAL -> {
                //TODO: differentiate further between fatal actions
                this.userController.chooseAndSendUsername(this.serverOutput);
            }
            default -> presenter.display(packet);
        }
    }
}
