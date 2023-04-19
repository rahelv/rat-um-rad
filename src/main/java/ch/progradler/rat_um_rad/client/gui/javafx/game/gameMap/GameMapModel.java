package ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;

public class GameMapModel {
    private ClientGame clientGame;
    private ObservableList<Road> roadObservableList;
    private String gameID;
    private String status;
    private Date createdAt;
    private Integer requiredPlayers;
    public GameMapModel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.roadObservableList = FXCollections.observableArrayList();
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
        this.createdAt = clientGame.getCreatedAt();
        this.requiredPlayers = clientGame.getRequiredPlayerCount();
    }
    public ObservableList<Road> getRoadObservableList() {
        return this.roadObservableList;
    }
    public void setRoadObservableList() {
        this.roadObservableList = FXCollections.observableArrayList(this.clientGame.getMap().getRoads());
    }
    public void updateClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;

        //update associated fields
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
        this.createdAt = clientGame.getCreatedAt();
        this.requiredPlayers = clientGame.getRequiredPlayerCount();
    }

    public void updateClientGameWithMap(ClientGame clientGame) {
        this.updateClientGame(clientGame);
        this.roadObservableList.clear();
        this.roadObservableList.addAll(clientGame.getMap().getRoads());
    }

    public String getGameID() {
        return gameID;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Integer getRequiredPlayers() {
        return requiredPlayers;
    }
}
