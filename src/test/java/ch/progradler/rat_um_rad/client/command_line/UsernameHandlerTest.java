package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import ch.progradler.rat_um_rad.server.gateway.OutputPacketGateway;
import ch.progradler.rat_um_rad.server.repositories.IUserRepository;
import ch.progradler.rat_um_rad.server.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameHandlerTest {

    @Mock
    ComputerInfo computerInfoMock;

    @Mock
    InputReader inputReaderMock;

    private UsernameHandler usernameHandler;

    @BeforeEach
    public void initServiceTest() {
        usernameHandler = new UsernameHandler(computerInfoMock, inputReaderMock);
    }

    @Test
    void setConfirmedUsernameSetsTheConfirmedUsername() { //TODO: add tests for propertychangelistener
        usernameHandler.setConfirmedUsername("rahel");
        assertEquals("rahel", usernameHandler.getUsername());
    }

    @Test
    void chooseUsernameChoosesSystemUsernameWhenNothingEntered() {
        String suggestedUsername = "systemUsername";
        when((computerInfoMock).getSystemUsername()).thenReturn(suggestedUsername);
        when(inputReaderMock.readInputWithPrompt("The username suggested for you is: " +
                suggestedUsername +
                ".\nPress enter to confirm. Otherwise enter your new username below and click Enter." +
                "\nTo change your username in the future, type CHANGEUSERNAME and press Enter")).thenReturn("");
        assertEquals(suggestedUsername, usernameHandler.chooseUsername());
    }

    @Test
    void chooseUsernameChoosesSystemUsernameWhenNameEntered() {
        String suggestedUsername = "systemUsername";
        String chosenUsername = "chosenUsername";
        when((computerInfoMock).getSystemUsername()).thenReturn(suggestedUsername);
        when(inputReaderMock.readInputWithPrompt("The username suggested for you is: " +
                suggestedUsername +
                ".\nPress enter to confirm. Otherwise enter your new username below and click Enter." +
                "\nTo change your username in the future, type CHANGEUSERNAME and press Enter")).thenReturn(chosenUsername);
        assertEquals(chosenUsername, usernameHandler.chooseUsername());
    }

    @Test
    void chooseAndSendUsername() { //TODO
    }

    @Test
    void changeUsername() {
        //TODO
    }

    @Test
    void changeAndSendNewUsername() { //TODO
    }
}