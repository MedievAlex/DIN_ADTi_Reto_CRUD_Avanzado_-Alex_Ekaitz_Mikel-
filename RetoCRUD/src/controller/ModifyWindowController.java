package controller;

import exception.OurException;
import exception.ShowAlert;
import exception.passwordequalspassword;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Profile;
import model.User;

/**
 * FXML Controller class for modifying a user's profile.
 */
public class ModifyWindowController implements Initializable {
    @FXML
    private Label LabelUsername; // Label showing current username
    @FXML
    private Label LabelEmail; // Label showing current email
    @FXML
    private TextField TextField_Name; // Field to modify name
    @FXML
    private TextField TextField_Surname; // Field to modify surname
    @FXML
    private TextField TextField_Telephone; // Field to modify telephone
    @FXML
    private TextField TextField_NewPass; // Field to enter new password
    @FXML
    private TextField TextField_CNewPass; // Field to confirm new password
    @FXML
    private Button Button_Cancel;

    private Controller cont; // Controller instance for business logic
    private Profile profile; // Currently logged-in user

    // Set controller instance
    public void setCont(Controller cont)
    {
        this.cont = cont;
    }

    // Set current profile and populate labels
    public void setProfile(Profile profile)
    {
        this.profile = profile;
        LabelUsername.setText(profile.getUsername());
        LabelEmail.setText(profile.getEmail());
    }

    // Save changes button action
    @FXML
    private void save(ActionEvent event) throws passwordequalspassword
    {
        String name = TextField_Name.getText();
        String surname = TextField_Surname.getText();
        String telephone = TextField_Telephone.getText();
        String newPass = TextField_NewPass.getText();
        String cNewPass = TextField_CNewPass.getText();

        String username = profile.getUsername();
        String email = profile.getEmail();

        if (name == null || name.isEmpty() || name.equals("Insert your new name"))
        {
            name = profile.getName();
        }

        if (surname == null || surname.isEmpty() || surname.equals("Insert your new surname"))
        {
            surname = profile.getSurname();
        }

        if (telephone == null || telephone.isEmpty() || telephone.equals("Insert your new telephone"))
        {
            telephone = profile.getTelephone();
        }

        String password = profile.getPassword();
        boolean changingPassword = false;

        if (newPass != null && !newPass.isEmpty() && !newPass.equals("New Password") &&
            cNewPass != null && !cNewPass.isEmpty() && !cNewPass.equals("Confirm New Password"))
        {
            if (!newPass.equals(cNewPass))
            {
                throw new passwordequalspassword("The passwords do not match");
            }
            password = newPass;
            changingPassword = true;
        }

        String gender = "";
        if (profile instanceof User)
        {
            gender = ((User) profile).getGender();
        }

        try
        {
            Boolean success = cont.modifyUser(password, email, name, telephone, surname, username, gender);

            if (success)
            {
                profile.setName(name);
                profile.setSurname(surname);
                profile.setTelephone(telephone);

                if (changingPassword)
                {
                    profile.setPassword(password);
                }

                ShowAlert.showAlert("Success", "User data has been successfully updated.", Alert.AlertType.INFORMATION);

                try
                {
                    javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                    javafx.scene.Parent root = fxmlLoader.load();

                    controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.setCont(this.cont);
                    controllerWindow.setUsuario(profile);

                    Stage stage = new Stage();
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
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Cancel button action: returns to MenuWindow without saving
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

            Stage stage = new Stage();
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}
