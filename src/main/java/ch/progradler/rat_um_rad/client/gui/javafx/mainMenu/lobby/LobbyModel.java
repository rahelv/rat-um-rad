package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;


import ch.progradler.rat_um_rad.client.models.GameInfo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class LobbyModel {
    //Was braucht es hier?
    private ObservableList<GameInfo> gameInfoList; //TODO: model has too much information, new model?
    private Integer currentlyOnlinePlayers; //TODO: evt. Liste dieser Spieler
    private ObservableList<String> gameNamesList;
    private Integer playersInGameGroup;
    public LobbyModel() {
        //this.gameList = new ArrayList<GameInfo>();
        this.gameInfoList = FXCollections.observableArrayList();
        gameInfoList.add(new GameInfo("1", "open",5, "spoiel1"));
        gameInfoList.add(new GameInfo("2", "open",4, "lalalulu"));
        gameInfoList.add(new GameInfo("3", "open",5, "mergwönned"));
        gameInfoList.add(new GameInfo("4", "open",3, "neimer"));
        gameInfoList.add(new GameInfo("5", "open",4, "die coole"));

        this.currentlyOnlinePlayers = 12;
    }

    /*
    public List<String> getGameNames() {
        List<String> list = new ArrayList<String>();
        for (GameInfo game : gameList) {
            list.add(game.getName());
        }
        return list;
    }
     */
    public ObservableList<String> getGameNamesList(){
        for(GameInfo game : gameInfoList){
            gameNamesList.add(game.getName());
        }
        return gameNamesList;
    }

}
