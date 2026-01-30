package controller;

import static exception.ShowAlert.showAlert;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logger.GeneraLog;
import model.*;

/**
 * Controller for the main Menu window. Handles navigation to modify, delete,
 * and logout actions.
 * 
 * @author ema
 */
public class MenuWindowController implements Initializable {

    @FXML
    private Button Button_Delete;

    @FXML
    private Button Button_Modify;

    @FXML
    private Button Button_LogOut;

    @FXML
    private Label label_Username;

    private Profile profile;
    private Controller cont;
    private Stage parentStage;

    /**
     * Sets the user profile and updates the username label.
     *
     * @param profile The user's profile object
     */
    public void setUsuario(Profile profile) {
        this.profile = profile;
        label_Username.setText(profile.getUsername());
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
     * Gets the main controller instance.
     *
     * @return The main controller
     */
    public Controller getCont() {
        return cont;
    }
    
    /**
     * Sets the parent stage that opened this window.
     *
     * @param parentStage The parent stage reference
     */
    public void setParentStage(Stage parentStage) {
        this.parentStage = parentStage;
    }

    /**
     * Opens the Modify window.
     * Closes the current window and displays the user modification form.
     *
     * @param event The action event that triggered this method
     */
    @FXML
    private void modifyVentana(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            controller.ModifyWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setProfile(profile);
            controllerWindow.setCont(this.cont);

            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            stage.setScene(new Scene(root));
            stage.setTitle("MODIFY");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            Stage currentStage = (Stage) Button_Modify.getScene().getWindow();
            currentStage.close();

        } catch (IOException ex) {
            GeneraLog.getLogger().severe("Failed to open modify window: " + ex.getMessage());
            showAlert("Error", "Failed to open modify window", Alert.AlertType.ERROR);
        }
    }

    /**
     * Opens the Delete Account window depending on profile type. Users open
     * DeleteAccount; Admins open DeleteAccountAdmin.
     * Closes the current window and displays the appropriate delete interface.
     */
    @FXML
    private void delete() {
        try {
            FXMLLoader fxmlLoader;
            if (profile instanceof User) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccount.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                controller.DeleteAccountController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);
                controllerWindow.setParentStage(parentStage);

                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                stage.setScene(new Scene(root));
                stage.setTitle("DELETE");
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();

            } else if (profile instanceof Admin) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/DeleteAccountAdmin.fxml"));
                javafx.scene.Parent root = fxmlLoader.load();
                controller.DeleteAccountAdminController controllerWindow = fxmlLoader.getController();
                controllerWindow.setProfile(profile);
                controllerWindow.setCont(cont);
                controllerWindow.setComboBoxUser();

                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
                stage.setScene(new Scene(root));
                stage.setTitle("DELETE");
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

                Stage currentStage = (Stage) Button_Delete.getScene().getWindow();
                currentStage.close();
            }
        } catch (IOException ex) {
            GeneraLog.getLogger().severe("Failed to open delete window: " + ex.getMessage());
            showAlert("Error", "Failed to open delete window", Alert.AlertType.ERROR);
        }
    }

    /**
     * Closes the current window (used for logout).
     *
     * @param event The action event that triggered this method
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
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
        // Initialization logic if needed
    }
}