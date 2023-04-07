package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.services.IUserService;
import ch.progradler.rat_um_rad.shared.util.UsernameValidator;

import java.io.IOException;

public class UsernameChangeController {
    private UsernameChangeDialogView usernameChangeDialogView;
    private UsernameChangeModel usernameChangeModel;
    private UsernameValidator usernameValidator = new UsernameValidator();
    private IUserService userService;

    interface UsernameEntered {
        /**
         * @return error if there was one
         */
        String onEntered();
    }

    public UsernameChangeController(UsernameChangeModel usernameChangeModel, UsernameChangeDialogView usernameChangeDialogView, IUserService userService) {
        this.usernameChangeModel = usernameChangeModel;
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
        System.out.println("username sent to server");
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
