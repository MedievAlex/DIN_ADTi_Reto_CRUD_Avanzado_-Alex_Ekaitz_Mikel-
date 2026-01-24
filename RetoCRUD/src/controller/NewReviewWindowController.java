package controller;

import exception.OurException;
import static exception.ShowAlert.showAlert;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logger.GeneraLog;
import model.Platform;
import model.Profile;
import model.Review;
import model.VideoGame;

/**
 * FXML Controller class
 *
 * @author ema
 */
public class NewReviewWindowController implements Initializable {

    @FXML
    private TextArea textAreaReview;
    @FXML
    private ComboBox<String> comboBoxPlatForm;
    @FXML
    private ComboBox<String> comboBoxGame;
    @FXML
    private Spinner<Integer> spinnerRating;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;

    private Profile profile;
    private Controller cont;
    private ObservableList<Review> reviewsObservableList;
    private ReviewsWindowController parentController;

    public void setUsuario(Profile profile) {
        this.profile = profile;
    }

    public void setParentController(ReviewsWindowController parentController) {
        this.parentController = parentController;
    }

    public void setCont(Controller cont) {

        this.cont = cont;
        btnConfirm.setOnAction(this::confirmReview);
        comboBoxGame.setDisable(true);
        comboBoxPlatForm.setOnAction(e
                -> {
            comboBoxGame.setDisable(false);
            comboBoxGame.getItems().clear();
        });
    }

    public Controller getCont() {
        return cont;
    }

    public void setAvailableGames() {

        comboBoxGame.setDisable(false);
    }

    public void setReviewsObservableList(ObservableList<Review> reviewsObservableList) {
        this.reviewsObservableList = reviewsObservableList;
    }

    @FXML
    private void cargardatosReview() {
        try {
            String name = comboBoxGame.getValue();

            if (name == null || name.isEmpty()) {
                resetReviewForm();
                return;
            }
            Profile currentProfile = this.profile;
            if (currentProfile == null) {
                resetReviewForm();
                return;
            }
            String username = currentProfile.getUsername();
            VideoGame videoGame = cont.findVideoGameByName(name);
            if (videoGame == null) {
                resetReviewForm();
                return;
            }

            int gameId = videoGame.getV_id();

            Review reviewEncontrada = cont.findReview(username, gameId);

            if (reviewEncontrada == null) {
                resetReviewForm();
                return;
            }
            spinnerRating.getValueFactory().setValue(reviewEncontrada.getScore());
            textAreaReview.setText(reviewEncontrada.getDescription());

            if (reviewEncontrada.getPlatform() != null) {
                comboBoxPlatForm.setValue(reviewEncontrada.getPlatform().name());
            } else {
                comboBoxPlatForm.setValue(null);
            }
        } catch (Exception ex) {
            GeneraLog.getLogger().severe("Failed loading review's data from the database: " + ex.getMessage());
            resetReviewForm();
        }
    }

    private void resetReviewForm() {
        spinnerRating.getValueFactory().setValue(5);
        textAreaReview.clear();
    }

    private void populatePlatformComboBox() {
        ObservableList<String> platformNames = FXCollections.observableArrayList();
        for (Platform platform : Platform.values()) {
            if (!platform.equals(Platform.DEFAULT)) {
                platformNames.add(platform.name());
            }
        }
        comboBoxPlatForm.setItems(platformNames);

        comboBoxPlatForm.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                comboBoxGame.setDisable(false);
                try {
                    populateGameComboBox(newValue);
                } catch (OurException ex) {
                    GeneraLog.getLogger().severe("Failed getting videogames: " + ex.getMessage());
                }
            } else {
                comboBoxGame.setDisable(true);
                comboBoxGame.getItems().clear();
                comboBoxGame.setValue(null);
            }
        });
    }

    private void populateGameComboBox(String selectedPlatform) throws OurException {
        comboBoxGame.getItems().clear();

        ArrayList<VideoGame> games = cont.getAllVideoGames();
        Platform platform = Platform.valueOf(selectedPlatform);
        ObservableList<String> gameNames = FXCollections.observableArrayList();
        if (platform.equals("-- Choose Platform --")) {
            comboBoxGame.setDisable(true);
        } else {
            for (VideoGame game : games) {
                if (game.getV_platform().equals(platform)) {
                    if (game.getV_name().equals("")) {
                    } else {
                        gameNames.add(game.getV_name());
                    }
                }
            }
            if (!gameNames.isEmpty()) {
                comboBoxGame.setItems(gameNames);
                comboBoxGame.setPromptText("-- Choose Game --");
            } else {
                comboBoxGame.setPromptText("-- No Games --");
            }
        }

    }

    private void configureRatingSpinner() {
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 5);
        spinnerRating.setValueFactory(valueFactory);
        spinnerRating.setEditable(true);
        spinnerRating.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinnerRating.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }

            if (!newValue.isEmpty()) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value > 10) {
                        spinnerRating.getEditor().setText("10");
                    }
                } catch (NumberFormatException e) {
                    spinnerRating.getEditor().setText("5");
                }
            }
        });
    }

    @FXML
    private void confirmReview(ActionEvent event) {
        if (!validateFields()) {
            return;
        }
        try {
            Profile managedProfile = cont.findProfileByUsername(profile.getUsername());
            VideoGame managedGame = cont.findVideoGameByName(comboBoxGame.getValue());

            if (managedProfile == null || managedGame == null) {
                showAlert("Invalid data", "The game or the profile can't be found in the database.", Alert.AlertType.ERROR);
                return;
            }

            String selectedPlatform = comboBoxPlatForm.getValue();
            int rating = spinnerRating.getValue();
            String reviewText = textAreaReview.getText();
            Platform platform = Platform.valueOf(selectedPlatform);

            Review newReview = new Review(managedProfile, managedGame, rating, reviewText, LocalDate.now(), platform);
            saveReview(newReview);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (IllegalArgumentException ex) {
            showAlert("Invalid platform", "The selected platform is not valid.", Alert.AlertType.ERROR);
            
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed saving review: " + ex.getMessage());
            showAlert("Error", "Failed saving review", Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        ArrayList<String> errors = new ArrayList<String>();

        if (comboBoxGame.getValue() == null || comboBoxGame.getValue().isEmpty()) {
            errors.add("You must choose a game");
        }

        if (comboBoxPlatForm.getValue() == null || comboBoxPlatForm.getValue().isEmpty()) {
            errors.add("You must choose a platform");
        }

        if (textAreaReview.getText() == null || textAreaReview.getText().trim().isEmpty()) {
            errors.add("You must add a description");
        }

        if (errors.size() > 0) {
            showAlert("Empty fields", "Please, fill all the fields:", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void saveReview(Review review) {
        try {
            if (cont.saveOrUpdateReview(review)) {
                showAlert("Review created/updated", "The review has been successfully created/updated.", Alert.AlertType.INFORMATION);
                parentController.loadReview();
            } else {
                showAlert("Error", "Error creating/updating the review", Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            GeneraLog.getLogger().severe("Failed to save a review: " + ex.getMessage());
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populatePlatformComboBox();
        configureRatingSpinner();
        comboBoxGame.setOnAction((event) -> {
            cargardatosReview();
        });
        spinnerRating.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                try {
                    int value = Integer.parseInt(spinnerRating.getEditor().getText());
                    spinnerRating.getValueFactory().setValue(value);
                } catch (NumberFormatException e) {
                    spinnerRating.getEditor().setText(spinnerRating.getValue().toString());
                }
            }
        });
    }
}
