/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Profile;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class ListWindowController implements Initializable {

    @FXML
    private SplitMenuButton menu;
    @FXML
    private MenuItem miProfile;
    @FXML
    private MenuItem miReviews;
    @FXML
    private MenuItem miMainMenu;
    @FXML
    private MenuItem miLogOut;
    @FXML
    private Text listName;
    @FXML
    private TableView<String> tableLists;
    @FXML
    private TableColumn<String, String> tcGame;
    @FXML
    private TableColumn<?, ?> tcRelease;
    @FXML
    private TableColumn<?, ?> tcPlatform;
    @FXML
    private TableColumn<?, ?> tcPegi;
    @FXML
    private TableColumn<?, ?> tcCheckBox;
    @FXML
    private ComboBox<?> combLists;
    @FXML
    private VBox vbLists;
    @FXML
    private Button bttnRemove;
    @FXML
    private Button bttnAdd;

    private Profile profile;
    private Controller cont;

    private int number;

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
        Button button = new Button("+ New List");
        button.setMinWidth(menu.getPrefWidth() - 50);
        button.setStyle("-fx-background-radius: 30px;");
        button.setOnAction(e ->
        {
            newList();
        });

        vbLists.getChildren().add(button);
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

        miMainMenu.setOnAction((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.MainMenuWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("MAIN MENU");
                stage.setResizable(false);
                stage.show();

                Stage currentStage = (Stage) menu.getScene().getWindow();
                currentStage.close();
            } catch (IOException ex) {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miLogOut.setOnAction((event) -> {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    public void loadLists() {
        HashMap<String, ArrayList> lists = profile.getLists();
        for (Map.Entry<String, ArrayList> entry : lists.entrySet()) {
            String listName = entry.getKey();
            ArrayList<String> games = entry.getValue();

            Button button = new Button(listName);
            button.setMinWidth(menu.getPrefWidth() - 50);
            button.setStyle("-fx-background-radius: 30px;");
            vbLists.getChildren().add(button);
        }
    }

    public void showList(Button button) {
        ArrayList<String> list = profile.getLists().get(button.getText());
        for(String name: list)
        {
            tableLists.getItems().add(name);
        }
    }

    public void newList() {
        number++;
        String name = "New List " + number;

        profile.newList(name, new ArrayList<String>());
        
        Button button = new Button(name);
        button.setMinWidth(menu.getPrefWidth() - 50);
        button.setStyle("-fx-background-radius: 30px;");

        button.setOnAction(e ->
        {
            showList(button);
        });
        
        vbLists.getChildren().add(button);
    }

    public void test() {
        ArrayList<String> games = new ArrayList<String>();
        games.add("Animal Crossing New Horizons");
        games.add("Owlboy");
        profile.newList("NDSW", games);
        games = new ArrayList<String>();
        games.add("Outlast");
        games.add("Detroit: Become Human");
        profile.newList("PlayStation", games);
    }
}
