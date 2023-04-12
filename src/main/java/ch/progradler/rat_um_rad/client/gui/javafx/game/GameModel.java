package ch.progradler.rat_um_rad.client.gui.javafx.game;

import ch.progradler.rat_um_rad.shared.models.game.ClientGame;

public class GameModel {
    private ClientGame clientGame;
    /**
     * 1. Warten auf Spieler in Lobby
     * 2. Game Startet
     * 3. --> Karten wählen
     * 4. --> Farbe zugewiesen, Radkarten bekommen etc.
     * 5. Karte anzeigen
     * */

    public void setClientGame(ClientGame clientGame) {
        this.clientGame = clientGame;
    }

    public ClientGame getClientGame() {
        return clientGame;
    }
}
