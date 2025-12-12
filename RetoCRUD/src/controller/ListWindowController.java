/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.VideoGame;

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
    private TableView<VideoGame> tableLists;
    @FXML
    private TableColumn<VideoGame, String> tcGame;
    @FXML
    private TableColumn<VideoGame, Date> tcRelease;
    @FXML
    private TableColumn<VideoGame, Platform> tcPlatform;
    @FXML
    private TableColumn<VideoGame, Pegi> tcPegi;
    @FXML
    private TableColumn<VideoGame, CheckBox> tcCheckBox;
    @FXML
    private ComboBox<?> combLists;
    @FXML
    private ScrollPane spLists;
    @FXML
    private VBox vbLists;
    @FXML
    private Button bttnRemove;
    @FXML
    private Button bttnAdd;

    private Profile profile;
    private Controller cont;

    private ObservableList<VideoGame> videoGames;
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
    
    private void buttonStyle(Button button){
        button.setMinWidth(vbLists.getPrefWidth() - 10);
        button.setStyle("-fx-background-radius: 30px;");
    }

    public void loadLists() {
        HashMap<String, ArrayList> lists = profile.getLists();
        for (Map.Entry<String, ArrayList> entry : lists.entrySet()) {
            String list = entry.getKey();

            Button button = new Button(list);
            buttonStyle(button);
            button.setOnAction(e
                    -> {
                showList(button);
            });

            vbLists.getChildren().add(button);
        }
    }

    public void showList(Button button) {
        ArrayList<VideoGame> list = profile.getLists().get(button.getText());
        listName.setText(button.getText());
        
        tcGame.setCellValueFactory(new PropertyValueFactory<VideoGame, String>("v_name"));
        tcRelease.setCellValueFactory(new PropertyValueFactory<VideoGame, Date>("v_release"));
        tcPlatform.setCellValueFactory(new PropertyValueFactory<VideoGame, Platform>("v_platform"));
        tcPegi.setCellValueFactory(new PropertyValueFactory<VideoGame, Pegi>("v_pegi"));

        videoGames = FXCollections.observableArrayList();
        for (VideoGame game : list) {
            videoGames.add(game);
        }
        tableLists.setItems(videoGames);
    }

    public void newList() {
        number++;
        String name = "New List " + number;

        profile.newList(name, new ArrayList<VideoGame>());

        Button button = new Button(name);
        buttonStyle(button);

        button.setOnAction(e
                -> {
            showList(button);
        });

        vbLists.getChildren().add(button);
    }

    public void test() {
        ArrayList<VideoGame> games = new ArrayList<VideoGame>();
        games.add(new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        games.add(new VideoGame(3, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.newList("NDSW", games);

        games = new ArrayList<VideoGame>();
        games.add(new VideoGame(4, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
        games.add(new VideoGame(2, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        profile.newList("PlayStation", games);
        
        profile.newGame("All my Games", new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.newGame("All my Games", new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));        
        profile.newGame("All my Games", new VideoGame(2, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        profile.newGame("All my Games", new VideoGame(3, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.newGame("All my Games", new VideoGame(4, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Button button = new Button("+ New List");
        buttonStyle(button);
        button.setOnAction(e
                -> {
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
}
