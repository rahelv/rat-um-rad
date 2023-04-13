package ch.progradler.rat_um_rad.client.gui.javafx.game.chatRoom;

import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.ChatMessage;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ChatRoomModel implements ServerResponseListener<List<String>> {
    ObservableList<ChatMessage> chatMessageList;
    private StringProperty textInputContent;

    ObservableList<String> chatTargetsList;
    List<String> allOnlinePlayers;
    public ChatRoomModel() {

        this.textInputContent = new SimpleStringProperty("");
        this.chatMessageList = FXCollections.observableArrayList();
        chatTargetsList.add("all");
        this.chatTargetsList = FXCollections.observableArrayList(getListOfAllOnlinePlayersFromServer());
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
    public void addPlayersToTargetList(String userName){
        chatTargetsList.add(userName);
    }

    @Override
    public void serverResponseReceived(List<String> content, ContentType contentType) {
        Platform.runLater(() -> allOnlinePlayers = content);
    }
    public List<String> getListOfAllOnlinePlayersFromServer(){
        return allOnlinePlayers;
    }
}
