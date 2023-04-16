package ch.progradler.rat_um_rad.client.gui.javafx.startupPage.lobby;


import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.server.models.Game;
import ch.progradler.rat_um_rad.shared.models.game.*;
import ch.progradler.rat_um_rad.shared.protocol.Command;
import ch.progradler.rat_um_rad.shared.protocol.ContentType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class LobbyModel {
    private ObservableList<GameBase> gameInfoList; //TODO: model has too much information, new model?
    private Integer currentlyOnlinePlayers; //TODO: evt. Liste dieser Spieler
    public LobbyModel() {
        InputPacketGatewaySingleton.getInputPacketGateway().addListener(new ServerResponseListener<List<GameBase>>() {
            @Override
            public void serverResponseReceived(List<GameBase> content) {
                waitingGamesReceived(content);
            }

            @Override
            public Command forCommand() {
                return Command.SEND_FINISHED_GAMES;
            }
        });
        this.gameInfoList = FXCollections.observableArrayList();
        this.currentlyOnlinePlayers = 12;
    }

    public void addGameToLobby(GameBase game) {
        this.gameInfoList.add(game);
    }

    public void updateGameList(List<GameBase> gameList) {
        this.gameInfoList.clear();
        for(GameBase gameBase : gameList) {
            this.gameInfoList.add(gameBase);
        }
    }

    public ObservableList<GameBase> getGameInfoList() {
        return gameInfoList;
    }

    public void waitingGamesReceived(List<GameBase> content) {
        this.updateGameList(content);
    }
}
