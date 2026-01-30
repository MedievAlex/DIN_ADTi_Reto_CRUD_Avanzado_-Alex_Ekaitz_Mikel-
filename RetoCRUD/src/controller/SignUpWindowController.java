package controller;

import javafx.scene.control.ToggleGroup;
import exception.OurException;
import static exception.ShowAlert.showAlert;
import java.awt.Desktop;
import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import logger.GeneraLog;
import model.Profile;

/**
 * Controller for the SignUp window.
 * Handles user registration and navigation to login or main menu.
 * 
 * @author ema
 */
public class SignUpWindowController implements Initializable {
    @FXML
    private TextField textFieldEmail, textFieldName, textFieldSurname, textFieldTelephone;
    @FXML
    private TextField textFieldCardN, textFieldUsername;
    @FXML
    private PasswordField textFieldPassword, textFieldCPassword;
    @FXML
    private RadioButton rButtonM, rButtonW, rButtonO;
    @FXML
    private Button buttonSignUp, buttonLogIn;

    private Controller cont;
    private ToggleGroup grupOp;

    /**
     * Sets the main controller for this signup controller.
     *
     * @param cont The main controller instance
     */
    public void setCont(Controller cont) {
        this.cont = cont;
    }

    /**
     * Navigates back to login window.
     * Closes the current signup window and opens the login interface.
     */
    @FXML
    private void login() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();
            
            controller.LogInWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setCont(this.cont);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("LOGIN");
            stage.show();
                
