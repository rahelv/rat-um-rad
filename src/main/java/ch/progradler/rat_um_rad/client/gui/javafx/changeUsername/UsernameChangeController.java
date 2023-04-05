package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;

public class UsernameChangeController {
    private ChangeUsernameDialog changeUsernameDialog;
    private UsernameChangeModel usernameChangeModel;
    private UsernameValidator usernameValidator = new UsernameValidator();

    public UsernameChangeController(UsernameChangeModel usernameChangeModel, ChangeUsernameDialog changeUsernameDialog) {
        this.usernameChangeModel = usernameChangeModel;
        this.changeUsernameDialog = changeUsernameDialog;
    }

    public boolean validateUsername(String username) {
        return usernameValidator.isUsernameValid(username);
    }

    public void sendChosenUsernameToServer() {
        System.out.println("username sent to server");
      //TODO: use Service to send username to server
    }
}
