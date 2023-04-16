package ch.progradler.rat_um_rad.client.protocol;

import ch.progradler.rat_um_rad.client.command_line.presenter.PackagePresenter;
import ch.progradler.rat_um_rad.client.gui.javafx.changeUsername.UsernameChangeController;
import ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby.LobbyController;
import ch.progradler.rat_um_rad.client.protocol.pingpong.ClientPingPongRunner;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import ch.progradler.rat_um_rad.shared.protocol.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServerResponseHandlerTest {
    @Mock
    PackagePresenter mockPackagePresenter;
    @Mock
    ClientPingPongRunner mockClientPingPongRunner;

    private ServerResponseHandler serverResponseHandler;

    @BeforeEach
    void setUp() {
        serverResponseHandler = new ServerResponseHandler(mockPackagePresenter, mockClientPingPongRunner);
    }

    @Test
    void handlesUsernameConfirmedCommandCorrectly() {
        UsernameChangeController mockController = mock(UsernameChangeController.class);
        serverResponseHandler.addListener(mockController);

        UsernameChange change = new UsernameChange("old", "new");
        Packet packet = new Packet(Command.USERNAME_CONFIRMED, change, ContentType.USERNAME_CHANGE);
        serverResponseHandler.handleResponse(packet);

        verify(mockController).serverResponseReceived(change, ContentType.USERNAME_CHANGE);
    }
    @Test
    void handlesSendAllConnectedPlayersCommandCorrectly(){
        LobbyController.AllOnlinePlayersListener mockController = mock(LobbyController.AllOnlinePlayersListener.class);
        serverResponseHandler.addListener(mockController);

        List<String> lisOfUsernames = new ArrayList<>();
        lisOfUsernames.add("amy");
        lisOfUsernames.add("sam");
        Packet packet = new Packet(Command.SEND_ALL_CONNECTED_PLAYERS, lisOfUsernames, ContentType.STRING_LIST);

        serverResponseHandler.handleResponse(packet);

        verify(mockController).serverResponseReceived(lisOfUsernames,ContentType.STRING_LIST);

    }
}