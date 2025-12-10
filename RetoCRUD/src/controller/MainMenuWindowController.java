/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Profile;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class MainMenuWindowController implements Initializable {

    @FXML
    private SplitMenuButton menu;
    @FXML
    private MenuItem miProfile;
    @FXML
    private MenuItem miLists;
    @FXML
    private MenuItem miReviews;
    @FXML
    private MenuItem miLogOut;
    @FXML
    private TextField searchBar;
    @FXML
    private Button bttnSearch;
    @FXML
    private CheckBox chkNintendo;
    @FXML
    private CheckBox chkPC;
    @FXML
    private CheckBox chkPlayStation;
    @FXML
    private CheckBox chkXbox;
    @FXML
    private CheckBox chkPegi3;
    @FXML
    private CheckBox chkPegi7;
    @FXML
    private CheckBox chkPegi12;
    @FXML
    private CheckBox chkPegi16;
    @FXML
    private CheckBox chkPegi18;
    @FXML
    private TextField txtFromDate;
    @FXML
    private TextField txtToDate;
    @FXML
    private TableView<?> tableReview;
    @FXML
    private TableColumn<?, ?> tcGame;
    @FXML
    private TableColumn<?, ?> tcRelease;
    @FXML
    private TableColumn<?, ?> tcPlatform;
    @FXML
    private TableColumn<?, ?> tcPegi;
    @FXML
    private TableColumn<?, ?> tcCheckBox;

    private Profile profile;
    private Controller cont;

    public void setUsuario(Profile profile) {
        this.profile = profile;
        menu.setText(profile.getUsername());
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        miProfile.setOnAction((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("PROFILE MENU");
                stage.setResizable(false);
                stage.show();
                
                Stage currentStage = (Stage) menu.getScene().getWindow();
                currentStage.close();
            } catch (IOException ex) {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miLists.setOnAction((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ListWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.ListWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("LISTS");
                stage.setResizable(false);
                stage.show();
                
                Stage currentStage = (Stage) menu.getScene().getWindow();
                currentStage.close();
            } catch (IOException ex) {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miReviews.setOnAction((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ReviewsWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.ReviewsWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("REVIEWS");
                stage.setResizable(false);
                stage.show();
                
                Stage currentStage = (Stage) menu.getScene().getWindow();
                currentStage.close();
            } catch (IOException ex) {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miLogOut.setOnAction((event) -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        });
    }
}
