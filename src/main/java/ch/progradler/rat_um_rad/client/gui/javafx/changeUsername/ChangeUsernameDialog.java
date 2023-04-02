package ch.progradler.rat_um_rad.client.gui.javafx.changeUsername;

import ch.progradler.rat_um_rad.client.utils.ComputerInfo;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;

import javax.swing.plaf.synth.Region;
import java.util.Optional;

/**
 * handbook: shown in menubar (help - handbook)
 * dialog for username input : client: username suggestion based on system username which is shown in usernameField
 * TODO: refactoring, binding chosenUsername to inputField, add EventHandlers to Buttons (connect to UserController)
 */


public class ChangeUsernameDialog  {
    private UsernameChangeModel usernameChangeModel;
    ComputerInfo computerInfo;
    public ChangeUsernameDialog(UsernameChangeModel usernameChangeModel) {
        this.usernameChangeModel = usernameChangeModel;
        computerInfo = new ComputerInfo(); //TODO: move to viewModel
    }

    public void getView() {
        TextInputDialog textInputDialog = new TextInputDialog(computerInfo.getSystemUsername());
        textInputDialog.setGraphic(null);
        textInputDialog.initModality(Modality.APPLICATION_MODAL);


        textInputDialog.setTitle("Please choose your username");
        textInputDialog.setHeaderText(this.usernameChangeModel.getUsernameRules());
        textInputDialog.setContentText("Please enter your username: ");

        TextField inputField = textInputDialog.getEditor();
       // inputField.textProperty().bindBidirectional(usernameChangeModel.chosenUsernameProperty());
        //TODO

        Optional<String> result = textInputDialog.showAndWait();
        result.ifPresent(name -> System.out.println("Your name: " + name));

        //TODO: add eventhandlers to buttons
    }
}
