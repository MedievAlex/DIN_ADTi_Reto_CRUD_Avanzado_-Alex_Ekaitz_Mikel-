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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Platform;
import model.Profile;
import model.Review;
import model.VideoGame;
import javafx.scene.control.ContextMenu;
import javafx.event.EventHandler;
import javafx.scene.control.TableRow;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import logger.GeneraLog;

/**
 * FXML Controller class for the Reviews window.
 * Handles displaying, filtering, creating, and deleting reviews.
 *
 * @author ema
 */
public class ReviewsWindowController implements Initializable {
    @FXML
    private MenuButton menu;
    @FXML
    private MenuItem miProfile;
    @FXML
    private MenuItem miLists;
    @FXML
    private MenuItem miMainMenu;
    @FXML
    private MenuItem miLogOut;
    @FXML
    private TextField searchBar;
    @FXML
    private ComboBox<String> combLists;
    @FXML
    private TableColumn<Review, String> tcGame;
    @FXML
    private TableColumn<Review, String> tcAutor;
    @FXML
    private TableColumn<Review, LocalDate> tcRelease;
    @FXML
    private TableColumn<Review, Platform> tcPlatform;
    @FXML
    private TableColumn<Review, Integer> tcRate;
    @FXML
    private TableColumn<Review, String> tcReview;
    @FXML
    private TableView<Review> tableReview;
    @FXML
    private Button btnNewReview;

    private Profile profile;
    private Controller cont;
    private ObservableList<Review> reviews;
    private ContextMenu currentContextMenu;

