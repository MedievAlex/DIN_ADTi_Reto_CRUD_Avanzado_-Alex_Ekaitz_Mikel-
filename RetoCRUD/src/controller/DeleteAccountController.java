package controller;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import model.Profile;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Controller for the Delete Account window for regular Users. This controller
 * allows a user to delete their own account.
 */
public class DeleteAccountController implements Initializable
{
    @FXML
    private Label LabelUsername;
    @FXML
    private TextField TextFieldPassword;
    @FXML
    private Button Button_Cancel;
    @FXML
    private Button Button_Delete;

    private Controller cont;

    private Profile profile;
    
    private Stage parentStage;

    /**
     * Sets the Controller instance.
     *
     * @param cont Controller object
     */
    public void setCont(Controller cont)
    {
        this.cont = cont;
    }

    /**
     * Sets the current logged-in profile and updates the username label.
     *
     * @param profile Profile object
     */
    public void setProfile(Profile profile)
    {
        this.profile = profile;
        LabelUsername.setText(profile.getUsername());
    }
    
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    /**
     * Handles cancel button action. Closes the current window and returns to
     * MenuWindow.
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
            controllerWindow.setParentStage(parentStage);

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
            java.util.logging.Logger.getLogger(MenuWindowController.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles delete button action. Confirms deletion and calls the Controller
     * to remove the user account.
     */
    @FXML
    private void delete()
    {
        if (TextFieldPassword.getText().isEmpty())
        {
            javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Password required");
            error.setContentText("Please enter your password to delete the account.");
            error.showAndWait();
            return;
        }
        Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK)
        {
            try
            {
                String user, password;
                user = LabelUsername.getText();
                password = TextFieldPassword.getText();
                Boolean success = cont.dropOutUser(user, password);
                if (success)
                {
                    Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Deleted account");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Your account has been successfully deleted.");
                    successAlert.showAndWait();

                    profile = null;

                    if (parentStage != null) {
                        parentStage.close();
                    }

                    Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                    currentStage.close();

                    try
                    {
                        javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
                        javafx.scene.Parent root = fxmlLoader.load();

                        controller.LogInWindowController controllerWindow = fxmlLoader.getController();
                        controllerWindow.setController(cont);

                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.setTitle("LOG IN");
                        stage.setResizable(false);
                        stage.show();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(DeleteAccountController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("Incorrect password");
                    error.setContentText("The password is incorrect. Please try again.");
                    error.showAndWait();
                }
            }
            catch (Exception ex)
            {
                javafx.scene.control.Alert error = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("The account could not be deleted.");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
    }
}
