package ch.progradler.rat_um_rad.client.command_line;

import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
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
        when(inputReaderMock.readInputWithPrompt(new StringBuilder()
                .append( "The username suggested for you is: ")
                .append(suggestedUsername)
                .append("\nPress enter to confirm. Otherwise enter your new username below and click Enter.")
                .append("\nUsername Rules: 5-30 characters. only letters, digits and underscores allowed. first char must be a letter!")
                .append("\nTo change your username in the future, type CHANGEUSERNAME and press Enter")
                .toString())).thenReturn("");
        assertEquals(suggestedUsername, usernameHandler.chooseUsername());
    }

    @Test
    void chooseUsernameChoosesSystemUsernameWhenNameEntered() {
        String suggestedUsername = "systemUsername";
        String chosenUsername = "chosenUsername";
        when((computerInfoMock).getSystemUsername()).thenReturn(suggestedUsername);
        when(inputReaderMock.readInputWithPrompt( new StringBuilder()
                .append( "The username suggested for you is: ")
                .append(suggestedUsername)
                .append("\nPress enter to confirm. Otherwise enter your new username below and click Enter.")
                .append("\nUsername Rules: 5-30 characters. only letters, digits and underscores allowed. first char must be a letter!")
                .append("\nTo change your username in the future, type CHANGEUSERNAME and press Enter")
                .toString())).thenReturn(chosenUsername);
        assertEquals(chosenUsername, usernameHandler.chooseUsername());
    }

    @Test
    void chooseAndSendUsername() { //TODO see handleMessageBroadCastsThisMessageWIthCorrectUsernameToAllClientsExceptThatUser() in UserService
    }

    @Test
    void changeUsername() {
        //TODO
    }

    @Test
    void changeAndSendNewUsername() { //TODO
    }
}