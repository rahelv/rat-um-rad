package ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.client.utils.listeners.IListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

/**
 * Controller for the lobby internal chat (for view chatRoomView.fxml)
 */
public class ChatRoomController implements Initializable, IListener<ChatMessage> {
    private ChatRoomModel chatRoomModel;
    public TextField chatMsgTextField;
    public Button sendButton;
    public ListView chatPaneListView;
    private IUserService userService;

    /** send chat message to server through userService
     * @param event
     */
    @FXML
    public void sendChatMessageAction(ActionEvent event) {
        try {
            userService.sendBroadCastMessage(chatRoomModel.getTextInputContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        addMyOwnChatContentToChatPaneList(chatRoomModel.getTextInputContent());
        chatMsgTextField.clear();
    }

    /** adds the own written message to ChatPanel
     * @param content
     */
    public void addMyOwnChatContentToChatPaneList(String content){
        chatRoomModel.addChatMessageToList(new ChatMessage("You", content));
    }

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(this); //add listener for ServerResponses

        this.chatRoomModel = new ChatRoomModel();
        chatMsgTextField.textProperty().bindBidirectional(chatRoomModel.TextInputContentProperty()); //bind TextField for Chat Input to model
        this.chatPaneListView.setItems(chatRoomModel.chatMessageList);

        try {
            this.userService = ServiceLoader.load(UserService.class).iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** when a chatMessage is received on the ServerResponseHandler, adds the received message to the list.
     * @param chatMessage
     */
    @Override
    public void serverResponseReceived(ChatMessage chatMessage) {
        Platform.runLater(() -> {
            chatRoomModel.addChatMessageToList(chatMessage);
        });
    }
}
