package ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Road;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameMapModel {
    private ClientGame clientGame;
    private ObservableList<Road> roadsToBuildObservableList;
    private ObservableList<String> builtRoadsObservableList;
    private String gameID;
    private String status;
    private Date createdAt;
    private Integer requiredPlayers;

    public GameMapModel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.roadsToBuildObservableList = FXCollections.observableArrayList();
        this.builtRoadsObservableList = FXCollections.observableArrayList();
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
        this.createdAt = clientGame.getCreatedAt();
        this.requiredPlayers = clientGame.getRequiredPlayerCount();
    }

    public ObservableList<Road> getRoadsToBuildObservableList() {
        return this.roadsToBuildObservableList;
    }

    public void setRoadsToBuildObservableList() {
        this.roadsToBuildObservableList = FXCollections.observableArrayList(this.clientGame.getMap().getRoads());
    }

    public ObservableList<String> getBuiltRoadsObservableList() {
        return this.builtRoadsObservableList;
    }

    public void setBuiltRoadObservableList() {
        List<String> roadsList = new ArrayList<String>(this.clientGame.getRoadsBuilt().keySet());
        this.builtRoadsObservableList = FXCollections.observableArrayList(roadsList);
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
        this.roadsToBuildObservableList.clear();
        this.roadsToBuildObservableList.addAll(clientGame.getMap().getRoads());

        this.builtRoadsObservableList.clear();
        this.setBuiltRoadObservableList();
    }

    public void updateFields() {
        //update associated fields
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
        this.createdAt = clientGame.getCreatedAt();
        this.requiredPlayers = clientGame.getRequiredPlayerCount();

        this.roadsToBuildObservableList.clear();
        this.roadsToBuildObservableList.addAll(clientGame.getMap().getRoads());

        this.builtRoadsObservableList.clear();
        this.setBuiltRoadObservableList();
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
