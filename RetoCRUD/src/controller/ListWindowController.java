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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logger.GeneraLog;
import model.*;

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
    private TableView<SelectableVideoGame> tableLists;
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
    private ListWindowController self = this;

    private ObservableList<SelectableVideoGame> videoGames;
    private String selectedList;
    private ArrayList<Button> litsButtons = new ArrayList<>();

    //[USERS & CONTROLLER]
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

    //[BUTTONS]
    private void buttonStyle(Button button) {
        button.setMinWidth(vbLists.getPrefWidth());
        button.setMaxWidth(vbLists.getPrefWidth());
        button.setPrefWidth(vbLists.getPrefWidth());
        button.setStyle("-fx-background-radius: 30px; -fx-background-color: #A7C4E5;");
        button.wrapTextProperty().setValue(true);
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

    private ContextMenu contextualMenu(String buttonName) {
        ContextMenu contextualMenu = new ContextMenu();

        MenuItem renameList = new MenuItem("Rename List");
        renameList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/RenameListWindow.fxml"));
                    Parent root = fxmlLoader.load();

                    controller.RenameListWindowController controllerWindow = fxmlLoader.getController();
                    controllerWindow.setUsuario(profile);
                    controllerWindow.setCont(cont);
                    controllerWindow.setListToRename(buttonName);
                    controllerWindow.setParentCont(self);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("RENAME " + buttonName + " LIST");
                    stage.setResizable(false);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                } catch (IOException ex) {
                    GeneraLog.getLogger().severe("Failed renaming list: " + ex.getMessage());
                    showAlert("Error", "Failed renaming list", Alert.AlertType.ERROR);
                }
            }
        });

        MenuItem deleteList = new MenuItem("Delete List");

        deleteList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    cont.deleteList(profile.getUsername(), buttonName);
                    loadListButtons();
                } catch (OurException ex) {
                    GeneraLog.getLogger().severe("Failed deleting list: " + ex.getMessage());
                    showAlert("Error", "Failed deleting list", Alert.AlertType.ERROR);
                }
            }
        });

        contextualMenu.getItems().addAll(renameList, deleteList);

        return contextualMenu;
    }

    private void newListButton() {
        Button button = new Button("+ New List");
        buttonStyle(button);
        button.setStyle("-fx-background-radius: 30px; -fx-background-color: #75E773;");

        button.setOnAction(e
                -> {
            newList();
        });
        vbLists.getChildren().add(button);
    }

    public void loadListButtons() {
        vbLists.getChildren().clear();
        newListButton();

        ArrayList<String> listsNames = new ArrayList();

        try {
            listsNames = cont.getUserLists(profile.getUsername());

            for (String name : listsNames) { // The first button always My Games
                if ("My Games".equals(name)) { 
                    Button button = new Button(name);
                    buttonStyle(button);
                    button.setOnAction(e
                            -> {
                        showList(button);
                    });

                    vbLists.getChildren().add(button);
                    litsButtons.add(button);
                }
            }

            for (String name : listsNames) { // The rest buttons ordered
                if (!"My Games".equals(name)) {
                    Button button = new Button(name);
                    buttonStyle(button);
                    button.setOnAction(e
                            -> {
                        showList(button);
                    });

                    vbLists.getChildren().add(button);
                    litsButtons.add(button);
                    button.setContextMenu(contextualMenu(button.getText()));
                }
            }

            Button button = new Button("My Games");
            showList(button);
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed loading lists: " + ex.getMessage());
            showAlert("Error", "Failed loading lists", Alert.AlertType.ERROR);
        }
    }

    //[LISTS]
    public void showList(Button button) {
        selectedList = button.getText();
        listName.setText(selectedList);
        selectedButton(button);

        try {
            ArrayList<VideoGame> myGames = cont.getGamesFromList(profile.getUsername(), "My Games");
            ArrayList<VideoGame> selectedGames = cont.getGamesFromList(profile.getUsername(), selectedList);

            ArrayList<SelectableVideoGame> selectableGames = new ArrayList<>();

            for (VideoGame game : myGames) {
                boolean isInSelectedList = selectedGames.stream().anyMatch(g -> g.getV_id() == game.getV_id());

                if (!"DEFAULT_GAME".equals(game.getV_name()) && isInSelectedList) {
                    SelectableVideoGame selectable = new SelectableVideoGame(game, false);
                    selectableGames.add(selectable);
                }
            }

            videoGames = FXCollections.observableArrayList(selectableGames);
            tableLists.setItems(videoGames);
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed showing lists: " + ex.getMessage());
            showAlert("Error", "Failed showing lists", Alert.AlertType.ERROR);
        }
    }

    private int getListNumber() {
        String prevNumber;
        int newNumber = 1;

        try {
            ArrayList<String> lists = cont.getUserLists(profile.getUsername());
            for (String name : lists) {
                if (name.length() >= 8 && "New List".equals(name.substring(0, 8))) {
                    prevNumber = name.substring(9, name.length());

                    if (newNumber <= Integer.parseInt(prevNumber)) {
                        newNumber = Integer.parseInt(prevNumber) + 1;
                    }
                }
            }
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed to get list number: " + ex.getMessage());
            showAlert("Error", "Failed to get list number", Alert.AlertType.ERROR);
        }
        return newNumber;
    }

    private void newList() {

        String buttonName = "New List " + getListNumber();

        try {
            cont.newList(profile, buttonName);

            Button button = new Button(buttonName);
            buttonStyle(button);

            button.setOnAction(e
                    -> {
                showList(button);
            });

            vbLists.getChildren().add(button);
            litsButtons.add(button);
            button.setContextMenu(contextualMenu(button.getText()));

            setComboBox();
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed creating list: " + ex.getMessage());
            showAlert("Error", "Failed creating list", Alert.AlertType.ERROR);
        }
    }

    public void setComboBox() {

        ArrayList<String> listsNames = new ArrayList();
        try {
            listsNames = cont.getUserLists(profile.getUsername());
            listsNames.remove("My Games");
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed setting combobox: " + ex.getMessage());
            showAlert("Error", "Failed setting combobox", Alert.AlertType.ERROR);
        }
        combLists.getItems().clear();
        combLists.getItems().addAll(listsNames);
    }

    private void addToList() {
        if (combLists.getValue() == null) {
            showAlert("Error", "[No list selected]", Alert.AlertType.ERROR);
        } else if (combLists.getValue() != null) {
            String targetList = combLists.getValue();

            try {
                boolean anyAdded = false;
                boolean anyAlreadyExists = false;
                StringBuilder alreadyExistsGames = new StringBuilder();

                for (SelectableVideoGame selectable : videoGames) {
                    if (selectable.isSelected()) {
                        VideoGame game = selectable.getVideoGame();

                        boolean alreadyInList = cont.verifyGameInList(profile.getUsername(), targetList, game.getV_id());

                        if (!alreadyInList) {
                            cont.addGameToList(profile.getUsername(), targetList, game.getV_id());
                            profile.addGame(targetList, game);
                            anyAdded = true;
                            selectable.setSelected(false);
                        } else {
                            anyAlreadyExists = true;
                            alreadyExistsGames.append("- ").append(game.getV_name()).append("\n");
                        }
                    }
                }

                if (anyAdded && anyAlreadyExists) {
                    showAlert("Partial Success",
                            "Some games were added to " + targetList + ", but others already existed:\n" + alreadyExistsGames.toString(),
                            Alert.AlertType.WARNING);
                } else if (anyAdded) {
                    showAlert("Success", "Games added to " + targetList + " successfully.", Alert.AlertType.INFORMATION);
                } else if (anyAlreadyExists) {
                    showAlert("Warning",
                            "The selected games already exist in " + targetList + ":\n" + alreadyExistsGames.toString(),
                            Alert.AlertType.WARNING);
                } else {
                    showAlert("Info", "No games selected. Please select games using the checkboxes.", Alert.AlertType.INFORMATION);
                }
            } catch (OurException ex) {
                GeneraLog.getLogger().severe("Failed adding game to list: " + ex.getMessage());
                showAlert("Error", "Failed adding game to list", Alert.AlertType.ERROR);
            }
        }
    }

    private void removeFromList() {
        try {
            boolean anyRemoved = false;
            ArrayList<SelectableVideoGame> toRemoveFromUI = new ArrayList<>();

            for (SelectableVideoGame selectable : videoGames) {
                if (selectable.isSelected()) {
                    VideoGame game = selectable.getVideoGame();

                    cont.removeGameFromList(profile.getUsername(), selectedList, game.getV_id());

                    if ("My Games".equals(selectedList)) {
                        try {
                            ArrayList<String> allLists = cont.getUserLists(profile.getUsername());
                            for (String listName : allLists) {
                                profile.removeGame(listName, game);
                            }
                        } catch (OurException ex) {
                            GeneraLog.getLogger().severe("Failed removing game from list: " + ex.getMessage());
                            showAlert("Error", "Failed removing game from list", Alert.AlertType.ERROR);
                        }
                    } else {
                        profile.removeGame(selectedList, game);
                    }

                    anyRemoved = true;
                    toRemoveFromUI.add(selectable);
                }
            }

            if (anyRemoved) {
                videoGames.removeAll(toRemoveFromUI);

                String message = "My Games".equals(selectedList)
                        ? "Games removed from all lists successfully."
                        : "Games removed from " + selectedList + " successfully.";

                showAlert("Success", message, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Info", "No games selected. Please select games using the checkboxes.", Alert.AlertType.INFORMATION);
            }
        } catch (OurException ex) {
            GeneraLog.getLogger().severe("Failed removing game from list: " + ex.getMessage());
            showAlert("Error", "Failed removing game from list", Alert.AlertType.ERROR);
        }
    }

    //[MENU]
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
                GeneraLog.getLogger().severe("Failed profile listener: " + ex.getMessage());
                showAlert("Error", "Failed profile listener", Alert.AlertType.ERROR);
            }
        });

        miReviews.setOnAction((event) -> {
            try {
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
            } catch (IOException ex) {
                GeneraLog.getLogger().severe("Failed reviews listener: " + ex.getMessage());
                showAlert("Error", "Failed reviews listener", Alert.AlertType.ERROR);
            } catch (OurException ex) {
                GeneraLog.getLogger().severe("Failed loading reviews: " + ex.getMessage());
                showAlert("Error", "Failed loading reviews", Alert.AlertType.ERROR);
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

        miLogOut.setOnAction((event) -> {
            Stage stage = (Stage) menu.getScene().getWindow();
            stage.close();
        });
    }

    private void setOnActionHandlers() {
        newListButton();

        litsButtons = new ArrayList<>();

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
        tcCheckBox.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        tcCheckBox.setCellFactory(CheckBoxTableCell.forTableColumn(tcCheckBox));
    }

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
