package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;


import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class LobbyModel {
    private ObservableList<GameBase> gameInfoList; //TODO: model has too much information, new model?
    private Integer currentlyOnlinePlayers; //TODO: evt. Liste dieser Spieler
    public LobbyModel() {
        this.gameInfoList = FXCollections.observableArrayList();
        this.currentlyOnlinePlayers = 12;
    }

    public void addGameToLobby(GameBase game) {
        this.gameInfoList.add(game);
    }

    public void updateGameList(List<GameBase> gameList) {
        this.gameInfoList = FXCollections.observableArrayList(gameList);
    }

    public ObservableList<GameBase> getGameInfoList() {
        return gameInfoList;
    }
}
