package controller;

import exception.OurException;
import exception.ShowAlert;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Listed;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.SelectableVideoGame;
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
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuActions;
    @FXML
    private MenuItem menuItemReport;
    @FXML
    private Menu menuHelp;
    @FXML
    private MenuItem menuItemHelp;

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

        contextualMenu.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                System.out.println("List: " + buttonName);
            }
        });

        MenuItem renameList = new MenuItem("Rename List");
        renameList.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                /*
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("RENAME " + buttonName + " LIST");
                alert.setHeaderText(buttonName + " list's new name:");

                TextField listNewName = new TextField();
                listNewName.setPromptText("New name");

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.add(listNewName, 1, 0);

                alert.getDialogPane().setContent(grid);
                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                Optional<ButtonType> accept = alert.showAndWait();
                if (accept.get() == ButtonType.OK) {

                    String newName = listNewName.getText();
                    try {
                        if (cont.verifyListName(profile.getUsername(), listNewName.getText())) {
                            alert.setHeaderText(" List named " + newName + " already exists.");
                        } else {
                            cont.renameList(profile.getUsername(), buttonName, newName);
                            alert.setHeaderText(listName + " updated to " + newName + ".");
                        }
                    } catch (OurException ex) {
                        Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                 */

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
                    Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
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

            for (String name : listsNames) {
                Button button = new Button(name);
                buttonStyle(button);
                button.setOnAction(e
                        -> {
                    showList(button);
                });

                vbLists.getChildren().add(button);
                litsButtons.add(button);
                if (!"My Games".equals(button.getText())) {
                    button.setContextMenu(contextualMenu(button.getText()));
                }
            }

            Button button = new Button("My Games");
            showList(button);
        } catch (OurException ex) {
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //[LISTS]
    private void showList(Button button)
    {
        selectedList = button.getText();
        listName.setText(selectedList);
        selectedButton(button);

        try
        {
            ArrayList<VideoGame> myGames = cont.getGamesFromList(profile.getUsername(), "My Games");
            ArrayList<VideoGame> selectedGames = cont.getGamesFromList(profile.getUsername(), selectedList);

            ArrayList<SelectableVideoGame> selectableGames = new ArrayList<>();

            for (VideoGame game : myGames)
            {
                boolean isInSelectedList = selectedGames.stream().anyMatch(g -> g.getV_id() == game.getV_id());

                if (game.getV_id() != 1 && isInSelectedList)
                {
                    SelectableVideoGame selectable = new SelectableVideoGame(game, false);
                    selectableGames.add(selectable);
                }
            }

            videoGames = FXCollections.observableArrayList(selectableGames);
            tableLists.setItems(videoGames);
        }
        catch (OurException ex)
        {
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
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
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setComboBox() {

        ArrayList<String> listsNames = new ArrayList();
        try {
            listsNames = cont.getUserLists(profile.getUsername());
        } catch (OurException ex) {
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
        combLists.getItems().clear();
        combLists.getItems().addAll(listsNames);
    }

    /*
    private void saveToAdd() {
        try {
            String selectedListName = combLists.getValue();

            if (selectedListName == null) {
                ShowAlert.showAlert("Error", "No list selected", Alert.AlertType.ERROR);
                return;
            }

            ArrayList<VideoGame> myGames = cont.getGamesFromList(profile.getUsername(), "My Games");
            ArrayList<VideoGame> listedGames = cont.getGamesFromList(profile.getUsername(), selectedListName);

            ArrayList<SelectableVideoGame> selectableGames = new ArrayList<>();

            for (VideoGame game : myGames) {
                boolean isInSelectedList = listedGames.stream().anyMatch(g -> g.getV_id() == game.getV_id());

                SelectableVideoGame selectable = new SelectableVideoGame(game, isInSelectedList);

                if (isInSelectedList) {
                    profile.addGame(selectedListName, game);
                }

                final boolean[] isUpdating = {false};
                final String choosedListName = selectedListName;

                selectable.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if (isUpdating[0]) {
                        return;
                    }

                    try {
                        if (newVal) {
                            cont.addGameToList(profile.getUsername(), choosedListName, game.getV_id());
                            profile.addGame(choosedListName, game);
                        } else {
                            cont.removeGameFromList(profile.getUsername(), choosedListName, game.getV_id());
                            profile.removeGame(choosedListName, game);
                        }
                    } catch (OurException ex) {
                        ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                        isUpdating[0] = true;
                        selectable.setSelected(oldVal);
                        isUpdating[0] = false;
                    }
                });

                selectableGames.add(selectable);
            }

            videoGames = FXCollections.observableArrayList(selectableGames);
            tableLists.setItems(videoGames);

        } catch (OurException ex) {
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }*/

    private void addToList() {
        if (combLists.getValue() == null) {
            ShowAlert.showAlert("Error", "[No list selected]", Alert.AlertType.ERROR);
            return;
        }

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
                ShowAlert.showAlert("Partial Success", 
                    "Some games were added to " + targetList + ", but others already existed:\n" + alreadyExistsGames.toString(), 
                    Alert.AlertType.WARNING);
            } else if (anyAdded) {
                ShowAlert.showAlert("Success", "Games added to " + targetList + " successfully.", Alert.AlertType.INFORMATION);
            } else if (anyAlreadyExists) {
                ShowAlert.showAlert("Warning", 
                    "The selected games already exist in " + targetList + ":\n" + alreadyExistsGames.toString(), 
                    Alert.AlertType.WARNING);
            } else {
                ShowAlert.showAlert("Info", "No games selected. Please select games using the checkboxes.", Alert.AlertType.INFORMATION);
            }

        } catch (OurException ex) {
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void removeFromList()
    {
        try
        {
            boolean anyRemoved = false;
            ArrayList<SelectableVideoGame> toRemoveFromUI = new ArrayList<>();

            for (SelectableVideoGame selectable : videoGames)
            {
                if (selectable.isSelected())
                {
                    VideoGame game = selectable.getVideoGame();

                    cont.removeGameFromList(profile.getUsername(), selectedList, game.getV_id());

                    if ("My Games".equals(selectedList)) {
                        try {
                            ArrayList<String> allLists = cont.getUserLists(profile.getUsername());
                            for (String listName : allLists) {
                                profile.removeGame(listName, game);
                            }
                        } catch (OurException ex) {
                            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
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

                ShowAlert.showAlert("Success", message, Alert.AlertType.INFORMATION);
            } else {
                ShowAlert.showAlert("Info", "No games selected. Please select games using the checkboxes.", Alert.AlertType.INFORMATION);
            }
        }
        catch (OurException ex)
        {
            ShowAlert.showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
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
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(LogInWindowController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (OurException ex) {
                Logger.getLogger(MainMenuWindowController.class.getName()).log(Level.SEVERE, null, ex);
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
    private void handleHelpAction(ActionEvent event) {
    }

}
