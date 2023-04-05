package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.shared.models.UsernameChange;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;

public class UsernameChangeController {
    private UsernameChangeDialogView usernameChangeDialogView;
    private UsernameChangeModel usernameChangeModel;
    private UsernameValidator usernameValidator = new UsernameValidator();
    private UserService userService;

    public UsernameChangeController(UsernameChangeModel usernameChangeModel, UsernameChangeDialogView usernameChangeDialogView, UserService userService) {
        this.usernameChangeModel = usernameChangeModel;
        this.usernameChangeDialogView = usernameChangeDialogView;
        this.userService = userService;
    }

    public boolean validateUsername(String username) {
        return usernameValidator.isUsernameValid(username);
    }

    public void sendChosenUsernameToServer() {
        System.out.println("username sent to server");
        this.userService.sendChosenUsernameToServer(this.usernameChangeModel.getChosenUsername());
      //TODO: use Service to send username to server
    }
}
