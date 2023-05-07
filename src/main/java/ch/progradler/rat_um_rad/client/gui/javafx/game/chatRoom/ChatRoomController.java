package ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controller for the lobby internal chat (for view ChatRoomView.fxml)
 */
public class ChatRoomController extends VBox {
    private ChatRoomModel chatRoomModel;
    @FXML
    private TextField chatMsgTextField;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<ChatMessage> chatPaneListView;
    private IUserService userService;

    public ChatRoomController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/game/ChatRoomView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        this.userService = new UserService();
    }

    public void initData(ChatRoomModel chatRoomModel) {
        this.chatRoomModel = chatRoomModel;
        chatMsgTextField.textProperty().bindBidirectional(chatRoomModel.TextInputContentProperty()); //bind TextField for Chat Input to model
        this.chatPaneListView.setItems(chatRoomModel.chatMessageList);
    }

    /**
     * whisper chat form : WHISPER<>ToUsername<>chatContent
     * when user wants to send whisper chat, he/she should enter message in this form:
     * ToUsername can be found in activities
     * WHISPER<>smith<>how many cards do you have
     * <p>
     * broadcast chat form : BROADCAST<>chatContent
     * when user wants to send broadcast message to all clients ,he/she should enter message in this form:
     * BROADCAST<>I like this game
     *
     * @param event send chat message to server through userService
     */
    @FXML
    public void sendChatMessageAction(ActionEvent event) {
        try {
            String whisperCommand = "WHISPER";
            String broadCommand = "BROADCAST";
            String separator = "<>";
            String textInputContent = chatRoomModel.getTextInputContent();
            String[] strings = textInputContent.split(separator);
            if (strings[0].equals(whisperCommand)) {
                String toUsername = strings[1];
                String whisperContent = "whisper : " + strings[2];
                userService.sendWhisperMessage(whisperContent, toUsername);
            } else if (strings[0].equals(broadCommand)) {
                String broadcastContent = "broadcast: " + strings[1];
                userService.sendBroadCastMessage(broadcastContent);
            } else {
                userService.sendGameInternalMessage(strings[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addMyOwnChatContentToChatPaneList(chatRoomModel.getTextInputContent());
        chatMsgTextField.clear();
    }

    /**
     * @param content adds the own written message to ChatPanel
     */
    public void addMyOwnChatContentToChatPaneList(String content) {
        chatRoomModel.addChatMessageToList(new ChatMessage("You", content));
    }
}
