package controller;

import exception.OurException;
import static exception.ShowAlert.showAlert;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import model.*;
import javafx.scene.web.WebView;
import logger.GeneraLog;

/**
 * Controller for the Main Menu window. Handles video game display, filtering,
 * and navigation to other application sections.
 * 
 * @author ema
 */
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
    private TableView<SelectableVideoGame> tableGames;
    @FXML
    private TableColumn<SelectableVideoGame, String> tcGame;
    @FXML
    private TableColumn<SelectableVideoGame, LocalDate> tcRelease;
    @FXML
    private TableColumn<SelectableVideoGame, Platform> tcPlatform;
    @FXML
    private TableColumn<SelectableVideoGame, Pegi> tcPegi;
    @FXML
    private TableColumn<SelectableVideoGame, Boolean> tcCheckBox;
    
    private Profile profile;
    private Controller cont;
    private ObservableList<SelectableVideoGame> videoGames;
    private boolean filtersVisible = false;

    /**
     * Sets the user profile and loads the video games.
     *
     * @param profile The user's profile object
     */
    public void setUsuario(Profile profile)
    {
        this.profile = profile;
        menu.setText(profile.getUsername());
        loadVideoGames();
    }

    /**
     * Sets the main controller for this controller.
     *
     * @param cont The main controller instance
     */
    public void setCont(Controller cont)
    {
        this.cont = cont;
    }

    /**
     * Gets the main controller instance.
     *
     * @return The main controller
     */
    public Controller getCont()
    {
        return cont;
    }

    /**
     * Configures the actions for menu items (Profile, Lists, Reviews, Log Out).
     */
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
                GeneraLog.getLogger().severe("Failed profile listener: " + ex.getMessage());
                showAlert("Error", "Failed profile listener", Alert.AlertType.ERROR);
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
                GeneraLog.getLogger().severe("Failed lists listener: " + ex.getMessage());
                showAlert("Error", "Failed lists listener", Alert.AlertType.ERROR);
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
                controllerWindow.setComboBox();
                controllerWindow.loadReview();
                

                Stage stage = (Stage) menu.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("REVIEWS");
            }
            catch (IOException ex)
            {
                GeneraLog.getLogger().severe("Failed reviews listener: " + ex.getMessage());
                showAlert("Error", "Failed reviews listener", Alert.AlertType.ERROR);
            } catch (OurException ex) {
                GeneraLog.getLogger().severe("Failed loading reviews: " + ex.getMessage());
                showAlert("Error", "Failed loading reviews", Alert.AlertType.ERROR);
            }
        });

        miLogOut.setOnAction((event) ->
        {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Sets up action handlers for UI components.
     */
    private void setOnActionHandlers() 
    {
        setupAccordion();
    }

    /**
     * Configures the filter accordion toggle functionality.
     */
    private void setupAccordion()
    {
        toggleFiltersButton.setOnAction(event -> toggleFilters());
        hideFilters();
    }
    
    /**
     * Toggles the visibility of the filter panel.
     */
    private void toggleFilters()
    {
        filtersVisible = !filtersVisible;
        if (filtersVisible) {
            showFilters();
        } else {
            hideFilters();
        }
    }
    
    /**
     * Shows the filter panel.
     */
    private void showFilters()
    {
        filtersContainer.setVisible(true);
        filtersContainer.setManaged(true);
        toggleFiltersButton.setText("-");
    }
    
    /**
     * Hides the filter panel.
     */
    private void hideFilters()
    {
        filtersContainer.setVisible(false);
        filtersContainer.setManaged(false);
        toggleFiltersButton.setText("+");
    }
    
    /**
     * Applies filters to the video games table based on user selections.
     * Filters by search text, platform, PEGI rating, and release date range.
     */
    private void filterGames()
    {
        if (videoGames == null) return;

        ObservableList<SelectableVideoGame> filtered = videoGames.filtered(selectable -> {
            VideoGame game = selectable.getVideoGame();

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
    
    /**
     * Loads all video games from the database and initializes the table.
     * Checks which games are already in the user's "My Games" list.
     */
    private void loadVideoGames()
    {
        try
        {
            ArrayList<VideoGame> allGames = cont.getAllVideoGames();
            ArrayList<VideoGame> myGames = cont.getGamesFromList(profile.getUsername(), "My Games");

            ArrayList<SelectableVideoGame> selectableGames = new ArrayList<>();

            for (VideoGame game : allGames)
            {
                if ("DEFAULT_GAME".equals(game.getV_name())) {
                    continue;
                }

                boolean isInMyGames = myGames.stream().anyMatch(g -> g.getV_id() == game.getV_id());

                if (isInMyGames)
                {
                    profile.addGame("My Games", game);
                }

                SelectableVideoGame selectable = new SelectableVideoGame(game, isInMyGames);

                final boolean[] isUpdating = {false};

                selectable.selectedProperty().addListener((obs, oldVal, newVal) ->
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
                        showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                        isUpdating[0] = true;
                        selectable.setSelected(oldVal);
                        isUpdating[0] = false;
                    }
                });

                selectableGames.add(selectable);
            }

            videoGames = FXCollections.observableArrayList(selectableGames);

            tcGame.setCellValueFactory(new PropertyValueFactory<>("v_name"));
            tcRelease.setCellValueFactory(new PropertyValueFactory<>("v_release"));
            tcPlatform.setCellValueFactory(new PropertyValueFactory<>("v_platform"));
            tcPegi.setCellValueFactory(new PropertyValueFactory<>("v_pegi"));

            tcCheckBox.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
            tcCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(tcCheckBox));

            tableGames.setItems(videoGames);
        }
        catch (OurException ex)
        {
            GeneraLog.getLogger().severe("Failed loading videogames: " + ex.getMessage());
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Initializes the controller class.
     * Sets up menu options, action handlers, table configuration, and filter listeners.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown
     * @param rb The resources used to localize the root object, or null if not localized
     */
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
     * Handles the print action by generating a report for the current user.
     */
    @FXML
    public void handleImprimirAction() {
        try {
            cont.generateReport(profile.getName() + " " + profile.getSurname());
        }
        catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed to generate report: " + ex.getMessage());
            showAlert("Error", "Failed to generate report", Alert.AlertType.ERROR);
        }
    }
}