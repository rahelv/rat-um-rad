package ch.progradler.rat_um_rad.client.gui.javafx;

import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomController;
import ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom.ChatRoomModel;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;


@ExtendWith(MockitoExtension.class)
public class ChatRoomControllerTest {

    public TextField chatMsgTextField;

    public ListView chatPaneListView;
    @Mock
    IUserService userService;
    @Mock
    private ChatRoomController chatRoomController;
    @Mock
    ChatRoomModel chatRoomModel;

    @BeforeEach
    void setUp() throws Exception{
        chatRoomController = new ChatRoomController();
        userService = new UserService();
    }
    @Test
    void listViewTest() {
        chatRoomController.chatMsgTextField.setText("expected chat message");
        chatRoomController.sendButton.setOnAction(event -> {
            try {
                userService.sendBroadCastMessage("expected chat message");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ObservableList items = chatRoomController.chatPaneListView.getItems();
        Assertions.assertEquals("expected chat message",items.toString());
    }
}
