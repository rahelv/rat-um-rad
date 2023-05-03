package ch.progradler.rat_um_rad.client.gui.javafx.game.gameEndPhase;

import ch.progradler.rat_um_rad.client.services.GameService;
import ch.progradler.rat_um_rad.client.services.IGameService;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ShowWinnerController {
    public Label winnerNameLabel;
    public Label winnerScoreLabel;
    Stage stage;
    private IGameService gameService;
    private ShowWinnerModel showWinnerModel;

    public ShowWinnerController() {
        this.gameService = new GameService();
    }

    public void initData(ShowWinnerModel showWinnerModel, Stage stage) {
        this.stage = stage;
        this.showWinnerModel = showWinnerModel;

        updateData();
    }

    private void updateData() {
        winnerNameLabel.setText(showWinnerModel.getWinnerName());
        winnerScoreLabel.setText(showWinnerModel.getWinnerScore());
    }
}
