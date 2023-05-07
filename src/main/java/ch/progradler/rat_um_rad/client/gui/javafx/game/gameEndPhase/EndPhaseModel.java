package ch.progradler.rat_um_rad.client.gui.javafx.game.gameEndPhase;

import ch.progradler.rat_um_rad.client.utils.listeners.ControllerChangeListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.models.game.Player;
import ch.progradler.rat_um_rad.shared.models.game.PlayerBase;
import ch.progradler.rat_um_rad.shared.models.game.PlayerEndResult;
import ch.progradler.rat_um_rad.shared.models.game.cards_and_decks.DestinationCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class EndPhaseModel {
    private final ControllerChangeListener<?> listener;
    ObservableList<String> rankingList;
    ClientGame clientGame;
    String myTotalScore;
    int numOfAchievedShorts;
    int numOfNotAchievedShorts;

    int scoreOfAchievedShorts;
    int scoreOfNotAchievedShorts;

    int winnerScore;
    String winnerName;

    public EndPhaseModel(ClientGame clientGame, ControllerChangeListener<?> listener) {
        this.listener = listener;
        this.clientGame = clientGame;
        rankingList = FXCollections.observableArrayList();
        playersInfo();
        myOwnGameAchievements();
    }

    public ObservableList<String> getRankingList() {
        return rankingList;
    }

    /**
     * descending sort based on scores,first 5 players, who have the highest scores, will be saved in rankingList.
     */
    private void playersInfo() {
        List<PlayerBase> allPlayers = new ArrayList<>(this.clientGame.getOtherPlayers());
        allPlayers.add(this.clientGame.getOwnPlayer());
        allPlayers.sort((o1, o2) -> o2.getScore() - o1.getScore());

        int rankingNum = 0;
        for (PlayerBase player : allPlayers) {
            rankingList.add((++rankingNum) + "     " + player.getName() + "     " + player.getScore());
        }
        this.winnerName = allPlayers.get(0).getName();
        this.winnerScore = allPlayers.get(0).getScore();
    }

    /**
     * list my own achievements on scene in details : number/scores of achieved / notAchieved destination cards
     */
    private void myOwnGameAchievements() {
        Player ownPlayer = this.clientGame.getOwnPlayer();
        myTotalScore = ownPlayer.getScore() + "";

        PlayerEndResult ownPlayerEndResult = ownPlayer.getEndResult();
        this.numOfAchievedShorts = ownPlayerEndResult.getAchievedShorts().size();
        this.numOfNotAchievedShorts = ownPlayerEndResult.getNotAchievedShorts().size();

        List<DestinationCard> achievedShortsList = ownPlayerEndResult.getAchievedShorts();
        int sumAchievedScore = 0;
        for (DestinationCard destinationCard : achievedShortsList) {
            sumAchievedScore = sumAchievedScore + destinationCard.getPoints();
        }
        this.scoreOfAchievedShorts = sumAchievedScore;

        List<DestinationCard> notAchievedShortsList = ownPlayerEndResult.getNotAchievedShorts();
        int sumNotAchievedScore = 0;
        for (DestinationCard destinationCard : notAchievedShortsList) {
            sumNotAchievedScore = sumNotAchievedScore + destinationCard.getPoints();
        }
        this.scoreOfNotAchievedShorts = sumNotAchievedScore;
        //TODO : infos of long destination cards should be given from server
    }

    public String getMyTotalScore() {
        return "You got totally : " + myTotalScore + " points";
    }

    public String getNumOfAchievedShorts() {
        return numOfAchievedShorts + "";
    }

    public String getNumOfNotAchievedShorts() {
        return numOfNotAchievedShorts + "";
    }

    public String getPositiveScoreOfAchievedShorts() {
        return "+ " + scoreOfAchievedShorts;
    }

    public String getNegativeScoreOfNotAchievedShorts() {
        return "- " + scoreOfNotAchievedShorts;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String getWinnerScore() {
        return winnerScore + " points";
    }

    public ControllerChangeListener<?> getListener() {
        return listener;
    }
}
