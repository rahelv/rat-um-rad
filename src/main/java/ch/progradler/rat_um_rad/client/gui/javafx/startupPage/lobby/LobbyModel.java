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
    public ObservableList<String> allOnlinePlayersList;
    private StringProperty allOnlinePlayersProperty; //TODO: evt. Liste dieser Spieler
    public LobbyModel() {
        this.gameInfoList = FXCollections.observableArrayList();
        gameInfoList.add(new GameBase("erstesGame", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("die Coole", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("meimei", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("GewinnerTeam", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        gameInfoList.add(new GameBase("bliblubb", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        allOnlinePlayersList = FXCollections.observableArrayList();
    }
    public void updateAllOnlinePlayersList(List<String> onlinePlayersList){

        allOnlinePlayersList.addAll(onlinePlayersList);

    }
    public StringProperty allOnlinePlayersProperty(){ //bindBidirectional with currentPlayersTextArea
        this.allOnlinePlayersProperty = new SimpleStringProperty("all online players : "+this.allOnlinePlayersList.size());
        return allOnlinePlayersProperty;
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
