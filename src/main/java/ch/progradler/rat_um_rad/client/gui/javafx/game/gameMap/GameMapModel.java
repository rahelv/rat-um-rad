package ch.progradler.rat_um_rad.client.gui.javafx.game.gameMap;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class GameMapModel {
    private ClientGame clientGame;
    private String gameID;
    private String status;
    private Integer requiredPlayers;
    private Map<String, City> citiesMap;

    public GameMapModel(ClientGame clientGame) {
        this.clientGame = clientGame;
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
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

    public void updateClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;

        //update associated fields
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
        this.requiredPlayers = clientGame.getRequiredPlayerCount();
    }

    public void updateFields() {
        //update associated fields
        this.gameID = clientGame.getId();
        this.status = clientGame.getStatus().toString();
        this.requiredPlayers = clientGame.getRequiredPlayerCount();
    }

    public String getGameID() {
        return gameID;
    }

    public String getStatus() {
        return status;
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
