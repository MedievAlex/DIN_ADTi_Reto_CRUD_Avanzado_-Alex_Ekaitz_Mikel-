package viewText;

import controller.Controller;
import controller.LogInWindowController;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

/**
 * Test class for LogInWindow view.
 * Tests login functionality including field validation, authentication,
 * and navigation to sign up screen.
 *
 * @author ema
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogInTest extends ApplicationTest {
    private LogInWindowController loginController;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile mockUser;

    /**
     * Initializes the JavaFX stage and loads the LogInWindow view.
     *
     * @param stage The primary stage for this test
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);

        mockUser = new User("MALE", "ES1234567890123456789012", "testuser", "Ab123456",
            "testuser@test.com", "Test", "123456789", "User");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
        Parent root = loader.load();
        loginController = loader.getController();
        
        loginController.setCont(realController);

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Sets up the test environment before each test method.
     * Resets the mock profile and disables exception throwing.
     */
    @Before
    public void setUp() {
        mockDAO.setMockProfile(null);
        mockDAO.setShouldThrowException(false, null);
    }

    /**
     * Verifies that all UI components are properly loaded.
     */
    @Test
    public void test1_AllComponentsAreLoaded() {
        TextField usernameField = lookup("#TextField_Username").query();
        PasswordField passwordField = lookup("#PasswordField_Password").query();
        Button loginButton = lookup("#Button_LogIn").query();
        Button signUpButton = lookup("#Button_SignUp").query();
        Label errorLabel = lookup("#labelIncorrecto").query();

        assertNotNull(usernameField);
        assertNotNull(passwordField);
        assertNotNull(loginButton);
        assertNotNull(signUpButton);
        assertNotNull(errorLabel);
    }

    /**
     * Tests text input functionality in username and password fields.
     */
    @Test
    public void test2_TextFieldWriting() {
        clickOn("#TextField_Username");
        write("testuser");
        verifyThat("#TextField_Username", hasText("testuser"));

        push(javafx.scene.input.KeyCode.TAB);
        write("mypassword123");
        verifyThat("#PasswordField_Password", hasText("mypassword123"));
    }

    /**
     * Tests login attempt with empty fields.
     * Should display an error message requesting field completion.
     */
    @Test
    public void test3_LoginWithEmptyFields() {
        clickOn("#Button_LogIn");
        sleep(500);
        
        Label errorLabel = lookup("#labelIncorrecto").query();
        assertTrue(errorLabel.getText().contains("Please fill in both fields"));
    }
    
    /**
     * Tests login attempt with invalid credentials.
     * Should display an error or alert dialog.
     */
    @Test
    public void test4_LoginWithInvalidCredentials() {
        mockDAO.setMockProfile(null);

        clickOn("#TextField_Username");
        write("mramirez");
        
        push(javafx.scene.input.KeyCode.TAB);
        write("wrongpass");
        clickOn("#Button_LogIn");

        pressEscape();
    }
    
    /**
     * Tests login behavior when a database exception occurs.
     * Should handle the exception gracefully and display an error.
     */
    @Test
    public void test5_LoginWithException() {
        mockDAO.setShouldThrowException(true, new OurException("Database error"));

        clickOn("#TextField_Username");
        write("mramirez");
        
        push(javafx.scene.input.KeyCode.TAB);
        write("pass456");
        clickOn("#Button_LogIn");

        pressEscape();
    }
    
    /**
     * Tests navigation from login to sign up screen.
     * Verifies that the sign up window opens and can navigate back.
     */
    @Test
    public void test6_NavigateToSignUp() {
        clickOn("#Button_SignUp");
        sleep(1000);

        Button signUpConfirmButton = lookup("#buttonSignUp").query();
        assertNotNull(signUpConfirmButton);
        
        sleep(500);
        
        clickOn("#buttonLogIn");
    }
    
    /**
     * Tests successful login with valid credentials.
     * Verifies that the main menu is displayed after login.
     */
    @Test
    public void test7_SuccessfulLogin() {
        mockDAO.setMockProfile(mockUser);
        
        clickOn("#TextField_Username");
        write("testuser");

        push(javafx.scene.input.KeyCode.TAB);
        write("Ab123456");

        clickOn("#Button_LogIn");
        sleep(500);

        assertTrue(lookup("#menuBar").query().isVisible());
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