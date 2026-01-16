package controller;

import exception.OurException;
import exception.ShowAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Profile;

/**
 * Controller for the Login window. Handles user login and navigation to the
 * main menu or signup window.
 */
public class LogInWindowController implements Initializable
{
    @FXML
    private TextField TextField_Username;

    @FXML
    private PasswordField PasswordField_Password;

    @FXML
    private Button Button_LogIn;

    @FXML
    private Button Button_SignUp;

    @FXML
    private Label labelIncorrecto;

    @FXML
    private Menu menuHelp;

    @FXML
    private MenuItem menuItemHelp;
    
    private Controller CONT;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuActions;
    @FXML
    private MenuItem menuItemReport;

    public void setController(Controller controller)
    {
        this.CONT = controller;
    }

    /**
     * Opens the SignUp window.
     */
    @FXML
    private void signUp()
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("SIGN IN");
            stage.setResizable(false);
            stage.show();

            controller.SignUpWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setCont(CONT);

            Stage currentStage = (Stage) Button_SignUp.getScene().getWindow();
            currentStage.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Attempts to log in the user. If successful, opens MenuWindow; otherwise,
     * shows an error.
     */
    @FXML
    private void logIn()
    {
        String username = TextField_Username.getText();
        String password = PasswordField_Password.getText();

        if (username.equals("") || password.equals(""))
        {
            labelIncorrecto.setText("Please fill in both fields.");
        }
        else
        {
            try
            {
                Profile profile = CONT.logIn(username, password);
                if (profile != null)
                {
                    try
                    {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
                        Parent root = fxmlLoader.load();

                        controller.MainMenuWindowController controllerWindow = fxmlLoader.getController();
                        controllerWindow.setCont(CONT);
                        controllerWindow.setUsuario(profile);

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("MAIN MENU");
                        stage.show();

                        Stage currentStage = (Stage) Button_LogIn.getScene().getWindow();
                        currentStage.close();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                {
                    labelIncorrecto.setText("The username and/or password are incorrect.");
                }
            }
            catch (OurException ex)
            {
                ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    public void handleHelpAction()
    {
        System.out.println("Help");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}