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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

/**
 * Controller for the lobby internal chat (for view ChatRoomView.fxml)
 */
public class ChatRoomController implements Initializable {
    private ChatRoomModel chatRoomModel;
    public TextField chatMsgTextField;
    public Button sendButton;
    public ListView<ChatMessage> chatPaneListView;
    private IUserService userService;

    /**
     * whisper chat form : WHISPER<>ToUsername<>chatContent
     * when user wants to send whisper chat, he/she should enter message in this form:
     * ToUsername can be found in activities
     * WHISPER<>smith<>how many cards do you have
     *
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

    /**
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(
                getChatListener());
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(
                getWhisperChatListener());
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(
                getBroadcastListener());
        this.chatRoomModel = new ChatRoomModel();
        chatMsgTextField.textProperty().bindBidirectional(chatRoomModel.TextInputContentProperty()); //bind TextField for Chat Input to model
        this.chatPaneListView.setItems(chatRoomModel.chatMessageList);

        try {
            this.userService = ServiceLoader.load(UserService.class).iterator().next();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServerResponseListener<ChatMessage> getChatListener() {
        return new ServerResponseListener<>() {
            /**
             * @param chatMessage when a chatMessage is received on the ServerResponseHandler, adds the received message to the list.
             */
            @Override
            public void serverResponseReceived(ChatMessage chatMessage) {
                Platform.runLater(() -> {
                    chatRoomModel.addChatMessageToList(chatMessage);
                });
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.GAME_INTERNAL_CHAT_SENT;
            }
        };
    }

    private ServerResponseListener<ChatMessage> getWhisperChatListener() {
        return new ServerResponseListener<>() {
            @Override
            public void serverResponseReceived(ChatMessage content) {
                Platform.runLater(() -> {
                    chatRoomModel.addChatMessageToList(content);
                });
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.WHISPER_CHAT_SENT;
            }
        };
    }

    private ServerResponseListener<ChatMessage> getBroadcastListener() {
        return new ServerResponseListener<ChatMessage>() {
            @Override
            public void serverResponseReceived(ChatMessage content) {
                Platform.runLater(() -> {
                    chatRoomModel.addChatMessageToList(content);
                });
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.BROADCAST_CHAT_SENT;
            }
        };
    }
}
