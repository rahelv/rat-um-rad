package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyController implements Initializable {
    public ListView<String> openGamesListView;
    public Button showAllGamesButton;
    public Button leaveLobbyButton;
    public Button createGameButton;
    public TextArea currentPlayersTextArea;
    public TextField gameIdTextField;
    public Button joinButton;

    private LobbyModel lobbyModel;


    //private ListView<String> listView;

    public LobbyController() {
        this.lobbyModel = new LobbyModel();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //this.openGamesListView.getItems().addAll(this.lobbyModel.getGameNamesList());
        this.openGamesListView.setItems(this.lobbyModel.getGameNamesList());

        //each item of listView should have 2 buttons:list players and join game
        openGamesListView.setCellFactory(param -> new CustomCell());

        //populate the ListView with HBox
        for(String item : lobbyModel.getGameNamesList()){
            openGamesListView.setCellFactory(param -> new CustomCell());
        }
    }
    static class CustomCell extends ListCell<String> {
        private HBox hbox;
        private Pane pane;
        private Label nameLabel;
        private Button listPlayersBtn;
        private Button enterGameBtn;
        public CustomCell() {
            super();
            hbox = new HBox();
            pane = new Pane();
            nameLabel = new Label();

            listPlayersBtn = new Button("list");
            enterGameBtn = new Button("join");

            hbox.getChildren().addAll(nameLabel,pane,listPlayersBtn, enterGameBtn);
            hbox.setHgrow(pane, Priority.ALWAYS);
            setText(null);

            listPlayersBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //do something
                }
            });
            enterGameBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //do something
                }
            });
        }
        @Override
        protected void updateItem(String item,boolean empty){
            super.updateItem(item,empty);
            setEditable(false);
            if(item != null){
                nameLabel.setText(item);
                setGraphic(hbox);
            }else{
                setGraphic(null);
            }
        }
    }

    @FXML
    public void showAllGamesAction(ActionEvent event){

    }

    @FXML
    public void leaveLobbyAction(ActionEvent actionEvent) {
    }

    @FXML
    public void createGameAction(ActionEvent actionEvent) {
    }

    @FXML
    public void joinAction(ActionEvent actionEvent) {
    }

}
