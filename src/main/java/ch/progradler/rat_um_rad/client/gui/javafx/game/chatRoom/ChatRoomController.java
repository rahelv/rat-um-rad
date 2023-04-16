package ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
/**
 * chat messages and private chat targets are received from ServerResponseListener
 *
 * */
public class ChatRoomController implements Initializable, ServerResponseListener<ChatMessage> {
    public ChoiceBox<String> chatChoiceBox;
    public TextField chatMsgTextField;
    public Button sendButton;
    public ListView chatPaneListView;

    private ChatRoomModel chatRoomModel;
    private IUserService userService;
    private ServerResponseListener<List<String>> allPlayerListener;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this);
        try {
            this.userService = ServiceLoader.load(UserService.class).iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            userService.requestOnlinePlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.chatRoomModel = new ChatRoomModel();

        allPlayerListener = this::handleAllPlayersUpdate;
        System.out.println("check if handle all players update is called");
        chatChoiceBox.setItems(chatRoomModel.chatTargetsList);
        chatChoiceBox.getSelectionModel().select(0);//select the first item in choiceBox:"all"
        chatChoiceBox.setOnAction((e) -> getTarget());

        chatMsgTextField.textProperty().bindBidirectional(chatRoomModel.TextInputContentProperty());
        this.chatPaneListView.setItems(chatRoomModel.chatMessageList);



    }
    @FXML
    public void sendChatMessageAction(ActionEvent event) {
        try {
            String target = getTarget();
            if (target.equals("all")) {
                userService.sendBroadCastMessage(chatRoomModel.getTextInputContent());
            } else {
                userService.sendWhisperMessage(chatRoomModel.getTextInputContent(), target);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addMyOwnChatContentToChatPaneList(chatRoomModel.getTextInputContent());
        chatMsgTextField.clear();
    }

    public void addMyOwnChatContentToChatPaneList(String content){
        chatRoomModel.addChatMessageToList(new ChatMessage("You", content));
    }

    private void handleAllPlayersUpdate(List<String> allPlayersList, ContentType contentType) {
        Platform.runLater(() -> {
            chatRoomModel.addPlayersToTargetList(allPlayersList);
        });
        System.out.println("handle all players update should be invoked");//actually here is not be operated
    }
    @Override
    public void serverResponseReceived(ChatMessage chatMessage, ContentType contentType) {
        Platform.runLater(() -> {
            chatRoomModel.addChatMessageToList(chatMessage);
        });
    }

    private String getTarget() {
        return chatChoiceBox.getValue();
    }
    public interface AllPlayersListener extends  ServerResponseListener<List<String>>{

    }
}
