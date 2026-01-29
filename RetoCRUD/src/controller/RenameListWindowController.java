package controller;

import exception.OurException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logger.GeneraLog;
import model.Profile;

/**
 * FXML Controller class for renaming user lists.
 *
 * @author ema
 */
public class RenameListWindowController implements Initializable {

    @FXML
    private TextField listNewName;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnConfirm;
    @FXML
    private Text txtMessage;

    private Profile profile;
    private Controller cont;
    private ListWindowController parentController;
    private String listName;

    //[USERS & CONTROLLER]
    /**
     * Sets the user profile for this controller.
     *
     * @param profile The user's profile object
     */
    public void setUsuario(Profile profile) {
        this.profile = profile;
    }

    /**
     * Sets the main controller for this controller.
     *
     * @param cont The main controller instance
     */
    public void setCont(Controller cont) {
        this.cont = cont;
    }
    
    /**
     * Sets the parent controller to allow communication between windows.
     *
     * @param parentController The parent ListWindowController instance
     */
    public void setParentCont(ListWindowController parentController) {
        this.parentController = parentController;
    }

    /**
     * Gets the main controller instance.
     *
     * @return The main controller
     */
    public Controller getCont() {
        return cont;
    }

    /**
     * Sets the name of the list to be renamed.
     * Updates the message text to reflect the current list name.
     *
     * @param listName The current name of the list to rename
     */
    public void setListToRename(String listName) {
        this.listName = listName;
        txtMessage.setText(listName + " list new name:");
    }

    /**
     * Renames the selected list with the new name provided by the user.
     * Validates that the new name doesn't already exist, then updates the list.
     * Refreshes the parent controller's UI after successful rename.
     */
    private void renameList() {
        String newName = listNewName.getText();

        try {
            if (cont.verifyListName(profile.getUsername(), listNewName.getText())) {
                txtMessage.setText("List named " + newName + " already exists.");
            } else if (listNewName.getText().trim().length() == 0) {
                txtMessage.setText("List can't have an empty name.");
            } else if (listNewName.getText().length() > 20) {
                txtMessage.setText("New name can't have more than 20 characters.");
            }else {
                cont.renameList(profile.getUsername(), listName, newName);
                txtMessage.setText(listName + " updated to " + newName + ".");
                Button newNameButton = new Button(newName);
                
                parentController.loadListButtons();
                parentController.showList(newNameButton);
                parentController.setComboBox();
                close();
            }
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed renaming list: " + ex.getMessage());
        }
    }

    /**
     * Closes the rename list window.
     */
    private void close(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
    }
    
    /**
     * Sets up action handlers for the confirm and cancel buttons.
     */
    private void setOnActionHandlers() {
        btnConfirm.setOnAction(e
                -> {
            renameList();
        });

        btnCancel.setOnAction(e
                -> {
            close();
        });
    }

    /**
     * Initializes the controller class.
     * Sets up button action handlers.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown
     * @param rb The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setOnActionHandlers();
    }
}