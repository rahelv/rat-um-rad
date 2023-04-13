package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomModel;
import ch.progradler.rat_um_rad.client.services.IUserService;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ChatRoomControllerTest {


    //@Mock
    ChoiceBox<String> chatChoiceBox;
    //@Mock
    TextField chatMsgTextField;
    //@Mock
    ListView chatPaneListView;

    @Mock
    IUserService userService;
    @Mock
    ChatRoomModel chatRoomModel;

    @InjectMocks
    ChatRoomController chatRoomController;

    @BeforeEach
    void setUp() {
        chatRoomController = new ChatRoomController();
    }

    @Test
    void sendChatButtonActionSendsInputToAllIfChatChoiceBoxIsAll() throws IOException {
        //chatChoiceBox = mock(ChoiceBox.class);
        chatChoiceBox = new ChoiceBox<>();
        chatChoiceBox.setValue("all");

        //chatRoomController.setChatChoiceBox(chatChoiceBox);

        // prepare
        String chatInput = "userInput";
        when(chatRoomModel.getTextInputContent()).thenReturn(chatInput);
        //when(chatChoiceBox.getValue()).thenReturn("all");

        // execute
        chatRoomController.sendChatMessageAction(new ActionEvent());

        // assert
        verify(userService).sendBroadCastMessage(chatInput);
    }

    @Test
    void sendChatButtonActionClearsTextField() throws IOException {
        // execute
        chatRoomController.sendChatMessageAction(new ActionEvent());

        // assert
        verify(chatMsgTextField).clear();
    }

    @Test
    void listViewTest() {
         /*String chatInput ="userInput";
        when(chatRoomModel.getTextInputContent()).thenReturn(chatInput);
        chatRoomController.sendChatMessageAction(new ActionEvent());


        userService.sendBroadCastMessage(chatInput);

        ObservableList items = chatRoomController.chatPaneListView.getItems();
        Assertions.assertEquals("expected chat message",items.toString());

          */
    }
}
