package ch.progradler.rat_um_rad.client.gui.javafx.chatRoom;

import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.services.IUserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ChatRoomController {

    public TextField chatMsgTextField;
    public Button sendButton;
    public ListView chatPaneListView;//fxml file is in resources/fxmlview


    private IUserService userService;
    private User user;

    @FXML
    public void sendChatMessageAction() {
        String chatContent = chatMsgTextField.getText();
        try {
            sendButton.setOnAction(event -> {
                try {
                    userService.sendChatMessageToServer(user.getUsername(),chatContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            chatMsgTextField.clear();
            addMyOwnChatContentToChatPaneList(chatContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * set sending chat message through ENTER key
     */
    public void sendThroughEnter(KeyEvent keyEvent){
        if(keyEvent.getCode() == KeyCode.ENTER){
            sendChatMessageAction();
        }
    }
    public void addMyOwnChatContentToChatPaneList(String content){
        Platform.runLater(() -> {
            Label contentLabel = new Label(content);
            contentLabel.setPrefSize(Label.USE_COMPUTED_SIZE,Label.USE_COMPUTED_SIZE);
            HBox hBox = new HBox();
            hBox.setMaxWidth(chatPaneListView.getWidth()-20);
            hBox.setAlignment(Pos.TOP_RIGHT);
            hBox.getChildren().add(contentLabel);
            chatPaneListView.getItems().add(hBox);
        });
    }

}
