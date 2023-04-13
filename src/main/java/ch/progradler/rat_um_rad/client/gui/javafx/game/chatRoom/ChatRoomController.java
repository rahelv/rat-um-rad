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
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

public class ChatRoomController implements Initializable, ServerResponseListener<ChatMessage> {
    public ChoiceBox<String> chatChoiceBox;
    private ChatRoomModel chatRoomModel;
    public TextField chatMsgTextField;
    public Button sendButton;
    public ListView chatPaneListView;
    private IUserService userService;

    @FXML
    public void sendChatMessageAction(ActionEvent event) {
        try {
            if(getTarget(event).equals("all")){
                userService.sendBroadCastMessage(chatRoomModel.getTextInputContent());
            }else{
                userService.sendWhisperMessage(chatRoomModel.getTextInputContent(),getTarget(event));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this);

        this.chatRoomModel = new ChatRoomModel();
        chatMsgTextField.textProperty().bindBidirectional(chatRoomModel.TextInputContentProperty());
        this.chatPaneListView.setItems(chatRoomModel.chatMessageList);

        //chatChoiceBox.getItems().addAll(chatRoomModel.chatTargetsList);
        chatChoiceBox.setItems(chatRoomModel.chatTargetsList);
        chatChoiceBox.getSelectionModel().select(0);//select the first item in choiceBox:"all"
        chatChoiceBox.setOnAction(this::getTarget);
        try {
            this.userService = ServiceLoader.load(UserService.class).iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverResponseReceived(ChatMessage chatMessage, ContentType contentType) {
        Platform.runLater(() -> {
            chatRoomModel.addChatMessageToList(chatMessage);
        });
    }
    public String getTarget(ActionEvent event){
        return chatChoiceBox.getValue();
    }
}
