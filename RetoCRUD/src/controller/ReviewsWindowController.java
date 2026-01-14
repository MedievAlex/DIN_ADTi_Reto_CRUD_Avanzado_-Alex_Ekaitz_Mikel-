/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import exception.OurException;
import exception.ShowAlert;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.Review;
import model.VideoGame;

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

    public void setUsuario(Profile profile) {
        this.profile = profile;
        menu.setText(profile.getUsername());
        if (this.cont != null) {
            //loadReview();
        }
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
            tcRelease.setCellValueFactory(new PropertyValueFactory<>("reviewDate"));
            tcPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
            tcRate.setCellValueFactory(new PropertyValueFactory<>("scoreFormatted"));
            tcReview.setCellValueFactory(new PropertyValueFactory<>("description"));

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

    public void loadLists() {
        ArrayList<VideoGame> games = new ArrayList<VideoGame>();
        games.add(new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        games.add(new VideoGame(3, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        //profile.newList("Nintendo Switch", games);

        games = new ArrayList<VideoGame>();
        games.add(new VideoGame(4, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
        games.add(new VideoGame(2, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        //profile.newList("PlayStation", games);

        profile.addGame("My Games", new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.addGame("My Games", new VideoGame(2, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        profile.addGame("My Games", new VideoGame(3, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.addGame("My Games", new VideoGame(4, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
        profile.addGame("My Games", new VideoGame(1, "Call of Duty: Black Ops II", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
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

    public void showReviewsByList(String listName) throws OurException {
        if ("All Reviews".equals(listName)) {
            loadReview();
            return;
        }

        /*ArrayList<VideoGame> gameList = profile.getLists().get(listName);
        if (gameList != null) {
            ObservableList<Review> filteredReviews = FXCollections.observableArrayList();
            for (Review review : reviews) {
                for (VideoGame game : gameList) {
                    if (review.getGame().equals(game.getV_name())) {
                        filteredReviews.add(review);
                        break;
                    }
                }
            }
            loadReviews(filteredReviews);
        }*/
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

        miLogOut.setOnAction((event)
                -> {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setMenuOptions();

        //loadReviews();

        /* combLists.setOnAction((event) -> {
            String selectedList = combLists.getValue();
            if (selectedList != null && !selectedList.isEmpty()) {
                showReviewsByList(selectedList);
            }
        });*/
    }

    @FXML
    private void handleHelpAction(ActionEvent event) {
    }
}
