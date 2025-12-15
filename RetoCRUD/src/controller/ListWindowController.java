/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
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
    private TableColumn<VideoGame, Boolean> tcCheckBox;
    @FXML
    private ComboBox<String> combLists;
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
    private String selectedList;

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

    private void buttonStyle(Button button) {
        button.setMinWidth(vbLists.getPrefWidth());
        button.setMaxWidth(vbLists.getPrefWidth());
        button.setPrefWidth(vbLists.getPrefWidth());
        button.setStyle("-fx-background-radius: 30px;");
        button.wrapTextProperty().setValue(true);
    }

    private void selectedButton(Button button) {
        button.setStyle("-fx-background-color: D1DFF0;");
    }

    private void notSelectedButton(Button button) {
        button.setStyle("-fx-background-color: A7C4E5;");
    }

    public void loadLists() {
        HashMap<String, ArrayList> lists = profile.getLists();
        for (Map.Entry<String, ArrayList> entry : lists.entrySet()) {
            String list = entry.getKey();

            Button button = new Button(list);
            buttonStyle(button);
            button.setOnAction(e
                    -> {
                String name = button.getText();
                selectedList = name;
                showList(name);
            });

            vbLists.getChildren().add(button);
        }
        
        selectedList = "All Games";
        showList(selectedList);
    }

    public void showList(String name) {
        ArrayList<VideoGame> list = profile.getLists().get(name);
        listName.setText(name);

        tcGame.setCellValueFactory(new PropertyValueFactory<VideoGame, String>("v_name"));
        tcRelease.setCellValueFactory(new PropertyValueFactory<VideoGame, Date>("v_release"));
        tcPlatform.setCellValueFactory(new PropertyValueFactory<VideoGame, Platform>("v_platform"));
        tcPegi.setCellValueFactory(new PropertyValueFactory<VideoGame, Pegi>("v_pegi"));
        tcCheckBox.setCellFactory(
                CheckBoxTableCell.forTableColumn(tcCheckBox)
        );
        //tcCheckBox.setCellFactory();

        videoGames = FXCollections.observableArrayList();
        for (VideoGame game : list) {
            videoGames.add(game);
        }
        tableLists.setItems(videoGames);
    }

    public void newList() {
        number++;
        String buttonName = "New List " + number;

        profile.newList(buttonName, new ArrayList<VideoGame>());

        Button button = new Button(buttonName);
        buttonStyle(button);

        button.setOnAction(e
                    -> {
                String name = button.getText();
                selectedList = name;
                showList(name);
            });

        vbLists.getChildren().add(button);
    }

    public void setComboBox() {
        HashMap<String, ArrayList> hmLists = profile.getLists();
        ArrayList<String> listsNames = new ArrayList<String>();
        
        for (Map.Entry<String, ArrayList> entry : hmLists.entrySet()) {
            if (!"All Games".equals(entry.getKey())) {
                listsNames.add(entry.getKey());
            }
        }
        
        combLists.getItems().clear();
        combLists.getItems().addAll(listsNames);
    }

    private void saveToAdd() {
        // Save in a list to add them when a checkbox is true
    }

    public void addToList() {
        String name;
        VideoGame game;

        // Saves them on a list and if it already is shows an alert
        if (combLists.getValue() == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("[No list selected]"); // O null si no quieres encabezado
            alert.setContentText("Select a list to add the games.");
            alert.showAndWait();
        } else {
            name = combLists.getValue();

            if (tableLists.getSelectionModel().getSelectedItem() != null) {
                game = tableLists.getSelectionModel().getSelectedItem();
                if (!profile.addGame(name, game)) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("WARNING");
                    alert.setHeaderText("Error when adding games to the list " + name + "."); // O null si no quieres encabezado
                    alert.setContentText("The game " + game.getV_name() + " it has not been added to the list " + name + " because it is already there.");
                    alert.showAndWait();
                } else {
                    showList(selectedList);
                    // Actualizar
                }
            }
        }
    }

    public void removeFromList() {
        String name = selectedList;
        VideoGame game;

        if (tableLists.getSelectionModel().getSelectedItem() != null) {
            game = tableLists.getSelectionModel().getSelectedItem();
            if (!profile.removeGame(name, game)) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("ERROR");
                alert.setHeaderText("Error when removing " + game.getV_name() + " from the list " + name + "."); // O null si no quieres encabezado
                alert.setContentText("The game " + game.getV_name() + " already has has been deleted from the list " + name + ".");
                alert.showAndWait();
            } else {
                // Actualizar
            }
        }
    }

    public void test() {
        ArrayList<VideoGame> games = new ArrayList<VideoGame>();
        games.add(new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        games.add(new VideoGame(3, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.newList("Nintendo Switch", games);

        games = new ArrayList<VideoGame>();
        games.add(new VideoGame(4, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
        games.add(new VideoGame(2, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        profile.newList("PlayStation", games);

        profile.addGame("All Games", new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.addGame("All Games", new VideoGame(2, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
        profile.addGame("All Games", new VideoGame(3, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
        profile.addGame("All Games", new VideoGame(4, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
        profile.addGame("All Games", new VideoGame(1, "Call of Duty: Black Ops II", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        
        
        Button button = new Button("+ New List");
        buttonStyle(button);
        button.setOnAction(e
                -> {
            newList();
        });
        
        vbLists.getChildren().add(button);
        
        bttnRemove.setOnAction(e
                -> {
            removeFromList();
        });

        bttnAdd.setOnAction(e
                -> {
            addToList();
        });  
    }
}