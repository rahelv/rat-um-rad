package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;


import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class LobbyModel {
    //Was braucht es hier?
    private ObservableList<GameBase> gameInfoList; //TODO: model has too much information, new model?
    private Integer currentlyOnlinePlayers; //TODO: evt. Liste dieser Spieler
    private ObservableList<String> allOnlinePlayersList;
    public LobbyModel() {
        this.gameInfoList = FXCollections.observableArrayList();
        gameInfoList.add(new GameBase("erstesGame", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("die Coole", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("meimei", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("GewinnerTeam", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("bliblubb", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));

        this.allOnlinePlayersList = FXCollections.observableArrayList();
        this.currentlyOnlinePlayers = allOnlinePlayersList.size();
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

    public void updateAllOnlinePlayersList(List<String> allOnlinePlayersList){
        this.allOnlinePlayersList = FXCollections.observableArrayList(allOnlinePlayersList);
    }
    public Integer getCurrentlyOnlinePlayers(){
        return currentlyOnlinePlayers;
    }
}
