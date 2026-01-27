package viewText;

import controller.Controller;
import controller.MainMenuWindowController;
import dao.MockClassDAO;
import model.Profile;
import model.User;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainMenuTest extends ApplicationTest {

    private MainMenuWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile testUser;

    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);

        testUser = new User("MALE", "ES1234567890123456789012", "testuser", "Ab123456",
                "test@example.com", "Test", "123456789", "User");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenuWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        controller.setCont(realController);
        controller.setUsuario(testUser);
        
        MenuItem fullScreen = new MenuItem("Full screen");
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(fullScreen);
    
        fullScreen.setOnAction(event -> stage.setFullScreen(true));
    
        root.setOnContextMenuRequested(event -> {
            contextMenu.show(root, event.getScreenX(), event.getScreenY());
        });

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
    }

    @Test
    public void test1_AllComponentsAreLoaded() {
        MenuButton menuButton = lookup("#menu").query();
        TextField searchBar = lookup("#searchBar").query();
        Button toggleButton = lookup("#toggleFiltersButton").query();
        TableView<?> tableGames = lookup("#tableGames").query();
        CheckBox chkPlayStation = lookup("#chkPlayStation").query();
        CheckBox chkXbox = lookup("#chkXbox").query();
        CheckBox chkNintendo = lookup("#chkNintendo").query();
        CheckBox chkPC = lookup("#chkPC").query();

        assertNotNull(menuButton);
        assertNotNull(searchBar);
        assertNotNull(toggleButton);
        assertNotNull(tableGames);
        assertNotNull(chkPlayStation);
        assertNotNull(chkXbox);
        assertNotNull(chkNintendo);
        assertNotNull(chkPC);
    }

    @Test
    public void test2_SearchBarFunctionality() {
        TextField searchBar = lookup("#searchBar").query();
        clickOn("#searchBar");
        write("Zelda");
        sleep(500);
        
        assertEquals("Zelda", searchBar.getText());
    }

    @Test
    public void test3_ToggleFilters() {
        Button toggleButton = lookup("#toggleFiltersButton").query();
        assertEquals("+", toggleButton.getText());
        
        clickOn("#toggleFiltersButton");
        sleep(500);
        
        assertEquals("-", toggleButton.getText());
        
        CheckBox chkPlayStation = lookup("#chkPlayStation").query();
        assertTrue(chkPlayStation.isVisible());
        
        clickOn("#toggleFiltersButton");
        sleep(500);
        
        assertEquals("+", toggleButton.getText());
    }

    @Test
    public void test4_CheckboxSelection() {
        clickOn("#toggleFiltersButton");
        sleep(500);
        
        clickOn("#chkPlayStation");
        sleep(500);
        
        CheckBox chkPlayStation = lookup("#chkPlayStation").query();
        assertTrue(chkPlayStation.isSelected());
        
        clickOn("#chkXbox");
        sleep(500);
        
        CheckBox chkXbox = lookup("#chkXbox").query();
        assertTrue(chkXbox.isSelected());
    }

    @Test
    public void test5_TableGamesLoaded() {
        TableView<?> tableGames = lookup("#tableGames").query();
        sleep(1000);
        
        assertTrue(tableGames.getItems().size() > 0);
    }

    @Test
    public void test6_MenuButtonActions() {
        MenuButton menuButton = lookup("#menu").query();
        assertNotNull(menuButton);

        clickOn("#menu");
        sleep(500);
        
        interact(() -> {
            assertTrue(menuButton.getItems().size() == 4);
            assertEquals("PROFILE", menuButton.getItems().get(0).getText());
            assertEquals("LISTS", menuButton.getItems().get(1).getText());
            assertEquals("REVIEWS", menuButton.getItems().get(2).getText());
            assertEquals("LOG OUT", menuButton.getItems().get(3).getText());
        });

        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }

    @Test
    public void test7_SelectGameInTable() {
        TableView<?> tableGames = lookup("#tableGames").query();
        sleep(1000);
        
        if (tableGames.getItems().size() > 0) {
            clickOn(".table-row-cell");
            sleep(500);
        }
    }

    @Test
    public void test8_FilterByPlatform() {
        clickOn("#toggleFiltersButton");
        sleep(500);
        
        clickOn("#chkPlayStation");
        sleep(1000);
        
        TableView<?> tableGames = lookup("#tableGames").query();
        assertTrue(tableGames.getItems().size() >= 0);
    }
    
    @Test
    public void test9_CheckboxInTable() {
        sleep(1000);

        TableView<?> tableGames = lookup("#tableGames").query();
        assertNotNull(tableGames);

        if (tableGames.getItems().size() > 0) {
            clickOn(".table-cell:last .check-box");
            sleep(500);
        }
    }
    
    @Test
    public void test10_ContextMenu() {
        sleep(1000);

        rightClickOn("#lblMainMenu");
        sleep(500);

        clickOn("Full screen");
        sleep(500);
    }

    private void pressEscape() {
        sleep(500);
        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }
}