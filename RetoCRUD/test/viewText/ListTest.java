package viewText;

import controller.Controller;
import controller.ListWindowController;
import dao.MockClassDAO;
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
import javafx.stage.Stage;
import model.SelectableVideoGame;
import static org.junit.Assert.*;

/**
 * Test class for ListWindow view.
 * Tests user list management functionality including creating, renaming, deleting lists,
 * and adding/removing games from lists.
 *
 * @author ema
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListTest extends ApplicationTest {
    private ListWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile mockUser;

    /**
     * Initializes the JavaFX stage and loads the ListWindow view.
     *
     * @param stage The primary stage for this test
     * @throws Exception if FXML loading fails
     */
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

    /**
     * Sets up the test environment before each test method.
     * Resets the mock DAO to not throw exceptions.
     */
    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
    }

    /**
     * Verifies that all UI components are properly loaded.
     */
    @Test
    public void test101_AllComponentsAreLoaded() {
        MenuButton menuButton = lookup("#menu").query();
        Text listName = lookup("#listName").query();
        TableView<SelectableVideoGame> tableLists = lookup("#tableLists").query();
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

    /**
     * Tests menu button actions and verifies menu items are correctly loaded.
     */
    @Test
    public void test102_MenuButtonActions() { 
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

    /**
     * Verifies that games are properly loaded into the table view.
     */
    @Test
    public void test103_TableGamesLoaded() {
        TableView<SelectableVideoGame> tableLists = lookup("#tableLists").query();
        sleep(1000);

        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().size() == 3);
    }

    /**
     * Verifies that user lists are properly loaded into the combo box.
     */
    @Test
    public void test104_ComboBoxListsLoaded() { 
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

    /**
     * Tests creating new lists using the new list button.
     */
    @Test
    public void test105_NewListButton() { 
        VBox vbLists = lookup("#vbLists").query();
        int elementsQuantity = vbLists.getChildren().size();

        // Create Lists
        clickOn("+ New List");
        elementsQuantity++;
        clickOn("+ New List");
        elementsQuantity++;
        clickOn("+ New List");
        elementsQuantity++;
        sleep(1000);
        
        assertTrue(vbLists.getChildren().size() == elementsQuantity);
    }

    /**
     * Tests loading games in selected lists and verifies the list name changes correctly.
     */
    @Test
    public void test106_ListButtonsLoad() { 
        Text listName = lookup("#listName").query();
        TableView<SelectableVideoGame> tableLists = lookup("#tableLists").query();

        clickOn("Favorites");
        assertEquals("Favorites", listName.getText());
        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().size() == 1);
        sleep(1000);

        clickOn("Playstation");
        assertEquals("Playstation", listName.getText());
        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().isEmpty());
        sleep(1000);

        clickOn("Nintendo");
        assertEquals("Nintendo", listName.getText());
        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().isEmpty());
        sleep(1000);

        clickOn("My Games");
        assertEquals("My Games", listName.getText());
        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().size() == 3);
        sleep(1000);
    }

    /**
     * Tests renaming a list through the contextual menu.
     * Validates name constraints: non-empty, non-duplicate, and maximum length.
     *
     * @throws Exception if test execution fails
     */
    @Test
    public void test107_RenameListName() throws Exception { 
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
        assertEquals("New name can't have more than 10 characters.", txtMessage.getText());
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

    /**
     * Tests deleting lists through the contextual menu.
     */
    @Test
    public void test108_DeleteList() { 
        VBox vbLists = lookup("#vbLists").query();
        int elementsQuantity = vbLists.getChildren().size();

        // Create Lists
        clickOn("+ New List");
        elementsQuantity++;
        clickOn("+ New List");
        elementsQuantity++;
        clickOn("+ New List");
        elementsQuantity++;
        assertTrue(vbLists.getChildren().size() == elementsQuantity);
        sleep(500);

        // Delete Lists
        rightClickOn("New List 2");
        clickOn("Delete List");
        elementsQuantity--;
        assertTrue(vbLists.getChildren().size() == elementsQuantity);
        sleep(500);

        rightClickOn("New List 3");
        clickOn("Delete List");
        elementsQuantity--;
        assertTrue(vbLists.getChildren().size() == elementsQuantity);
        sleep(500);

        rightClickOn("New List 1");
        clickOn("Delete List");
        elementsQuantity--;
        assertTrue(vbLists.getChildren().size() == elementsQuantity);
        sleep(500);
    }

    /**
     * Tests selecting and deselecting games in the table view.
     */
    @Test
    public void test109_SelectGameInTable() {
        TableView<SelectableVideoGame> tableLists = lookup("#tableLists").query();

        if (tableLists.getItems().size() > 0) {
            clickOn(".table-cell:last .check-box");
            sleep(500);
            clickOn(".table-cell:last .check-box");
        }
    }

    /**
     * Tests removing selected games from a list.
     * Verifies that games cannot be removed without selection.
     */
    @Test
    public void test110_RemoveGames() {
        TableView<SelectableVideoGame> tableLists = lookup("#tableLists").query();

        // Need to select a game
        clickOn("Favorites");
        assertTrue(tableLists.getItems().size() == 1);
        sleep(1000);
        clickOn("My Games");
        sleep(1000);
        clickOn("#bttnRemove");
        sleep(1000);
        push(KeyCode.ESCAPE);
        
        // Removes from list
        if (tableLists.getItems().size() > 0) {
            clickOn(".table-cell:last .check-box");
        }
        clickOn("#bttnRemove");
        sleep(1000);
        push(KeyCode.ESCAPE);
        
        // Verify
        clickOn("Favorites");
        assertTrue(tableLists.getItems().isEmpty());
        sleep(1000);
    }

    /**
     * Tests adding selected games to a list.
     * Verifies that both a list and games must be selected.
     */
    @Test
    public void test111_AddGamesGames() {
        TableView<SelectableVideoGame> tableLists = lookup("#tableLists").query();
        ComboBox<String> combLists = lookup("#combLists").query();

        clickOn("Nintendo");
        assertTrue(tableLists.getItems().isEmpty());
        sleep(1000);
        clickOn("My Games");
        
        // Need to select a list
        clickOn("#bttnAdd");
        sleep(1000);
        push(KeyCode.ESCAPE);
        
        // Need to select a game
        clickOn("#combLists");
        sleep(500);
        interact(() -> {
            assertTrue(combLists.getItems().size() > 0);
            combLists.getSelectionModel().select(1);
        });
        clickOn("#bttnAdd");
        sleep(1000);push(KeyCode.ESCAPE);
        
        // Add
        if (tableLists.getItems().size() > 0) {
            clickOn(".table-cell:0 .check-box");
        }
        clickOn("#bttnAdd");
        sleep(1000);
        push(KeyCode.ESCAPE);
        
        // Changes the selection so the test can click in the button correctly
        interact(() -> {
            combLists.getSelectionModel().select(0);
        });
        
        clickOn("Nintendo");
        assertTrue(tableLists.getItems().size() == 1);
        sleep(1000);
    }
}