    /**
     * Sets the user profile for this controller.
     *
     * @param profile The user's profile object
     */
    public void setUsuario(Profile profile) {
        this.profile = profile;
        menu.setText(profile.getUsername());
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
     * Loads all reviews from the database and populates the table.
     *
     * @throws OurException If there's an error retrieving reviews from the database
     */
    public void loadReview() throws OurException {
        try {
            ArrayList<Review> allreviews = cont.getAllReviews();
            reviews = FXCollections.observableArrayList(allreviews);

            tcGame.setCellValueFactory(new PropertyValueFactory<>("gameName"));
            tcAutor.setCellValueFactory(new PropertyValueFactory<>("ProfileUsername"));
            tcRelease.setCellValueFactory(new PropertyValueFactory<>("reviewDate"));
            tcPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
            tcRate.setCellValueFactory(new PropertyValueFactory<>("scoreFormatted"));
            tcReview.setCellValueFactory(new PropertyValueFactory<>("description"));
            if (profile.getUsername().equals("asanchez") || profile.getUsername().equals("rluna")) {
                setupTableContextMenu();
            }
            tableReview.setItems(reviews);
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed to load reviews: " + ex.getMessage());
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Opens a new window to create or edit a review.
     */
    @FXML
    private void newReview() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/NewReviewWindow.fxml"));
            Parent root = fxmlLoader.load();

            controller.NewReviewWindowController controllerWindow = fxmlLoader.getController();
            controllerWindow.setUsuario(profile);
            controllerWindow.setCont(this.cont);
            controllerWindow.setParentController(this);

            controllerWindow.setReviewsObservableList(reviews);
            controllerWindow.setAvailableGames();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("NEW REVIEW");
            stage.setResizable(false);
            stage.show();
        } catch (IOException ex) {
            GeneraLog.getLogger().severe("Failed to create a review: " + ex.getMessage());
            showAlert("Error", "Failed to create a review", Alert.AlertType.ERROR);
        }
    }

    /**
     * Filters reviews by game name based on the search bar text.
     */
    private void searchByName() {
        if (reviews == null) {
            return;
        }
        ObservableList<Review> filtered = reviews.filtered(review -> {
            String searchText = searchBar.getText().toLowerCase().trim();

            if (!searchText.isEmpty()) {
                VideoGame game = review.getVideogame();
                if (game != null) {
                    String gameName = game.getV_name().toLowerCase();
                    if (!gameName.contains(searchText)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            return true;
        });
        tableReview.setItems(filtered);
    }

    /**
     * Populates the combo box with the user's lists.
     * Includes "All Reviews" as the first option.
     */
    public void setComboBox() {
        ArrayList<String> listsNames = new ArrayList();
        try {
            listsNames = cont.getUserLists(profile.getUsername());
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed to get user lists: " + ex.getMessage());
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
        combLists.getItems().clear();
        combLists.getItems().add("All Reviews");
        combLists.getItems().addAll(listsNames);
    }

    /**
     * Creates a context menu for a review with delete functionality.
     * Only available for admin users (asanchez or rluna).
     *
     * @param review The review for which to create the context menu
     * @return The configured ContextMenu
     */
    private ContextMenu contextualMenu(Review review) {
        if (currentContextMenu != null && currentContextMenu.isShowing()) {
            currentContextMenu.hide();
        }
        ContextMenu contextualMenu = new ContextMenu();
        currentContextMenu = contextualMenu;
        MenuItem deleteReview = new MenuItem("Delete Review");
        deleteReview.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    if (review != null) {
                        cont.deleteReview(review);
                        if (reviews != null && tableReview != null) {
                            reviews.remove(review);
                            tableReview.refresh();
                        }
                        showAlert("Success", "Review deleted successfully",
                                Alert.AlertType.INFORMATION);
                        if (contextualMenu.isShowing()) {
                            contextualMenu.hide();
                        }
                    } else {
                        showAlert("Warning", "No review selected",
                                Alert.AlertType.WARNING);
                    }
                } catch (OurException ex) {
                    GeneraLog.getLogger().severe("Failed deleting a review: " + ex.getMessage());
                    showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
        contextualMenu.getItems().addAll(deleteReview);

        contextualMenu.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if (currentContextMenu == contextualMenu) {
                    currentContextMenu = null;
                }
            }
        });
        return contextualMenu;
    }

    /**
     * Sets up context menu functionality for the table rows.
     * Enables right-click to delete reviews for admin users.
     */
    private void setupTableContextMenu() {
        tableReview.setRowFactory(new Callback<TableView<Review>, TableRow<Review>>() {
            public TableRow<Review> call(TableView<Review> tableView) {
                final TableRow<Review> row = new TableRow<Review>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent event) {
                        if (event.getButton() == MouseButton.PRIMARY && currentContextMenu != null && currentContextMenu.isShowing()) {
                            currentContextMenu.hide();
                            currentContextMenu = null;
                        }
                        if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                            if (currentContextMenu != null && currentContextMenu.isShowing()) {
                                currentContextMenu.hide();
                            }
                            Review review = row.getItem();
                            if (review != null) {
                                ContextMenu contextMenu = contextualMenu(review);
                                contextMenu.show(row, event.getScreenX(), event.getScreenY());
                            }
                        }
                    }
                });
                return row;
            }
        });

        tableReview.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            public void handle(ContextMenuEvent event) {
                if (currentContextMenu != null && currentContextMenu.isShowing()) {
                    currentContextMenu.hide();
                }
                Review review = tableReview.getSelectionModel().getSelectedItem();
                if (review != null) {
                    ContextMenu contextMenu = contextualMenu(review);
                    contextMenu.show(tableReview, event.getScreenX(), event.getScreenY());
                }
            }
        });
        tableReview.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && currentContextMenu != null && currentContextMenu.isShowing()) {
                currentContextMenu.hide();
                currentContextMenu = null;
            }
        });
    }

    /**
     * Filters reviews based on the selected list from the combo box.
     * Shows reviews for games in the selected list, or all reviews if "All Reviews" is selected.
     */
    public void showReviewsByList() {
        try {
            String selectedList = combLists.getValue();

            if (selectedList == null) {
                showAlert("Information", "Please select a list first", Alert.AlertType.INFORMATION);
                return;
            }
            if ("All Reviews".equals(selectedList)) {
                loadReview();
                return;
            }
            ArrayList<VideoGame> gamesList;
            if ("My Games".equals(selectedList)) {
                gamesList = cont.getGamesFromList(profile.getUsername(), "My Games");
            } else {
                gamesList = cont.getGamesFromList(profile.getUsername(), selectedList);
            }
            if (gamesList == null || gamesList.isEmpty()) {
                reviews.clear();
                tableReview.setItems(reviews);
                showAlert("Information", "No games found in this list", Alert.AlertType.INFORMATION);
                return;
            }
            ArrayList<Review> allReviewsByGame = new ArrayList<>();
            for (VideoGame game : gamesList) {
                if (!"DEFAULT_GAME".equals(game.getV_name())) {
                    ArrayList<Review> reviewsByGame = cont.findReviews(game.getV_id());
                    if (reviewsByGame != null && !reviewsByGame.isEmpty()) {
                        allReviewsByGame.addAll(reviewsByGame);
                    }
                }
            }
            if (allReviewsByGame.isEmpty()) {
                showAlert("Information", "No reviews found for games in this list", Alert.AlertType.INFORMATION);
            }
            reviews = FXCollections.observableArrayList(allReviewsByGame);
            tableReview.setItems(reviews);

        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed to show reviews: " + ex.getMessage());
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            showAlert("Unexpected Error",
                    "An unexpected error occurred: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Configures the actions for menu items (Profile, Lists, Main Menu, Log Out).
     */
    private void setMenuOptions() {
        miProfile.setOnAction((event)
                -> {
            try {
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
            } catch (IOException ex) {
                GeneraLog.getLogger().severe("Failed profile listener: " + ex.getMessage());
                showAlert("Error", "Failed profile listener", Alert.AlertType.ERROR);
            }
        });

        miLists.setOnAction((event)
                -> {
            try {
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
            } catch (IOException ex) {
                GeneraLog.getLogger().severe("Failed lists listener: " + ex.getMessage());
                showAlert("Error", "Failed lists listener", Alert.AlertType.ERROR);
            }
        });
        miMainMenu.setOnAction((event) -> {
            try {
                Stage stage = (Stage) menu.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.MainMenuWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setCont(cont);
                controllerWindow.setUsuario(profile);
                
                MenuItem fullScreen = new MenuItem("Full screen");
                        
                ContextMenu contextMenu = new ContextMenu();
                contextMenu.getItems().addAll(fullScreen);

                fullScreen.setOnAction(events -> stage.setFullScreen(true));

                root.setOnContextMenuRequested(events -> {
                    contextMenu.show(root, events.getScreenX(), events.getScreenY());
                });

                stage.setScene(new Scene(root));
                stage.setTitle("MAIN MENU");
            } catch (IOException ex) {
                GeneraLog.getLogger().severe("Failed main menu listener: " + ex.getMessage());
                showAlert("Error", "Failed main menu listener", Alert.AlertType.ERROR);
            }
        });

        miLogOut.setOnAction((event)
                -> {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Initializes the controller class.
     * Sets up menu options, search functionality, and list selection handlers.
     *
     * @param url The location used to resolve relative paths for the root object, or null if unknown
     * @param rb The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setMenuOptions();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> searchByName());
        combLists.setOnAction(event -> {
                showReviewsByList();
        });
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
     * Handles the print action by generating a report for the current user.
     */
    @FXML
    public void handleImprimirAction() {
        try {
            cont.generateReport(profile.getName() + " " + profile.getSurname());
        }
        catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed to generate report: " + ex.getMessage());
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
}