package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ServerResponseHandlerTest {
    @Mock
    PackagePresenter mockPackagePresenter;
    @Mock
    ClientPingPongRunner mockClientPingPongRunner;

    private ServerResponseHandler serverResponseHandler;

    @BeforeEach
    void setUp() {
        serverResponseHandler = new ServerResponseHandler(mockClientPingPongRunner);
    }

    @Test
    void handlesUsernameConfirmedCommandCorrectly() {
        UsernameChangeController mockController = mock(UsernameChangeController.class);
        ServerResponseListener<UsernameChange> mockListener = mock(ServerResponseListener.class);
        serverResponseHandler.addListener(mockListener);

        UsernameChange change = new UsernameChange("old", "new");
        Packet.Server packet = new Packet.Server(ServerCommand.USERNAME_CONFIRMED, change, ContentType.USERNAME_CHANGE);
        serverResponseHandler.handleResponse(packet);

        // verify(mockController).usernameChangeReceived(change);
        // TODO: fix!
    }
}