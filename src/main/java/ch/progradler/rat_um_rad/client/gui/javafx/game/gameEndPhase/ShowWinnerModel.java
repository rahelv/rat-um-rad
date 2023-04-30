package ch.progradler.rat_um_rad.client.gui.javafx.game.gameEndPhase;

import ch.progradler.rat_um_rad.shared.models.VisiblePlayer;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;

import java.util.ArrayList;
import java.util.List;

public class ShowWinnerModel {
    String winnerName;
    String winnerScore;
    ClientGame clientGame;

    public ShowWinnerModel(ClientGame clientGame) {
        this.clientGame = clientGame;
        winnerInfo();
    }

    private void winnerInfo() {
        List<VisiblePlayer> otherPlayers = new ArrayList<>(this.clientGame.getOtherPlayers());
        otherPlayers.sort((o1, o2) -> o2.getScore() - o1.getScore());

        int highestScoreFromOtherPlayers = otherPlayers.get(0).getScore();
        String winnerNameFromOtherPlayers = otherPlayers.get(0).getName();

        int myScore = this.clientGame.getOwnPlayer().getScore();
        String myName = this.clientGame.getOwnPlayer().getName();

        if (highestScoreFromOtherPlayers > myScore) {
            this.winnerName = winnerNameFromOtherPlayers;
            this.winnerScore = highestScoreFromOtherPlayers + "";
        } else if (highestScoreFromOtherPlayers < myScore) {
            this.winnerName = myName;
            this.winnerScore = myScore + "";
        } else {
            this.winnerName = winnerNameFromOtherPlayers + " and " + myName;
            this.winnerScore = myScore + "";
        }
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getWinnerScore() {
        return winnerScore;
    }
}
