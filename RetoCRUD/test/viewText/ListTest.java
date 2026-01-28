package viewText;

import controller.Controller;
import controller.ListWindowController;
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
import javafx.stage.Stage;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.DEFAULT)
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
    public void test1_AllComponentsAreLoaded() {
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
    public void test2_MenuButtonActions() {
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
    public void test3_TableListsLoaded() {
        TableView<?> tableLists = lookup("#tableLists").query();
        sleep(1000);

        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().size() >= 0);
    }
    
    @Test
    public void test4_NewListButton() {
        VBox vbLists = lookup("#vbLists").query();
        ObservableList listButtons = vbLists.getChildren();
        listButtons.get(1);
        // ???
    }

    @Test
    public void test5_ListButtonsLoad() {
        //Clickar a un boton y que la tabla cambie de valores
    }

    @Test
    public void test6_RenameListName() {
        // Hacer click derecho y que se pueda abrir la ventana para renombrar
    }

    @Test
    public void test7_DeleteList() {
        // Hacer click derecho y que se pueda eliminar la lista
    }
    
    @Test
    public void test8_SelectGameInTable() {
        TableView<?> tableLists = lookup("#tableLists").query();
        sleep(1000);
        
        if (tableLists.getItems().size() > 0) {
            clickOn(".table-row-cell");
            sleep(500);
        }
    }
    
    @Test
    public void test9_RemoveGames() {
        // Que al darle a remove se elimine el juego
        Button bttnRemove = lookup("#bttnRemove").query();
    }
    
    @Test
    public void test10_ComboBoxListsLoaded() {
        // Que se puedan seleccionar juegos con el combobox
        ComboBox<String> combLists = lookup("#combLists").query();
        sleep(500);

        clickOn("#combLists");
        sleep(500);

        interact(() -> {
            assertTrue(combLists.getItems().size() > 0);
            assertEquals("Favorites", combLists.getItems().get(0));
        });
        push(KeyCode.ESCAPE);
        sleep(500);
    }
    
    @Test
    public void test11_AddGamesGames() {
        // Que al darle a add se añada el juego
        Button bttnAdd = lookup("#bttnAdd").query();
    }
}
