package viewText;

import controller.Controller;
import controller.MenuWindowController;
import dao.MockClassDAO;
import model.Profile;
import model.User;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import static org.junit.Assert.*;

/**
 * Test class for MenuWindow view.
 * Tests user menu functionality including navigation to delete and modify user screens,
 * and logout functionality.
 *
 * @author ema
 */
public class MenuTest extends ApplicationTest {
    private MenuWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile testUser;

    /**
     * Initializes the JavaFX stage and loads the MenuWindow view.
     *
     * @param stage The primary stage for this test
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);
        testUser = new User("MALE", "ES1234567890123456789012", "rluna", "zxcvbn",
                "rluna@example.com", "Rosa", "111222333", "Luna");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        controller.setCont(realController);
        controller.setUsuario(testUser);
        stage.setScene(new Scene(root));
        stage.show();
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
     * Checks that username and button labels display correct values.
     */
    @Test
    public void test1_AllComponentsAreLoaded() {
        Label usernameLabel = lookup("#label_Username").query();
        Button deleteButton = lookup("#Button_Delete").query();
        Button modifyButton = lookup("#Button_Modify").query();
        Button logoutButton = lookup("#Button_LogOut").query();
        assertNotNull(usernameLabel);
        assertNotNull(deleteButton);
        assertNotNull(modifyButton);
        assertNotNull(logoutButton);
        assertEquals("rluna", usernameLabel.getText());
        assertEquals("Delete User", deleteButton.getText());
        assertEquals("Modify User", modifyButton.getText());
        assertEquals("Back", logoutButton.getText());
    }

    /**
     * Tests navigation to the delete user screen and canceling the operation.
     * Verifies that the cancel button returns to the menu.
     */
    @Test
    public void test2_NavigateToDeleteAndCancel() {
        clickOn("#Button_Delete");
        sleep(500);
        
        Button cancelButton = lookup("#Button_Cancel").query();
        assertNotNull(cancelButton);
        
        clickOn("#Button_Cancel");
        sleep(500);
        
        Button deleteButton = lookup("#Button_Delete").query();
        assertNotNull(deleteButton);
    }

    /**
     * Tests navigation to the modify user screen and canceling the operation.
     * Verifies that the cancel button returns to the menu.
     */
    @Test
    public void test3_NavigateToModifyAndCancel() {
        clickOn("#Button_Modify");
        sleep(500);
        
        Button cancelButton = lookup("#Button_Cancel").query();
        assertNotNull(cancelButton);
        
        clickOn("#Button_Cancel");
        sleep(500);
        
        Button modifyButton = lookup("#Button_Modify").query();
        assertNotNull(modifyButton);
    }

    /**
     * Tests logout functionality.
     */
    @Test
    public void test4_Logout() {
        clickOn("#Button_LogOut");
        sleep(500);
    }

    /**
     * Tests complete navigation flow through all menu options.
     * Navigates to delete, cancels, navigates to modify, cancels, and logs out.
     */
    @Test
    public void test5_CompleteFlow() {
        clickOn("#Button_Delete");
        sleep(500);
        
        clickOn("#Button_Cancel");
        sleep(500);
        
        clickOn("#Button_Modify");
        sleep(500);
        
        clickOn("#Button_Cancel");
        sleep(500);
        
        clickOn("#Button_LogOut");
        sleep(500);
    }
}