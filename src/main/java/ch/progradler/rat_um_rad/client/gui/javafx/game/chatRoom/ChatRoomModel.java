package ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatRoomModel {
    ObservableList<ChatMessage> chatMessageList;
    private StringProperty textInputContent;

    public ChatRoomModel() {
        this.textInputContent = new SimpleStringProperty("");
        this.chatMessageList = FXCollections.observableArrayList();

        InputPacketGatewaySingleton.getInputPacketGateway().addListener(
                getChatListener());
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(
                getWhisperChatListener());
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(
                getBroadcastListener());
    }

    public StringProperty TextInputContentProperty() {
        return textInputContent;
    }

    public String getTextInputContent() {
        return textInputContent.get();
    }

    public void addChatMessageToList(ChatMessage chatMessage) {
        this.chatMessageList.add(chatMessage);
    }


    private ServerResponseListener<ChatMessage> getChatListener() {
        return new ServerResponseListener<>() {
            /**
             * @param chatMessage when a chatMessage is received on the ServerResponseHandler, adds the received message to the list.
             */
            @Override
            public void serverResponseReceived(ChatMessage chatMessage) {
                Platform.runLater(() -> {
                    addChatMessageToList(chatMessage);
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
                    addChatMessageToList(content);
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
                    addChatMessageToList(content);
                });
            }

            @Override
            public ServerCommand forCommand() {
                return ServerCommand.BROADCAST_CHAT_SENT;
            }
        };
    }
}
