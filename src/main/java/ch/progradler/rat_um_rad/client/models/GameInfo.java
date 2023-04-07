package ch.progradler.rat_um_rad.client.models;

import ch.progradler.rat_um_rad.shared.models.game.GameMap;
import ch.progradler.rat_um_rad.shared.models.game.GameStatus;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

public class GameInfo {
    private String id;
    private String name;
    private String status;
    private List<VisiblePlayer> playersInGame; //TODO
    private Integer requiredPlayerCount;

    public GameInfo(String id, String status, Integer requiredPlayerCount, String name) {
        this.id = id;
        this.status = status;
        //this.playersInGame = players;
        this.requiredPlayerCount = requiredPlayerCount;
        this.name = name;
    }

    public String getName() {
        return name;
    }

}