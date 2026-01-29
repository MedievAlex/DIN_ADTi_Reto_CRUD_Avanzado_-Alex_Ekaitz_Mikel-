package viewText;

import controller.Controller;
import controller.ListWindowController;
import controller.RenameListWindowController;
import dao.MockClassDAO;
import javafx.collections.ObservableList;
import model.Profile;
import model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListTest extends ApplicationTest {

    private ListWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile mockUser;

    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);

        this.mockUser = new User("MALE", "ES1234567890123456789012", "testuser", "Ab123456",
                "test@test.com", "Test", "123456789", "User");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        controller.setCont(realController);
        controller.setUsuario(mockUser);

        stage.setScene(new Scene(root));
        stage.show();
        controller.loadListButtons();
        controller.setComboBox();
    }

    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
    }

    @Test
    public void test01_AllComponentsAreLoaded() {
        MenuButton menuButton = lookup("#menu").query();
        Text listName = lookup("#listName").query();
        TableView<?> tableLists = lookup("#tableLists").query();
        VBox vbLists = lookup("#vbLists").query();
        Button bttnRemove = lookup("#bttnRemove").query();
        Button bttnAdd = lookup("#bttnAdd").query();
        ComboBox<String> combLists = lookup("#combLists").query();

        assertNotNull(menuButton);
        assertNotNull(listName);
        assertNotNull(tableLists);
        assertNotNull(vbLists);
        assertNotNull(vbLists.getChildren());
        assertNotNull(bttnRemove);
        assertNotNull(bttnAdd);
        assertNotNull(combLists);
    }

    @Test
    public void test02_MenuButtonActions() { // Menu has other windows
        MenuButton menuButton = lookup("#menu").query();
        assertNotNull(menuButton);

        clickOn("#menu");
        sleep(500);

        interact(() -> {
            assertTrue(menuButton.getItems().size() == 4);
            assertEquals("PROFILE", menuButton.getItems().get(0).getText());
            assertEquals("REVIEWS", menuButton.getItems().get(1).getText());
            assertEquals("MAIN MENU", menuButton.getItems().get(2).getText());
            assertEquals("LOG OUT", menuButton.getItems().get(3).getText());
        });
        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }

    @Test
    public void test03_TableListsLoaded() {
        TableView<?> tableLists = lookup("#tableLists").query();
        sleep(1000);

        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().size() >= 0);
    }

    @Test
    public void test04_NewListButton() { // Creates new lists
        VBox vbLists = lookup("#vbLists").query();
        // Create Lists
        clickOn("+ New List");
        clickOn("+ New List");
        clickOn("+ New List");
        sleep(1000);

        assertTrue(vbLists.getChildren().size() == 8);
    }

    @Test
    public void test05_ListButtonsLoad() { // Change table values showing the games in the selected list
        Text listName = lookup("#listName").query();

        clickOn("Favorites");
        assertEquals("Favorites", listName.getText());
        sleep(1000);

        clickOn("Playstation");
        assertEquals("Playstation", listName.getText());
        sleep(1000);

        clickOn("Nintendo");
        assertEquals("Nintendo", listName.getText());
        sleep(1000);

        clickOn("My Games");
        assertEquals("My Games", listName.getText());
        sleep(1000);
    }

    @Test
    public void test06_RenameListName() throws Exception  { // Rename list with the contextual menu
        Text listName = lookup("#listName").query();
        Text txtMessage;
        TextField listNewName;
        
        String buttonName = "New List 1";
        String repeatedName = "Nintendo";
        String longName = "Potential Game of The Year winning Games";
        String newName = "GOTYs";
        
        // Create Lists
        clickOn("+ New List");
        sleep(500);

        // Rename List
        rightClickOn(buttonName);
        clickOn("Rename List");
        txtMessage = lookup("#txtMessage").query();
        listNewName = lookup("#listNewName").query();
        sleep(1000);
        
        // Name can't be empty
        clickOn("#btnConfirm");
        assertEquals("List can't have an empty name.", txtMessage.getText());
        sleep(1000);
        
        // Name can't be repeated
        clickOn("#listNewName");
        write(repeatedName);
        clickOn("#btnConfirm");
        assertEquals("List named " + repeatedName + " already exists.", txtMessage.getText());
        sleep(1000);
        listNewName.clear();
        
        // Name too long
        clickOn("#listNewName");
        write(longName);
        clickOn("#btnConfirm");
        assertEquals("New name can't have more than 20 characters.", txtMessage.getText());
        sleep(1000);
        listNewName.clear();
        
        // Renamed
        clickOn("#listNewName");
        write(newName);
        clickOn("#btnConfirm");
        assertEquals(buttonName + " updated to " + newName + ".", txtMessage.getText());
        assertEquals(newName, listName.getText());
        sleep(1000);
    }

    @Test
    public void test07_DeleteList() { // Delete list with the contextual menu
        VBox vbLists = lookup("#vbLists").query();

        // Create Lists
        clickOn("+ New List");
        clickOn("+ New List");
        clickOn("+ New List");
        sleep(500);

        // Delete Lists
        rightClickOn("New List 2");
        clickOn("Delete List");
        sleep(500);

        rightClickOn("New List 3");
        clickOn("Delete List");
        sleep(500);

        rightClickOn("New List 1");
        clickOn("Delete List");
        sleep(500);

        assertTrue(vbLists.getChildren().size() == 5);
        sleep(500);
    }

    @Test
    public void test08_SelectGameInTable() {
        TableView<?> tableLists = lookup("#tableLists").query();
        sleep(1000);

        if (tableLists.getItems().size() > 0) {
            clickOn(".table-row-cell");
            sleep(500);
        }
    }

    @Test
    public void test09_RemoveGames() {
        // Que al darle a remove se elimine el juego
        Button bttnRemove = lookup("#bttnRemove").query();
        clickOn("#bttnRemove");
        sleep(1000);
        push(KeyCode.ESCAPE);
    }

    @Test
    public void test10_ComboBoxListsLoaded() { // Lists show in the comboBox
        ComboBox<String> combLists = lookup("#combLists").query();

        clickOn("#combLists");
        sleep(500);

        interact(() -> {
            assertTrue(combLists.getItems().size() == 3);
            assertEquals("Favorites", combLists.getItems().get(0));
            assertEquals("Nintendo", combLists.getItems().get(1));
            assertEquals("Playstation", combLists.getItems().get(2));
        });
        push(KeyCode.ESCAPE);
        sleep(500);
    }

    @Test
    public void test11_AddGamesGames() {
        // Que al darle a add se añada el juego
        Button bttnAdd = lookup("#bttnAdd").query();
        ComboBox<String> combLists = lookup("#combLists").query();

        clickOn("#combLists");
        sleep(500);

        interact(() -> {
            assertTrue(combLists.getItems().size() > 0);
            combLists.getSelectionModel().select(0);
        });

        clickOn("#bttnAdd");
        sleep(1000);
        push(KeyCode.ESCAPE);;
    }

    @Test
    public void test12_RenamePrueba() {

    }
}
