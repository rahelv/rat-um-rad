package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;


import ch.progradler.rat_um_rad.shared.models.game.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;


public class LobbyModel {
    //Was braucht es hier?
    private ObservableList<GameBase> gameInfoList; //TODO: model has too much information, new model?
    //TODO: evt. Liste dieser Spieler
    public ObservableList<String> allOnlinePlayersList;
    public LobbyModel() {
        this.gameInfoList = FXCollections.observableArrayList();
        this.allOnlinePlayersList = FXCollections.observableArrayList();
        gameInfoList.add(new GameBase("erstesGame", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("die Coole", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("meimei", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("GewinnerTeam", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("bliblubb", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));

    }
    public void addPlayersToList(List<String> allOnlinePlayers){
        this.allOnlinePlayersList = FXCollections.observableArrayList(allOnlinePlayers);
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
