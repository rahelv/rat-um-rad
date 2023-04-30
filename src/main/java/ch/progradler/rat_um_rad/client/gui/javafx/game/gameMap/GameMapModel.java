package ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class GameMapModel {
    private ClientGame clientGame;
    private ObservableList<Road> roadsToBuildObservableList;
    private ObservableList<String> builtRoadsObservableList;
    private String gameID;
    private String status;
    private Date createdAt;
    private Integer requiredPlayers;
    private Map<String, City> citiesMap;

    public GameMapModel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.roadsToBuildObservableList = FXCollections.observableArrayList();
        this.builtRoadsObservableList = FXCollections.observableArrayList();
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
        this.createdAt = clientGame.getCreatedAt();
        this.requiredPlayers = clientGame.getRequiredPlayerCount();
        citiesMap = createCityHashMap(clientGame.getMap());
    }

    private static Map<String, City> createCityHashMap(GameMap gameMap) {
        Map<String, City> cities = new HashMap<>();
        for (City city : gameMap.getCities()) {
            cities.put(city.getId(), city);
        }
        return cities;
    }

    public GameMap getGameMap() {
        return this.clientGame.getMap();
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

    public Map<String, City> getCitiesMap() {
        return citiesMap;
    }

    public Map<String, String> getRoadsBuilt() {
        return clientGame.getRoadsBuilt();
    }

    public PlayerColor getPlayerColor(String ipAddress) {
        for (VisiblePlayer player : clientGame.getOtherPlayers()) {
            if (player.getIpAddress().equals(ipAddress)) return player.getColor();
        }
        return clientGame.getOwnPlayer().getColor();
    }
}
