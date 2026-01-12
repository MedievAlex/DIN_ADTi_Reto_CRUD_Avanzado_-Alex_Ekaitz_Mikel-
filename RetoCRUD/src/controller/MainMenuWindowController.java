package controller;

import exception.OurException;
import exception.ShowAlert;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.VideoGame;

public class MainMenuWindowController implements Initializable
{
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
    private Button toggleFiltersButton;
    @FXML
    private VBox filtersContainer;
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
    private CheckBox chkPegi6;
    @FXML
    private CheckBox chkPegi12;
    @FXML
    private CheckBox chkPegi16;
    @FXML
    private CheckBox chkPegi18;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
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
    private boolean filtersVisible = false;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuActions;
    @FXML
    private MenuItem menuItemReport;
    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem menuItemHelp;

    public void setUsuario(Profile profile)
    {
        this.profile = profile;
        menu.setText(profile.getUsername());
        loadVideoGames();
    }

    public void setCont(Controller cont)
    {
        this.cont = cont;
    }

    public Controller getCont()
    {
        return cont;
    }

    private void setMenuOptions()
    {
        miProfile.setOnAction((event) ->
        {
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.MenuWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);
                
                Stage mainStage = (Stage) menu.getScene().getWindow();
                controllerWindow.setParentStage(mainStage);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("PROFILE MENU");
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
            catch (IOException ex)
            {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miLists.setOnAction((event) ->
        {
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ListWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.ListWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                controllerWindow.loadListButtons();
                controllerWindow.setComboBox();

                Stage stage = (Stage) menu.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("LISTS");
            }
            catch (IOException ex)
            {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miReviews.setOnAction((event) ->
        {
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ReviewsWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.ReviewsWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setCont(cont);
                controllerWindow.setUsuario(profile);
                //controllerWindow.setComboBox();
                controllerWindow.loadReview();
                

                Stage stage = (Stage) menu.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("REVIEWS");
            }
            catch (IOException ex)
            {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (OurException ex) {
                Logger.getLogger(MainMenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        miLogOut.setOnAction((event) ->
        {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    private void setOnActionHandlers() 
    {
        setupAccordion();
    }

    private void setupAccordion()
    {
        toggleFiltersButton.setOnAction(event -> toggleFilters());
        hideFilters();
    }
    
    private void toggleFilters()
    {
        filtersVisible = !filtersVisible;
        if (filtersVisible) {
            showFilters();
        } else {
            hideFilters();
        }
    }
    
    private void showFilters()
    {
        filtersContainer.setVisible(true);
        filtersContainer.setManaged(true);
        toggleFiltersButton.setText("-");
    }
    
    private void hideFilters()
    {
        filtersContainer.setVisible(false);
        filtersContainer.setManaged(false);
        toggleFiltersButton.setText("+");
    }
    
    private void filterGames()
    {
        if (videoGames == null) return;

        ObservableList<VideoGame> filtered = videoGames.filtered(game -> {
            String searchText = searchBar.getText().toLowerCase().trim();
            if (!searchText.isEmpty() && !game.getV_name().toLowerCase().contains(searchText)) return false;

            boolean anyPlatformSelected = chkNintendo.isSelected() || chkPC.isSelected() || chkPlayStation.isSelected() || chkXbox.isSelected();

            if (anyPlatformSelected)
            {
                Platform gamePlatform = game.getV_platform();
                boolean matches = (chkNintendo.isSelected() && gamePlatform == Platform.NINTENDO) ||
                                (chkPC.isSelected() && gamePlatform == Platform.PC) ||
                                (chkPlayStation.isSelected() && gamePlatform == Platform.PLAYSTATION) ||
                                (chkXbox.isSelected() && gamePlatform == Platform.XBOX);

                if (!matches) return false;
            }

            boolean anyPegiSelected = chkPegi3.isSelected() || chkPegi6.isSelected() || chkPegi12.isSelected() || chkPegi16.isSelected() || 
                                     chkPegi18.isSelected();

            if (anyPegiSelected)
            {
                Pegi gamePegi = game.getV_pegi();
                boolean matches = (chkPegi3.isSelected() && gamePegi == Pegi.PEGI3) ||
                                (chkPegi6.isSelected() && gamePegi == Pegi.PEGI6) ||
                                (chkPegi12.isSelected() && gamePegi == Pegi.PEGI12) ||
                                (chkPegi16.isSelected() && gamePegi == Pegi.PEGI16) ||
                                (chkPegi18.isSelected() && gamePegi == Pegi.PEGI18);

                if (!matches) return false;
            }

            LocalDate from = fromDate.getValue();
            LocalDate to = toDate.getValue();
            LocalDate gameDate = game.getV_release();

            if (gameDate != null)
            {
                if ((from != null && gameDate.isBefore(from)) || (to != null && gameDate.isAfter(to)))
                {
                    return false;
                }
            }

            return true;
        });

        tableGames.setItems(filtered);
    }
    
    private void loadVideoGames()
    {
        try
        {
            ArrayList<VideoGame> allGames = cont.getAllVideoGames();

            ArrayList<VideoGame> myGames = cont.getGamesFromList(profile.getUsername(), "My Games");

            for (VideoGame game : allGames)
            {
                boolean isInMyGames = myGames.stream().anyMatch(g -> g.getV_id() == game.getV_id());
                game.setChecked(isInMyGames);

                if (isInMyGames)
                {
                    profile.addGame("My Games", game);
                }

                final boolean[] isUpdating = {false};

                game.checkedProperty().addListener((obs, oldVal, newVal) ->
                {
                    if (isUpdating[0]) return;

                    try
                    {
                        if (newVal)
                        {
                            cont.addGameToList(profile.getUsername(), "My Games", game.getV_id());
                            profile.addGame("My Games", game);
                        }
                        else
                        {
                            cont.removeGameFromList(profile.getUsername(), "My Games", game.getV_id());
                            profile.removeGame("My Games", game);
                        }
                    }
                    catch (OurException ex)
                    {
                        ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                        isUpdating[0] = true;
                        game.setChecked(oldVal);
                        isUpdating[0] = false;
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
        catch (OurException ex)
        {
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setMenuOptions();
        setOnActionHandlers();
        tableGames.setSelectionModel(null);
        tableGames.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        hideFilters();

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterGames());

        ChangeListener<Boolean> platformListener = (obs, oldVal, newVal) -> filterGames();
        chkNintendo.selectedProperty().addListener(platformListener);
        chkPC.selectedProperty().addListener(platformListener);
        chkPlayStation.selectedProperty().addListener(platformListener);
        chkXbox.selectedProperty().addListener(platformListener);

        ChangeListener<Boolean> pegiListener = (obs, oldVal, newVal) -> filterGames();
        chkPegi3.selectedProperty().addListener(pegiListener);
        chkPegi6.selectedProperty().addListener(pegiListener);
        chkPegi12.selectedProperty().addListener(pegiListener);
        chkPegi16.selectedProperty().addListener(pegiListener);
        chkPegi18.selectedProperty().addListener(pegiListener);

        ChangeListener<LocalDate> dateListener = (obs, oldVal, newVal) -> filterGames();
        fromDate.valueProperty().addListener(dateListener);
        toDate.valueProperty().addListener(dateListener);
    }

    @FXML
    private void handleHelpAction(ActionEvent event) {
    }
}