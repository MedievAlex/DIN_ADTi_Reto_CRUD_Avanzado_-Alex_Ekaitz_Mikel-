package controller;

import exception.OurException;
import exception.ShowAlert;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author 2dami
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
    private Button btnSearch;
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
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

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
            Logger.getLogger(SignUpWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    public void setComboBox() {
        ArrayList<String> listsNames = new ArrayList();
        try {
            listsNames = cont.getUserLists(profile.getUsername());
        } catch (OurException ex) {
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        combLists.getItems().clear();
        combLists.getItems().add("All Reviews");
        combLists.getItems().addAll(listsNames);
    }

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
                        ShowAlert.showAlert("Success", "Review deleted successfully",
                                Alert.AlertType.INFORMATION);
                        if (contextualMenu.isShowing()) {
                            contextualMenu.hide();
                        }
                    } else {
                        ShowAlert.showAlert("Error", "No review selected",
                                Alert.AlertType.ERROR);
                    }
                } catch (OurException ex) {
                    Logger.getLogger(ReviewsWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    ShowAlert.showAlert("Error", "Error deleting review: " + ex.getMessage(),
                            Alert.AlertType.ERROR);
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

    public void showReviewsByList() {
        try {
            String selectedList = combLists.getValue();

            if (selectedList == null) {
                ShowAlert.showAlert("Information", "Please select a list first", Alert.AlertType.INFORMATION);
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
                ShowAlert.showAlert("Information", "No games found in this list", Alert.AlertType.INFORMATION);
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
                ShowAlert.showAlert("Information", "No reviews found for games in this list", Alert.AlertType.INFORMATION);
            }
            reviews = FXCollections.observableArrayList(allReviewsByGame);
            tableReview.setItems(reviews);

        } catch (OurException ex) {
            String errorMessage = ex.getMessage();
            if (errorMessage.contains("connection") || errorMessage.contains("database")) {
                ShowAlert.showAlert("Database Error","Cannot connect to database.",Alert.AlertType.ERROR);
            } else {
                ShowAlert.showAlert("Error", errorMessage, Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            ShowAlert.showAlert("Unexpected Error",
                    "An unexpected error occurred: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

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
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        miMainMenu.setOnAction((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.MainMenuWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setCont(cont);
                controllerWindow.setUsuario(profile);

                Stage stage = (Stage) menu.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("MAIN MENU");
            } catch (IOException ex) {
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setMenuOptions();
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> searchByName());
        combLists.setOnAction(event -> {
                showReviewsByList();
        });
    }

    @FXML
    private void handleHelpAction(ActionEvent event) {
    }
}
