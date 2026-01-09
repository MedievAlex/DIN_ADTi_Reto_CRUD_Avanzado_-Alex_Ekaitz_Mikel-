package controller;

import exception.OurException;
import exception.ShowAlert;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;
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
import model.Listed;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.VideoGame;

/**
 * FXML Controller class
 *
 * @author ema
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
    private ArrayList<Button> litsButtons = new ArrayList<>();

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

    public void loadListButtons() {
        ArrayList<String> listsNames = new ArrayList();

        try {
            listsNames = cont.getUserLists(profile.getUsername());
        } catch (OurException ex) {
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (String name : listsNames) {

            Button button = new Button(name);
            buttonStyle(button);
            button.setOnAction(e
                    -> {
                showList(button);
            });

            vbLists.getChildren().add(button);
            litsButtons.add(button);
        }

        Button button = new Button("My Games");
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

        try {
            ArrayList<VideoGame> myGames = cont.getGamesFromList(profile.getUsername(), "My Games");
            ArrayList<VideoGame> selectedGames = cont.getGamesFromList(profile.getUsername(), selectedList);
            videoGames = FXCollections.observableArrayList();

            for (VideoGame game : myGames) { // Revisa los Juegos en My Games
                boolean isInSelectedList = selectedGames.stream().anyMatch(g -> g.getV_id() == game.getV_id()); // Si existe en la lista seleccionada

                if (isInSelectedList) {
                    if (game.getV_id() != 1) { // AÑADIRLO A LAS OTRAS TABLAS
                        profile.addGame(selectedList, game);
                        videoGames.add(game);
                    }
                }

                final boolean[] isUpdating = {false};

                game.checkedProperty().addListener((obs, oldVal, newVal)
                        -> {
                    if (isUpdating[0]) {
                        return;
                    }

                    try {
                        if (newVal) {
                            cont.addGameToList(profile.getUsername(), selectedList, game.getV_id());
                            profile.addGame(selectedList, game);
                        } else {
                            cont.removeGameFromList(profile.getUsername(), selectedList, game.getV_id());
                            profile.removeGame(selectedList, game);
                        }
                    } catch (OurException ex) {
                        ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                        isUpdating[0] = true;
                        game.setChecked(oldVal);
                        isUpdating[0] = false;
                    }
                });
            }
            //videoGames = FXCollections.observableArrayList();

            tableLists.setItems(videoGames);
        } catch (OurException ex) {
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private int getListNumber() {
        HashMap<String, ArrayList<VideoGame>> lists = profile.getListsView();
        String name, prevNumber;
        int newNumber = 1;

        for (HashMap.Entry<String, ArrayList<VideoGame>> entry : lists.entrySet()) {
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

    private void newList(String newList) {
        String buttonName = newList;

        profile.newList(buttonName);

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

        ArrayList<String> listsNames = new ArrayList();
        try {
            listsNames = cont.getUserLists(profile.getUsername());
        } catch (OurException ex) {
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        listsNames.add("Create a New List");
        combLists.getItems().clear();
        combLists.getItems().addAll(listsNames);
    }

    private void saveToAdd() {
        try {
            String selectedListName = null;
            ArrayList<VideoGame> listedGames = cont.getGamesFromList(profile.getUsername(), selectedListName);

            for (VideoGame gameInList : listedGames) {
                boolean isInMyGames = listedGames.stream().anyMatch(g -> g.getV_id() == gameInList.getV_id());
                gameInList.setChecked(isInMyGames);

                if (isInMyGames) {
                    profile.addGame(selectedListName, gameInList);
                }

                final boolean[] isUpdating = {false};
                final String choosedListName = selectedListName;

                gameInList.checkedProperty().addListener((obs, oldVal, newVal)
                        -> {
                    if (isUpdating[0]) {
                        return;
                    }

                    try {
                        if (newVal) {
                            cont.addGameToList(profile.getUsername(), choosedListName, gameInList.getV_id());
                            profile.addGame(choosedListName, gameInList);
                        } else {
                            cont.removeGameFromList(profile.getUsername(), choosedListName, gameInList.getV_id());
                            profile.removeGame(choosedListName, gameInList);
                        }
                    } catch (OurException ex) {
                        ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                        isUpdating[0] = true;
                        gameInList.setChecked(oldVal);
                        isUpdating[0] = false;
                    }
                });
            }
        } catch (OurException ex) {
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addToList() {
        /*if (combLists.getValue() == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("[No list selected]");
            alert.setContentText("Select a list to add the games.");
            alert.showAndWait();
        } else {
            final String selectedListName = combLists.getValue();

            if ("Create a New List".equals(selectedListName)) {
                
                cont.newList(profile, videogame, selectedListName);
                
            } else {
                try {
                    ArrayList<VideoGame> actualListGames = cont.getGamesFromList(profile.getUsername(), selectedList);
                    ArrayList<VideoGame> nextListGames = cont.getGamesFromList(profile.getUsername(), selectedListName);

                    for (VideoGame game : actualListGames) {
                        boolean isInMyGames = nextListGames.stream().anyMatch(g -> g.getV_id() == game.getV_id());
                        game.setChecked(isInMyGames);

                        if (isInMyGames) {
                            profile.addGame(selectedListName, game);
                        }

                        final boolean[] isUpdating = {false};

                        game.checkedProperty().addListener((obs, oldVal, newVal)
                                -> {
                            if (isUpdating[0]) {
                                return;
                            }

                            try {
                                if (newVal) {
                                    cont.addGameToList(profile.getUsername(), selectedListName, game.getV_id());
                                    profile.addGame(selectedListName, game);
                                } else {
                                    cont.removeGameFromList(profile.getUsername(), selectedListName, game.getV_id());
                                    profile.removeGame(selectedListName, game);
                                }
                            } catch (OurException ex) {
                                ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                                isUpdating[0] = true;
                                isUpdating[0] = false;
                            }
                        });
                    }
                    for (VideoGame uncheckGame : actualListGames) {
                        uncheckGame.setChecked(false);
                    }
                } catch (OurException ex) {
                    ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }*/
    }

    private void removeFromList() {
        boolean anyRemoved = false;

        for (VideoGame game : new ArrayList<>(videoGames)) {
            if (game.isChecked()) {
                if ("My Games".equals(selectedList)) {
                    Set<Listed> lists = profile.getListedGames();
                    for (Listed list : lists) {
                        profile.removeGame(list.getListName(), game);
                    }
                } else {
                    profile.removeGame(selectedList, game);
                }
                anyRemoved = true;
                game.setChecked(false);
            }
        }

        if (anyRemoved) {
            showList(new Button(selectedList));
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

        miLogOut.setOnAction((event) -> {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    private void setOnActionHandlers() {
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
        tableLists.setSelectionModel(null);
        tableLists.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tcGame.setCellValueFactory(new PropertyValueFactory<>("v_name"));
        tcRelease.setCellValueFactory(new PropertyValueFactory<>("v_release"));
        tcPlatform.setCellValueFactory(new PropertyValueFactory<>("v_platform"));
        tcPegi.setCellValueFactory(new PropertyValueFactory<>("v_pegi"));
        tcCheckBox.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
        tcCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(tcCheckBox));

        /*
        tcCheckBox.setOnEditColumn(new EventHandler<TableColumn.CellEditEvent<VideoGame, Boolean>(){
            VideoGame videoGame = event.getTableView().getItems().grt(event.getTablePosition().getRow());
        >};
         */
    }

    /*
    public void test() {
        VideoGame owlboy = new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3);
        VideoGame animalCrossing = new VideoGame(2, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3);

        profile.newList("Nintendo Switch");
        profile.addGame("Nintendo Switch", owlboy);
        profile.addGame("Nintendo Switch", animalCrossing);

        VideoGame detroit = new VideoGame(3, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18);
        VideoGame astrobot = new VideoGame(4, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3);
        VideoGame bo2 = new VideoGame(5, "Call of Duty: Black Ops II", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3);

        profile.newList("PlayStation");
        profile.addGame("PlayStation", detroit);
        profile.addGame("PlayStation", astrobot);

        profile.addGame("My Games", owlboy);
        profile.addGame("My Games", astrobot);
        profile.addGame("My Games", animalCrossing);
        profile.addGame("My Games", detroit);
        profile.addGame("My Games", bo2);
    }
     */
}
