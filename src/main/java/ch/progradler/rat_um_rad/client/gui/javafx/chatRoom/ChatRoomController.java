package ch.progradler.rat_um_rad.client.gui.javafx.chatRoom;

import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

public class ChatRoomController implements Initializable {
    private ChatRoomModel chatRoomModel;
    public TextField chatMsgTextField;
    public Button sendButton;
    public ListView chatPaneListView;//fxml file is in resources/fxmlview

    private IUserService userService;

    @FXML
    public void sendChatMessageAction(ActionEvent event) {
        try {
            userService.sendChatMessageToServer(chatRoomModel.getTextInputContent());
            System.out.println("trying to send message " + chatRoomModel.getTextInputContent());
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
        this.chatRoomModel = new ChatRoomModel();
        chatMsgTextField.textProperty().bindBidirectional(chatRoomModel.TextInputContentProperty());
        this.chatPaneListView.setItems(chatRoomModel.chatMessageList);

        try {
            this.userService = ServiceLoader.load(UserService.class).iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
