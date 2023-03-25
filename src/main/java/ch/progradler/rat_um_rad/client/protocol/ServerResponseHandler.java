package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.UsernameHandler;
import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Handles incoming responses from server.
 */
public class ServerResponseHandler implements ServerInputPacketGateway {
    private final PackagePresenter presenter;
    private final UsernameHandler usernameHandler;

    public ServerResponseHandler(PackagePresenter presenter, UsernameHandler usernameHandler) {
        this.presenter = presenter;
        this.usernameHandler = usernameHandler;
    }

    /** handles incoming packet from server depending on the command.
     * @param packet
     */
    @Override
    public void handleResponse(Packet packet) {
        //TODO: implement QUIT command and other commands
        switch (packet.getCommand()) {
            case USERNAME_CONFIRMED -> {
                UsernameChange change = (UsernameChange) packet.getContent();
                this.usernameHandler.setConfirmedUsername(change.getNewName());
                presenter.display(packet);
            }
            default -> presenter.display(packet);
        }
    }
}
