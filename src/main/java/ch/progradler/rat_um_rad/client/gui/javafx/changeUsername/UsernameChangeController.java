package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.models.User;
import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.client.services.UserService;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UsernameChangeController implements Initializable {
    private UsernameChangeDialogView usernameChangeDialogView;
    private UsernameChangeModel usernameChangeModel;
    private UsernameValidator usernameValidator = new UsernameValidator();
    private IUserService userService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userService = new UserService();
    }

    public void initData(UsernameChangeModel usernameChangeModel) {
        this.usernameChangeModel = usernameChangeModel;
    }

    interface UsernameEntered {
        /**
         * @return error if there was one
         */
        String onEntered();
    }

    public UsernameChangeController(UsernameChangeModel usernameChangeModel, UsernameChangeDialogView usernameChangeDialogView, IUserService userService) {
        this.usernameChangeDialogView = usernameChangeDialogView;
        this.userService = userService;
    }

    public void updateView() {
        //TODO: unittest!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        usernameChangeDialogView.displayUsernameChangedDialog(usernameChangeModel,() -> {
            String username = usernameChangeModel.getChosenUsername();
            if(!validateUsername(username)) return "Invalid username. See: " + usernameChangeModel.getUsernameRules();
            if(username.equals(usernameChangeModel.getCurrentUsername())){
                return usernameChangeModel.getChosenUsername() + " is already your username";
                //TODO: tell user to choose another name or cancel the action
            }
            sendChosenUsernameToServer();
            return null;
        });
    }

    public boolean validateUsername(String username) {
        return usernameValidator.isUsernameValid(username);
    }

    public void sendChosenUsernameToServer() {
        try {
            this.userService.sendChosenUsernameToServer(this.usernameChangeModel.getChosenUsername());
        } catch (IOException e) {
            // TODO: remove
            e.printStackTrace();
        }
        //TODO: use Service to send username to server
    }

    public void setConfirmedUsername(String newName) {
        System.out.println("Username confirmed in UsernameChangeController: " + newName);
        // TODO: implement
    }
}
