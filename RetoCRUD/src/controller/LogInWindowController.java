package controller;

import exception.OurException;
import static exception.ShowAlert.showAlert;
import java.awt.Desktop;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import logger.GeneraLog;
import model.Profile;

/**
 * Controller for the Login window. Handles user login and navigation to the
 * main menu or signup window.
 * 
 * @author ema
 */
public class LogInWindowController implements Initializable {
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

    private Controller cont;

    /**
     * Sets the main controller for this login controller.
     *
     * @param controller The main controller instance
     */
    public void setCont(Controller controller) {
        this.cont = controller;
    }

    /**
     * Opens the SignUp window.
     * Closes the current login window and displays the signup form.
     */
    @FXML
    private void signUp() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("SIGN UP");
            stage.show();

            controller.SignUpWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setCont(cont);

            Stage currentStage = (Stage) Button_SignUp.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            GeneraLog.getLogger().severe("Error trying to open Sign Up: " + ex.getMessage());
            showAlert("Error", "Trying to open Sign Up", Alert.AlertType.ERROR);
        }
    }

    /**
     * Attempts to log in the user. If successful, opens MenuWindow; otherwise,
     * shows an error.
     * Validates input fields, authenticates credentials, and navigates to the main menu.
     */
    @FXML
    private void logIn() {
        String username = TextField_Username.getText();
        String password = PasswordField_Password.getText();

        if (username.equals("") || password.equals("")) {
            labelIncorrecto.setText("Please fill in both fields.");
        } else {
            try {
                Profile profile = cont.logIn(username, password);
                if (profile != null)
                {
                    try
                    {
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
                        Parent root = fxmlLoader.load();

                        controller.MainMenuWindowController controllerWindow = fxmlLoader.getController();
                        controllerWindow.setCont(cont);
                        controllerWindow.setUsuario(profile);
                        
                        MenuItem fullScreen = new MenuItem("Full screen");
                        
                        ContextMenu contextMenu = new ContextMenu();
                        contextMenu.getItems().addAll(fullScreen);
                        
                        fullScreen.setOnAction(event -> stage.setFullScreen(true));
                        
                        root.setOnContextMenuRequested(event -> {
                            contextMenu.show(root, event.getScreenX(), event.getScreenY());
                        });

                        stage.setScene(new Scene(root));
                        stage.setTitle("MAIN MENU");
                        stage.show();

                        Stage currentStage = (Stage) Button_LogIn.getScene().getWindow();
                        currentStage.close();
                        
                        GeneraLog.getLogger().info("Logged correctly: " + profile.getUsername());
                    } catch (IOException ex) {
                        GeneraLog.getLogger().severe("Error trying to open Main Menu: " + ex.getMessage());
                        showAlert("Error", "Trying to open Main Menu", Alert.AlertType.ERROR);
                    }
                } else {
                    labelIncorrecto.setText("The username and/or password are incorrect.");
                }
            } catch (OurException ex) {
                GeneraLog.getLogger().severe("Error trying to login: " + ex.getMessage());
                showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Initializes the controller class.
     * This method is called automatically after the FXML file has been loaded.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown
     * @param rb The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Handles the video tutorial action by opening a YouTube tutorial video in a WebView.
     * Opens the video in full screen mode.
     */
    @FXML
    public void handleVideoAction() {
        try {
            WebView webview = new WebView();
            webview.getEngine().load("https://youtu.be/phyKDIryZWk?si=ugkWCRi_GpBrg_0z");
            webview.setPrefSize(640, 390);

            Stage stage = new Stage();
            stage.setScene(new Scene(webview));
            stage.setTitle("Tutorial Video");
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception ex) {
            GeneraLog.getLogger().severe("Failed to load video: " + ex.getMessage());
            showAlert("Error", "Failed to load video", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the help action by opening the user manual PDF file.
     * Displays a warning if the file is not found.
     */
    @FXML
    public void handleHelpAction() {
        try {
            File path = new File("user manual/UserManual.pdf");
            if (!path.exists()) {
                GeneraLog.getLogger().warning("User manual not found at: " + path.getAbsolutePath());
                showAlert("File Not Found", "User manual not found at: " + path.getAbsolutePath(), Alert.AlertType.WARNING);
                return;
            }
            Desktop.getDesktop().open(path);
        } catch (IOException ex) {
            GeneraLog.getLogger().severe("Failed to open user manual: " + ex.getMessage());
            showAlert("Error", "Failed to open user manual", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Handles the print action by generating a report.
     * Creates an empty report when triggered from the login screen.
     */
    @FXML
    public void handleImprimirAction() {
        try {
            cont.generateReport("");
        }
        catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed to generate report: " + ex.getMessage());
            showAlert("Error", "Failed to generate report", Alert.AlertType.ERROR);
        }
    }
}