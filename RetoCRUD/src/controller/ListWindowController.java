/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import static java.lang.Integer.parseInt;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
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
    private MenuButton menu;
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
    private String selectedList;
    private ArrayList<Button> litsButtons;

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
        button.setStyle("-fx-background-radius: 30px; -fx-background-color: #A7C4E5;");
        button.wrapTextProperty().setValue(true);
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
            litsButtons.add(button);
        }

        Button button = new Button("All Games");
        showList(button);
    }

    private void selectedButton(Button button) {
        for (Button listButton : litsButtons) {
            if (button.getText().equals(listButton.getText())) {
                listButton.setStyle("-fx-background-radius: 30px; -fx-background-color: #D1DFF0;");
            } else {
                listButton.setStyle("-fx-background-radius: 30px; -fx-background-color: #A7C4E5;");
            }
        }
    }

    private void showList(Button button) {
        selectedList = button.getText();

        selectedButton(button);

        ArrayList<VideoGame> list = profile.getLists().get(selectedList);
        listName.setText(selectedList);

        tcGame.setCellValueFactory(new PropertyValueFactory<VideoGame, String>("v_name"));
        tcRelease.setCellValueFactory(new PropertyValueFactory<VideoGame, Date>("v_release"));
        tcPlatform.setCellValueFactory(new PropertyValueFactory<VideoGame, Platform>("v_platform"));
        tcPegi.setCellValueFactory(new PropertyValueFactory<VideoGame, Pegi>("v_pegi"));
        tcCheckBox.setCellValueFactory(new PropertyValueFactory<VideoGame, Boolean>("checked"));
        tcCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(tcCheckBox));

        /*
        tcCheckBox.setOnEditColumn(new EventHandler<TableColumn.CellEditEvent<VideoGame, Boolean>(){
            VideoGame videoGame = event.getTableView().getItems().grt(event.getTablePosition().getRow());
        >};
         */
        videoGames = FXCollections.observableArrayList();
        for (VideoGame game : list) {
            videoGames.add(game);
        }
        tableLists.setItems(videoGames);
    }

    private int getListNumber() {
        HashMap<String, ArrayList> lists = profile.getLists();
        String name, prevNumber;
        int newNumber = 1;

        for (Map.Entry<String, ArrayList> entry : lists.entrySet()) {
            name = entry.getKey();

            if ("New List".equals(name.substring(0, 8))) {
                prevNumber = name.substring(9, name.length());

                if (newNumber <= Integer.parseInt(prevNumber)) {
                    newNumber = Integer.parseInt(prevNumber) + 1;
                }
            }
        }
        return newNumber;
    }

    private void newList() {
        String buttonName = "New List " + getListNumber();

        profile.newList(buttonName, new ArrayList<VideoGame>());

        Button button = new Button(buttonName);
        buttonStyle(button);

        button.setOnAction(e
                -> {
            showList(button);
        });

        vbLists.getChildren().add(button);
        litsButtons.add(button);
        setComboBox();
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

    private void addToList() {
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
                    // Actualizar
                }
            }
        }
    }

    private void removeFromList() {
        VideoGame game;

        if (tableLists.getSelectionModel().getSelectedItem() != null) {
            game = tableLists.getSelectionModel().getSelectedItem();

            if ("All Games".equals(selectedList)) {
                HashMap<String, ArrayList> lists = profile.getLists();
                for (Map.Entry<String, ArrayList> entry : lists.entrySet()) {
                    profile.removeGame(entry.getKey(), game);
                }
            } else {
                if (!profile.removeGame(selectedList, game)) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("Error when removing " + game.getV_name() + " from the list " + selectedList + "."); // O null si no quieres encabezado
                    alert.setContentText("The game " + game.getV_name() + " already has has been deleted from the list " + selectedList + ".");
                    alert.showAndWait();
                } else {

                }
            }
            Button button = new Button(selectedList);
            showList(button);
        }
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

        miMainMenu.setOnAction((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
                Parent root = fxmlLoader.load();

                controller.MainMenuWindowController controllerWindow = fxmlLoader.getController();
                controllerWindow.setUsuario(profile);
                controllerWindow.setCont(cont);

                Stage stage = (Stage) menu.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("MAIN MENU");
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
        Button button = new Button("+ New List");
        buttonStyle(button);
        button.setStyle("-fx-background-radius: 30px; -fx-background-color: #75E773;");
        button.setOnAction(e
                -> {
            newList();
        });
        vbLists.getChildren().add(button);
        litsButtons = new ArrayList<>();

        tcCheckBox.setOnEditCommit(event -> {
            //MiItem item = event.getRowValue();
            //System.out.println("Estado cambiado para " + item.getNombre() + ": " + item.isActivo());
        });

        bttnRemove.setOnAction(e
                -> {
            removeFromList();
        });

        bttnAdd.setOnAction(e
                -> {
            addToList();
        });
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setMenuOptions();
        setOnActionHandlers();
    }

    public void test() {
        ArrayList<VideoGame> games = new ArrayList<VideoGame>();
        VideoGame game = new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3);
        game.setChecked(true);
        games.add(game);
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
}
