package ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom;

import ch.progradler.rat_um_rad.shared.models.ChatMessage;
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
}
