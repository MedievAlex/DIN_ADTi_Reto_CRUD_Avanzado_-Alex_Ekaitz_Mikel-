/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import exception.OurException;
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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
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
    private ArrayList<VideoGame> availableGames;
    private ObservableList<Review> reviewsObservableList;
    private Stage parentStage;
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
                System.out.println("No se ha seleccionado un juego");
                resetReviewForm();
                return;
            }
            Profile currentProfile = this.profile;
            if (currentProfile == null) {
                System.out.println("No hay usuario logueado");
                resetReviewForm();
                return;
            }
            String username = currentProfile.getUsername();
            VideoGame videoGame = cont.findVideoGameByName(name);
            if (videoGame == null) {
                System.out.println("No se encontró el juego: " + name);
                resetReviewForm();
                return;
            }

            int gameId = videoGame.getV_id();

            Review reviewEncontrada = cont.findReview(username, gameId);

            if (reviewEncontrada == null) {
                System.out.println("No se encontró ninguna review para este juego y perfil");
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

            System.out.println("Review cargada correctamente para el juego: " + name);

        } catch (Exception e) {
            System.out.println("Error al cargar datos de la review desde la base de datos: " + e.getMessage());
            resetReviewForm();
        }
    }

    private void resetReviewForm() {
        spinnerRating.getValueFactory().setValue(5);
        textAreaReview.clear();
        comboBoxPlatForm.setValue(null);
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
                    Logger.getLogger(NewReviewWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
                    gameNames.add(game.getV_name());
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
            // Obtener los objetos ya gestionados por Hibernate
            Profile managedProfile = cont.findProfileByUsername(profile.getUsername());
            VideoGame managedGame = cont.findVideoGameByName(comboBoxGame.getValue());

            if (managedProfile == null || managedGame == null) {
                showAlert("Error", "Datos inválidos", "No se pudo encontrar el perfil o el juego en la base de datos.");
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
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Plataforma no válida", "La plataforma seleccionada no es válida.");
        } catch (OurException e) {
            // Mostrar el mensaje original de Hibernate
            showAlert("Error al guardar la reseña", e.getMessage(), "");
            e.printStackTrace();  // <- Esto ayuda a depurar
        }
    }

    private boolean validateFields() {
        ArrayList<String> errors = new ArrayList<String>();

        if (comboBoxGame.getValue() == null || comboBoxGame.getValue().isEmpty()) {
            errors.add("Debes seleccionar un juego");
        }

        if (comboBoxPlatForm.getValue() == null || comboBoxPlatForm.getValue().isEmpty()) {
            errors.add("Debes seleccionar una plataforma");
        }

        if (textAreaReview.getText() == null || textAreaReview.getText().trim().isEmpty()) {
            errors.add("Debes escribir una reseña");
        }

        if (errors.size() > 0) {
            showAlert("Campos incompletos", "Por favor completa todos los campos:", errors.toString());
            return false;
        }

        return true;
    }

    private void saveReview(Review review) throws OurException {
        try {
            if (cont.saveOrUpdateReview(review)) {
                System.out.println(review.toString());
                /*if (reviewsObservableList != null) {
                    reviewsObservableList.add(review);
                }*/
                showSuccessAlert("Review created/updated", "The review has been successfully created/updated.");
                parentController.loadReview();
                //ArrayList<Review> allreviews = cont.getAllReviews();
                //reviewsObservableList = FXCollections.observableArrayList(allreviews);
            } else {
                showAlert("Error", "Error creating/updating the review", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OurException("Error saving review: " + e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        populatePlatformComboBox();
        configureRatingSpinner();
    }

}
