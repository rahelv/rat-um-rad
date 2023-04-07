package ch.progradler.rat_um_rad.client.gui.javafx.mainMenu.lobby;

import ch.progradler.rat_um_rad.client.models.ClientGame;
import ch.progradler.rat_um_rad.client.models.GameInfo;
import ch.progradler.rat_um_rad.client.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.GameMap;

import java.util.ArrayList;
import java.util.List;

public class LobbyModel {

    public LobbyModel() {
        this.gameList = new ArrayList<GameInfo>();
        gameList.add(new GameInfo("1", "open",5, "spoiel1"));
        gameList.add(new GameInfo("2", "open",4, "lalalulu"));
        gameList.add(new GameInfo("3", "open",5, "mergwönned"));
        gameList.add(new GameInfo("4", "open",3, "neimer"));
        gameList.add(new GameInfo("5", "open",4, "die coole"));

        this.currentlyOnlinePlayers = 12;
    }
    //Was braucht es hier?
    private List<GameInfo> gameList; //TODO: model has too much information, new model?
    private Integer currentlyOnlinePlayers; //TODO: evt. Liste dieser Spieler
    public List<String> getGameNames() {
        List<String> list = new ArrayList<String>();
        for (GameInfo game : gameList) {
            list.add(game.getName());
        }
        return list;
    }
}
