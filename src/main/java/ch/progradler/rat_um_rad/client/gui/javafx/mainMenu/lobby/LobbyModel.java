package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;


import ch.progradler.rat_um_rad.shared.models.game.GameBase;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LobbyModel {
    //Was braucht es hier?
    private ObservableList<GameBase> gameInfoList; //TODO: model has too much information, new model?
    private Integer currentlyOnlinePlayers; //TODO: evt. Liste dieser Spieler
    public LobbyModel() {
        this.gameInfoList = FXCollections.observableArrayList();
        this.currentlyOnlinePlayers = 12;
    }

    public void addGameToLobby(GameBase game) {
        this.gameInfoList.add(game);
    }

    public ObservableList<GameBase> getGameInfoList() {
        return gameInfoList;
    }
}
