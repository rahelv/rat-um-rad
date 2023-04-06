package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.services.IUserService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * handbook: shown in menubar (help - handbook)
 * dialog for username input : client: username suggestion based on system username which is shown in usernameField
 *
 */

public class UsernameChangeDialogView {
    private Stage stage;
    private UsernameChangeModel usernameChangeModel;
    private UsernameChangeController usernameChangeController;

    public UsernameChangeDialogView(Stage stage, IUserService userService) {
        this.usernameChangeModel = new UsernameChangeModel();
        this.usernameChangeController = new UsernameChangeController(usernameChangeModel, this, userService);
    }

    public void getView() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Please choose your username");
        dialog.setHeaderText(this.usernameChangeModel.getUsernameRules());
        dialog.initModality(Modality.APPLICATION_MODAL);

        //Buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //Labels and fields
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField(this.usernameChangeModel.getSystemUsername());
        gridPane.add(new Label("Please enter your username: "), 0, 0);
        gridPane.add(username, 1, 0);

        username.textProperty().bindBidirectional(usernameChangeModel.chosenUsernameProperty());

        Button ok = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        ok.addEventFilter(ActionEvent.ACTION, event -> {
            if (!this.usernameChangeController.validateUsername(this.usernameChangeModel.getChosenUsername())) {
                Label invalidLabel = new Label(this.usernameChangeModel.getUsernameRules()); //TODO: Darstellung anpassen
                gridPane.add(invalidLabel, 0, 2);
                event.consume();
            }
            else if (this.usernameChangeModel.getChosenUsername() == this.usernameChangeModel.getCurrentUsername()) {
                Label invalidLabel = new Label(this.usernameChangeModel.getChosenUsername() + " is already your username"); //TODO: Darstellung anpassen
                gridPane.add(invalidLabel, 0, 2);
                event.consume();
                //TODO: tell user to choose another name or cancel the action
            }
            else {
                this.usernameChangeController.sendChosenUsernameToServer();
            }
        });

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        dialog.show();
    }
}
