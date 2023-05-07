package ch.progradler.rat_um_rad.client.gui.javafx.game.gameEndPhase;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;


public class EndPhaseController {
    public ListView<String> rankingListView;
    public Label achievedGoalCardsNum;
    public Label achievedGoalCardsScore;
    public Label notAchievedGoalCardsNum;
    public Label notAchievedGoalCardsScore;
    public Label winnerNameLabel;
    public Label winnerScoreLabel;
    public Label totalScore;
    Stage stage;
    private EndPhaseModel endPhaseModel;

    public void initData(EndPhaseModel endPhaseModel, Stage stage) {
        this.endPhaseModel = endPhaseModel;
        this.stage = stage;
        updateData();
    }

    private void updateData() {
        this.rankingListView.setItems(endPhaseModel.getRankingList());
        totalScore.setText(endPhaseModel.getMyTotalScore());

        achievedGoalCardsNum.setText(endPhaseModel.getNumOfAchievedShorts());
        notAchievedGoalCardsNum.setText(endPhaseModel.getNumOfNotAchievedShorts());

        achievedGoalCardsScore.setText(endPhaseModel.getPositiveScoreOfAchievedShorts());
        notAchievedGoalCardsScore.setText(endPhaseModel.getNegativeScoreOfNotAchievedShorts());

        winnerScoreLabel.setText(endPhaseModel.getWinnerScore());
        winnerNameLabel.setText(endPhaseModel.getWinnerName());
    }

    @FXML
    private void returnToStartupPage(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            endPhaseModel.getListener().controllerChanged("showStartupPage");
        });
    }
}
