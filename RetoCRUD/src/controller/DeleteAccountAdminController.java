package controller;

import exception.OurException;
import static exception.ShowAlert.showAlert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.ArrayList;
import javafx.stage.Stage;
import model.Profile;
import javafx.scene.control.ComboBox;
import logger.GeneraLog;

/**
 * FXML Controller class for deleting user accounts as an Admin.
 * 
 * @author ema
 */
public class DeleteAccountAdminController implements Initializable
{
    @FXML
    private ComboBox<String> ComboBoxUser;

    @FXML
    private TextField TextFieldPassword;

    private Controller cont;
    private Profile profile;

    @FXML
    private Button Button_Cancel;
    @FXML
    private Button Button_Delete;

    /**
     * Sets the main controller for this controller.
     *
     * @param cont The main controller instance
     */
    public void setCont(Controller cont)
    {
        this.cont = cont;
    }

    /**
     * Sets the profile of the currently logged-in admin.
     *
     * @param profile The profile of the admin
     */
    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }

    /**
     * Populates the combo box with a list of users from the database.
     * Clears existing items and adds all retrieved usernames.
     */
    public void setComboBoxUser()
    {
        try
        {
            ArrayList<String> users = cont.comboBoxInsert();
            ComboBoxUser.getItems().clear();
            ComboBoxUser.getItems().addAll(users);
        }
        catch (OurException ex)
        {
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the cancel button click event.
     * Closes the current window and opens the main menu window.
     * Displays an error alert if the menu window cannot be opened.
     */
    @FXML
    private void cancel()
    {
        try
        {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            controller.MenuWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUsuario(profile);
            controllerWindow.setCont(this.cont);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("PROFILE MENU");
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
            currentStage.close();
        }
        catch (IOException ex)
        {
            GeneraLog.getLogger().severe("Failed to open menu window: " + ex.getMessage());
            showAlert("Error", "Failed to open menu window", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the delete button click event.
     * Validates input fields, shows a confirmation dialog, and attempts to delete the selected user account.
     * If successful, closes the current window and opens the main menu window.
     * Displays appropriate alerts for validation errors or operation failures.
     */
    @FXML
    private void delete()
    {
        if (TextFieldPassword.getText().isEmpty())
        {
            showAlert("Error", "Please enter your password to delete the account.", Alert.AlertType.ERROR);
            return;
        }

        if (ComboBoxUser.getValue() == null || ComboBoxUser.getValue().isEmpty())
        {
            showAlert("Error", "Please select a user to delete.", Alert.AlertType.ERROR);
            return;
        }

        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText("Are you sure you want to delete this account?");
        alert.setContentText("This action cannot be undone.");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK)
        {
            try
            {
                String userToDelete = ComboBoxUser.getValue();
                String adminPassword = TextFieldPassword.getText();
                String adminUsername = profile.getUsername();

                Boolean success = cont.dropOutAdmin(userToDelete, adminUsername, adminPassword);

                if (success)
                {
                    showAlert("Deleted account", "The account has been successfully deleted.", Alert.AlertType.INFORMATION);

                    try
                    {
                        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                        javafx.scene.Parent root = fxmlLoader.load();

                        controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                        controllerWindow.setUsuario(profile);
                        controllerWindow.setCont(this.cont);
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.setTitle("PROFILE MENU");
                        stage.setResizable(false);
                        stage.show();

                        Stage currentStage = (Stage) Button_Cancel.getScene().getWindow();
                        currentStage.close();

                    }
                    catch (IOException ex)
                    {
                        GeneraLog.getLogger().severe("Failed to open menu window: " + ex.getMessage());
                        showAlert("Error", "Failed to open menu window", Alert.AlertType.ERROR);
                    }
                }
            }
            catch (OurException ex)
            {
                GeneraLog.getLogger().severe("Failed to delete profile: " + ex.getMessage());
            showAlert("Error", "Failed to delete profile", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Initializes the controller class.
     * This method is automatically called after the FXML file has been loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown
     * @param rb The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}