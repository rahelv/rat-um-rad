package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import javafx.stage.Stage;

public class UsernameChangeController {
    private ChangeUsernameDialog changeUsernameDialog;
    private UsernameChangeModel usernameChangeModel = new UsernameChangeModel();
    private Stage stage;

    public UsernameChangeController(Stage stage) {
        this.stage = stage;
        changeUsernameDialog = new ChangeUsernameDialog(usernameChangeModel);
        changeUsernameDialog.getView();
    }
}
