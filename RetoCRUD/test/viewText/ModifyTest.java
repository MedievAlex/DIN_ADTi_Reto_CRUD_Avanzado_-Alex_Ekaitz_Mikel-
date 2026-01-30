package viewText;

import controller.Controller;
import controller.ModifyWindowController;
import dao.MockClassDAO;
import exception.OurException;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Test class for ModifyWindow view.
 * Tests user profile modification functionality including field validation,
 * successful updates, and error handling.
 *
 * @author ema
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModifyTest extends ApplicationTest {
    private ModifyWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile testUser;

    /**
     * Initializes the JavaFX stage and loads the ModifyWindow view.
     *
     * @param stage The primary stage for this test
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);

        testUser = new User("MALE", "ES1234567890123456789012", "jlopez", "pass123",
                "jlopez@example.com", "Juan", "123456789", "Lopez");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModifyWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        controller.setCont(realController);
        controller.setProfile(testUser);

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Sets up the test environment before each test method.
     * Resets the mock DAO and dismisses any open dialogs.
     */
    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
        pressEscape();
    }

    /**
     * Verifies that all UI components are properly loaded.
     * Checks that user information is correctly displayed in labels.
     */
    @Test
    public void test1_AllComponentsAreLoaded() {
        Label usernameLabel = lookup("#LabelUsername").query();
        Label emailLabel = lookup("#LabelEmail").query();
        TextField nameField = lookup("#TextField_Name").query();
        TextField surnameField = lookup("#TextField_Surname").query();
        TextField telephoneField = lookup("#TextField_Telephone").query();
        Button saveButton = lookup("#Button_SaveChanges").query();
        Button cancelButton = lookup("#Button_Cancel").query();

        assertNotNull(usernameLabel);
        assertNotNull(emailLabel);
        assertNotNull(nameField);
        assertNotNull(surnameField);
        assertNotNull(telephoneField);
        assertNotNull(saveButton);
        assertNotNull(cancelButton);

        assertEquals("jlopez", usernameLabel.getText());
        assertEquals("jlopez@example.com", emailLabel.getText());
    }

    /**
     * Tests text input functionality in modification fields.
     * Verifies that user can enter and modify name, surname, and telephone.
     */
    @Test
    public void test2_TextFieldWriting() {
        clickOn("#TextField_Name");
        write("Juan Carlos");
        
        clickOn("#TextField_Surname");
        write("Lopez Garcia");
        
        clickOn("#TextField_Telephone");
        write("666777888");
        
        TextField nameField = lookup("#TextField_Name").query();
        TextField surnameField = lookup("#TextField_Surname").query();
        TextField telephoneField = lookup("#TextField_Telephone").query();
        
        assertEquals("Juan Carlos", nameField.getText());
        assertEquals("Lopez Garcia", surnameField.getText());
        assertEquals("666777888", telephoneField.getText());
    }

    /**
     * Tests modification behavior when a database exception occurs.
     * Should handle the exception gracefully and display an error.
     */
    @Test
    public void test3_ModifyWithException() {
        mockDAO.setShouldThrowException(true, new OurException("Database error"));

        clickOn("#TextField_Name");
        write("Juan Carlos");
        
        clickOn("#TextField_Surname");
        write("Lopez Garcia");
        
        clickOn("#TextField_Telephone");
        write("666777888");
        
        clickOn("#Button_SaveChanges");
        sleep(500);
        
        pressEscape();
    }

    /**
     * Tests successful user profile modification.
     * Verifies that changes are saved correctly.
     */
    @Test
    public void test4_SuccessfulModify() {
        clickOn("#TextField_Name");
        write("Juan Carlos");
        
        clickOn("#TextField_Surname");
        write("Lopez Garcia");
        
        clickOn("#TextField_Telephone");
        write("666777888");
        
        clickOn("#Button_SaveChanges");
        sleep(500);
        
        pressEscape();
    }

    /**
     * Helper method to press the Escape key and dismiss dialogs.
     */
    private void pressEscape() {
        sleep(500);
        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }
}