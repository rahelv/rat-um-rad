package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.gateway.ServerInputPacketGateway;
import ch.progradler.rat_um_rad.client.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.shared.protocol.Packet;

/**
 * Handles incoming responses from server.
 */
public class ServerResponseHandler implements ServerInputPacketGateway {
    private final PackagePresenter presenter;

    public ServerResponseHandler(PackagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void handleResponse(Packet packet) {
        //TODO: implement QUIT command and other commands

        presenter.display(packet); // TODO: add more cases
    }
}
