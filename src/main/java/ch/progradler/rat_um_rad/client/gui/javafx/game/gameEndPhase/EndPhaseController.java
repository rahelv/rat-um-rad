package ch.progradler.rat_um_rad.client.gui.javafx.game.gameEndPhase;

import ch.progradler.rat_um_rad.client.gateway.InputPacketGatewaySingleton;
import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import ch.progradler.rat_um_rad.client.utils.listeners.ServerResponseListener;
import ch.progradler.rat_um_rad.shared.models.game.ClientGame;
import ch.progradler.rat_um_rad.shared.protocol.ServerCommand;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EndPhaseController {
    Stage stage;

    public ListView<String> rankingListView;

    public Label achievedGoalCardsNum;
    public Label achievedGoalCardsScore;

    public Label notAchievedGoalCardsNum;
    public Label notAchievedGoalCardsScore;

    public Label longestPathNum;
    public Label longestPathScore;

    public Label totalScore;

    private IGameService gameService;
    private EndPhaseModel endPhaseModel;

    public EndPhaseController() {
        this.gameService = new GameService();
    }

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
    }
}
