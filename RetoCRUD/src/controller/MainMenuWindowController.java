package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.VideoGame;

/**
 * FXML Controller class
 *
 * @author ema
 */
public class MainMenuWindowController implements Initializable {

    @FXML
    private MenuButton menu;
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
    private TableView<VideoGame> tableGames;
    @FXML
    private TableColumn<VideoGame, String> tcGame;
    @FXML
    private TableColumn<VideoGame, Date> tcRelease;
    @FXML
    private TableColumn<VideoGame, Platform> tcPlatform;
    @FXML
    private TableColumn<VideoGame, Pegi> tcPegi;
    @FXML
    private TableColumn<VideoGame, Boolean> tcCheckBox;

    private Profile profile;
    private Controller cont;
    private ObservableList<VideoGame> videoGames;

    public void setUsuario(Profile profile) {
        this.profile = profile;
        menu.setText(profile.getUsername());
        loadVideoGames();
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    private void setMenuOptions() {
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
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
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

                controllerWindow.test();
                controllerWindow.loadLists();
                controllerWindow.setComboBox();

                Stage stage = (Stage) menu.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("LISTS");
                stage.setResizable(false);
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

               Stage stage = (Stage) menu.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("REVIEWS");
                stage.setResizable(false);
            } catch (IOException ex) {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miLogOut.setOnAction((event) -> {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    private void setOnActionHandlers() {

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setMenuOptions();
        setOnActionHandlers();
        tableGames.setSelectionModel(null);
    }

    private void loadVideoGames()
    {
        ArrayList<VideoGame> allGames = new ArrayList<>();
        allGames.add(new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        allGames.add(new VideoGame(4, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        allGames.add(new VideoGame(2, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        allGames.add(new VideoGame(3, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
        allGames.add(new VideoGame(5, "Call of Duty: Black Ops II", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        
        ArrayList<VideoGame> myGames = profile.getLists().get("My Games");
        if (myGames == null)
        {
            myGames = new ArrayList<>();
        }

        for (VideoGame game : allGames)
        {
            if (myGames.stream().anyMatch(g -> g.getV_id() == game.getV_id()))
            {
                game.setChecked(true);
            }

            game.checkedProperty().addListener((obs, oldVal, newVal) ->
            {
                if (newVal)
                {
                    if (!profile.getLists().get("My Games").contains(game))
                    {
                        profile.addGame("My Games", game);
                        cont.addGameToDB(profile, game);
                    }
                }
                else
                {
                    if (profile.getLists().get("My Games").contains(game))
                    {
                        profile.removeGame("My Games", game);
                        cont.removeGameFromDB(profile, game);
                    }
                }
            });
        }

        videoGames = FXCollections.observableArrayList(allGames);

        tcGame.setCellValueFactory(new PropertyValueFactory<>("v_name"));
        tcRelease.setCellValueFactory(new PropertyValueFactory<>("v_release"));
        tcPlatform.setCellValueFactory(new PropertyValueFactory<>("v_platform"));
        tcPegi.setCellValueFactory(new PropertyValueFactory<>("v_pegi"));

        tcCheckBox.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
        tcCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(tcCheckBox));

        tableGames.setItems(videoGames);
    }
}
