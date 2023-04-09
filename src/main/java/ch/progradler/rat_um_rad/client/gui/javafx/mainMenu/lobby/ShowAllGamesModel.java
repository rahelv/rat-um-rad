package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;

import ch.progradler.rat_um_rad.shared.models.game.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ShowAllGamesModel {
    private ObservableList<GameBase> openGameList;
    private ObservableList<GameBase> ongoingGameList;
    private ObservableList<GameBase> finishedGameList;

    public ShowAllGamesModel() {
        this.openGameList = FXCollections.observableArrayList();
        this.ongoingGameList = FXCollections.observableArrayList();
        this.finishedGameList = FXCollections.observableArrayList();

        openGameList.add(new GameBase("aloha", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        openGameList.add(new GameBase("die Coole", GameStatus.WAITING_FOR_PLAYERS, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        ongoingGameList.add(new GameBase("no found", GameStatus.STARTED, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        ongoingGameList.add(new GameBase("GewinnerTeam", GameStatus.STARTED, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
        finishedGameList.add(new GameBase("bliblubb", GameStatus.FINISHED, new GameMap(new ArrayList<City>(), new ArrayList<Road>()), "creatorpLayersip", 5));
    }
    public ObservableList<GameBase> getOpenGameList(){
        return openGameList;
    }
    public ObservableList<GameBase> getOngoingGameList(){
        return ongoingGameList;
    }
    public ObservableList<GameBase> getFinishedGameList(){
        return finishedGameList;
    }
}