            Stage currentStage = (Stage) buttonLogIn.getScene().getWindow();
            currentStage.close();
        } catch (IOException ex) {
            GeneraLog.getLogger().severe("Error trying to open Login: " + ex.getMessage());
            showAlert("Error", "Failed to load Login window", Alert.AlertType.ERROR);
        }
    }

    /**
     * Validates all input fields before signup.
     * Checks email format, username length, name/surname format,
     * phone number, card number, password requirements, and gender selection.
     *
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateInputs() {
        String email = textFieldEmail.getText().trim();
        if (email.isEmpty()) {
            showAlert("Validation Error", "Email field is required", Alert.AlertType.WARNING);
            textFieldEmail.requestFocus();
            return false;
        }
        if (!isValidEmail(email)) {
            showAlert("Validation Error", "Please enter a valid email address", Alert.AlertType.WARNING);
            textFieldEmail.requestFocus();
            return false;
        }

        String username = textFieldUsername.getText().trim();
        if (username.isEmpty()) {
            showAlert("Validation Error", "Username field is required", Alert.AlertType.WARNING);
            textFieldUsername.requestFocus();
            return false;
        }
        if (username.length() < 3) {
            showAlert("Validation Error", "Username must be at least 3 characters long", Alert.AlertType.WARNING);
            textFieldUsername.requestFocus();
            return false;
        }

        String name = textFieldName.getText().trim();
        if (name.isEmpty()) {
            showAlert("Validation Error", "Name field is required", Alert.AlertType.WARNING);
            textFieldName.requestFocus();
            return false;
        }
        if (!isValidName(name)) {
            showAlert("Validation Error", "Name should only contain letters", Alert.AlertType.WARNING);
            textFieldName.requestFocus();
            return false;
        }

        String surname = textFieldSurname.getText().trim();
        if (surname.isEmpty()) {
            showAlert("Validation Error", "Surname field is required", Alert.AlertType.WARNING);
            textFieldSurname.requestFocus();
            return false;
        }
        if (!isValidName(surname)) {
            showAlert("Validation Error", "Surname should only contain letters", Alert.AlertType.WARNING);
            textFieldSurname.requestFocus();
            return false;
        }

        String telephone = textFieldTelephone.getText().trim();
        if (telephone.isEmpty()) {
            showAlert("Validation Error", "Telephone field is required", Alert.AlertType.WARNING);
            textFieldTelephone.requestFocus();
            return false;
        }
        if (!isValidPhone(telephone)) {
            showAlert("Validation Error", "Please enter a valid phone number (exactly 9 digits)", Alert.AlertType.WARNING);
            textFieldTelephone.requestFocus();
            return false;
        }

        String cardN = textFieldCardN.getText().trim();
        if (cardN.isEmpty()) {
            showAlert("Validation Error", "Card number field is required", Alert.AlertType.WARNING);
            textFieldCardN.requestFocus();
            return false;
        }
        if (!isValidCardNumber(cardN)) {
            showAlert("Validation Error", "Please enter a valid card number (2 uppercase letters + 22 digits, e.g., ES1234567890123456789012)", Alert.AlertType.WARNING);
            textFieldCardN.requestFocus();
            return false;
        }

        String pass = textFieldPassword.getText();
        if (pass.isEmpty()) {
            showAlert("Validation Error", "Password field is required", Alert.AlertType.WARNING);
            textFieldPassword.requestFocus();
            return false;
        }
        if (pass.length() < 6) {
            showAlert("Validation Error", "Password must be at least 6 characters long", Alert.AlertType.WARNING);
            textFieldPassword.requestFocus();
            return false;
        }

        String passC = textFieldCPassword.getText();
        if (passC.isEmpty()) {
            showAlert("Validation Error", "Please confirm your password", Alert.AlertType.WARNING);
            textFieldCPassword.requestFocus();
            return false;
        }

        if (!pass.equals(passC)) {
            showAlert("Validation Error", "The passwords do not match", Alert.AlertType.WARNING);
            textFieldCPassword.clear();
            textFieldCPassword.requestFocus();
            return false;
        }

        if (!rButtonM.isSelected() && !rButtonW.isSelected() && !rButtonO.isSelected()) {
            showAlert("Validation Error", "Please select a gender", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    /**
     * Validates email format using regex.
     *
     * @param email The email address to validate
     * @return true if the email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Validates name/surname contains only letters and spaces.
     * Supports Spanish characters with accents.
     *
     * @param name The name or surname to validate
     * @return true if the name contains only valid characters, false otherwise
     */
    private boolean isValidName(String name) {
        return name.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    /**
     * Validates phone number format (exactly 9 digits).
     * Removes common separators (spaces, hyphens, parentheses) before validation.
     *
     * @param phone The phone number to validate
     * @return true if the phone number format is valid, false otherwise
     */
    private boolean isValidPhone(String phone) {
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return cleanPhone.matches("\\d{9}");
    }

    /**
     * Validates card number format (2 uppercase letters + 22 digits).
     * Removes spaces and hyphens before validation.
     * Example valid format: ES1234567890123456789012
     *
     * @param cardNumber The card number to validate
     * @return true if the card number format is valid, false otherwise
     */
    private boolean isValidCardNumber(String cardNumber) {
        String cleanCard = cardNumber.replaceAll("[\\s\\-]", "");
        return cleanCard.matches("[A-Z]{2}\\d{22}");
    }

    /**
     * Signs up a new user and navigates to MainMenuWindow if successful.
     * Validates inputs, creates a new user account, logs in automatically,
     * and navigates to the main menu.
     */
    @FXML
    private void signup() {
        if (!validateInputs()) {
            return;
        }

        String email = textFieldEmail.getText().trim();
        String name = textFieldName.getText().trim();
        String surname = textFieldSurname.getText().trim();
        String telephone = textFieldTelephone.getText().trim();
        String cardN = textFieldCardN.getText().trim();
        String pass = textFieldPassword.getText();
        String username = textFieldUsername.getText().trim();
        String gender = null;

        if (rButtonM.isSelected()) gender = "Man";
        else if (rButtonW.isSelected()) gender = "Woman";
        else if (rButtonO.isSelected()) gender = "Other";

        try {
            if (cont.signUp(gender, cardN, username, pass, email, name, telephone, surname)) {
                Profile profile = cont.logIn(username, pass);
                try {
                    Stage stage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
                    Parent root = fxmlLoader.load();
                    
                    controller.MainMenuWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.setCont(this.cont);
                    controllerWindow.setUsuario(profile);
                    
                    MenuItem fullScreen = new MenuItem("Full screen");
                        
                    ContextMenu contextMenu = new ContextMenu();
                    contextMenu.getItems().add(fullScreen);

                    fullScreen.setOnAction(event -> stage.setFullScreen(true));

                    root.setOnContextMenuRequested(event -> {
                        contextMenu.show(root, event.getScreenX(), event.getScreenY());
                    });
                    
                    stage.setScene(new Scene(root));
                    stage.setTitle("MAIN MENU");
                    stage.show();
                    
                    Stage currentStage = (Stage) buttonSignUp.getScene().getWindow();
                    currentStage.close();
                } catch (IOException ex) {
                    GeneraLog.getLogger().severe("Error trying to open Main Menu: " + ex.getMessage());
                    showAlert("Error", "Failed to load Main Menu window", Alert.AlertType.ERROR);
                }
            }
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Error in Sign Up: " + ex.getMessage());
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Initializes the controller class.
     * Sets up the toggle group for gender radio buttons.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown
     * @param rb The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        grupOp = new ToggleGroup();
        rButtonM.setToggleGroup(grupOp);
        rButtonW.setToggleGroup(grupOp);
        rButtonO.setToggleGroup(grupOp);
    }
    
    /**
     * Handles the video tutorial action by opening a YouTube tutorial video in a WebView.
     * Opens the video in full screen mode.
     */
    @FXML
    public void handleVideoAction() {
        try {
            cont.openVideo();
        } catch (Exception ex) {
            GeneraLog.getLogger().severe("Failed to load video: " + ex.getMessage());
            showAlert("Error", "Failed to open user manual", Alert.AlertType.ERROR);
        }
    }

    /**
     * Handles the help action by opening the user manual PDF file.
     * Displays a warning if the file is not found.
     */
    @FXML
    public void handleHelpAction() {
        try {
            cont.openManual();
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Erron in user manual: " + ex.getMessage());
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Handles the print action by generating a report.
     * Creates an empty report when triggered from the signup screen.
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