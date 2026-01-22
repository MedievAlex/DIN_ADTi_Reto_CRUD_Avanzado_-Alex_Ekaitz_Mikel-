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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;
import model.Profile;
import javafx.scene.control.ComboBox;

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

    public void setCont(Controller cont)
    {
        this.cont = cont;
    }

    public void setProfile(Profile profile)
    {
        this.profile = profile;
    }

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
            Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
                        Logger.getLogger(MenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            catch (OurException ex)
            {
                showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
        else
        {
            System.out.println("Deletion cancelled by the user.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}
