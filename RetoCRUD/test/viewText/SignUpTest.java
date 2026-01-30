package viewText;

import controller.Controller;
import controller.SignUpWindowController;
import dao.MockClassDAO;
import exception.OurException;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Profile;
import model.User;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Test class for SignUpWindow view.
 * Tests user registration functionality including field validation, email format,
 * password matching, and successful account creation.
 *
 * @author ema
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignUpTest extends ApplicationTest {
    private SignUpWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;

    /**
     * Initializes the JavaFX stage and loads the SignUpWindow view.
     *
     * @param stage The primary stage for this test
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUpWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        controller.setCont(realController);

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
     */
    @Test
    public void test1_AllComponentsAreLoaded() {
        TextField emailField = lookup("#textFieldEmail").query();
        TextField usernameField = lookup("#textFieldUsername").query();
        TextField nameField = lookup("#textFieldName").query();
        TextField surnameField = lookup("#textFieldSurname").query();
        TextField telephoneField = lookup("#textFieldTelephone").query();
        TextField cardField = lookup("#textFieldCardN").query();
        PasswordField passwordField = lookup("#textFieldPassword").query();
        PasswordField confirmField = lookup("#textFieldCPassword").query();
        RadioButton maleButton = lookup("#rButtonM").query();
        RadioButton femaleButton = lookup("#rButtonW").query();
        RadioButton otherButton = lookup("#rButtonO").query();
        Button signUpButton = lookup("#buttonSignUp").query();
        Button loginButton = lookup("#buttonLogIn").query();

        assertNotNull(emailField);
        assertNotNull(usernameField);
        assertNotNull(nameField);
        assertNotNull(surnameField);
        assertNotNull(telephoneField);
        assertNotNull(cardField);
        assertNotNull(passwordField);
        assertNotNull(confirmField);
        assertNotNull(maleButton);
        assertNotNull(femaleButton);
        assertNotNull(otherButton);
        assertNotNull(signUpButton);
        assertNotNull(loginButton);
    }

    /**
     * Tests text input functionality in all registration fields.
     * Verifies that all fields accept input and gender selection works.
     */
    @Test
    public void test2_TextFieldWriting() {
        clickOn("#textFieldEmail");
        write("test@example.com");
        
        clickOn("#textFieldUsername");
        write("testuser");
        
        clickOn("#textFieldName");
        write("Test");
        
        clickOn("#textFieldSurname");
        write("User");
        
        clickOn("#textFieldTelephone");
        write("123456789");
        
        clickOn("#textFieldCardN");
        write("ZA9081726354891027364512");
        
        clickOn("#textFieldPassword");
        write("pass123");
        
        clickOn("#textFieldCPassword");
        write("pass123");
        
        clickOn("#rButtonM");
        
        RadioButton maleButton = lookup("#rButtonM").query();
        assertTrue(maleButton.isSelected());
    }

    /**
     * Tests sign up attempt with empty fields.
     * Should display an error message requesting field completion.
     */
    @Test
    public void test3_SignUpWithEmptyFields() {
        clickOn("#buttonSignUp");
        sleep(500);
        pressEscape();
    }

    /**
     * Tests sign up attempt with invalid email format.
     * Should display an error message about email validation.
     */
    @Test
    public void test4_SignUpWithInvalidEmail() {
        clickOn("#textFieldEmail");
        write("invalid-email");
        
        clickOn("#textFieldUsername");
        write("testuser");
        
        clickOn("#textFieldName");
        write("Test");
        
        clickOn("#textFieldSurname");
        write("User");
        
        clickOn("#textFieldTelephone");
        write("123456789");
        
        clickOn("#textFieldCardN");
        write("ZA9081726354891027364512");
        
        clickOn("#textFieldPassword");
        write("pass123");
        
        clickOn("#textFieldCPassword");
        write("pass123");
        
        clickOn("#rButtonM");
        
        clickOn("#buttonSignUp");
        sleep(500);
        pressEscape();
    }

    /**
     * Tests sign up attempt with mismatched passwords.
     * Should display an error message about password mismatch.
     */
    @Test
    public void test5_SignUpWithPasswordMismatch() {
        clickOn("#textFieldEmail");
        write("test@example.com");
        
        clickOn("#textFieldUsername");
        write("testuser");
        
        clickOn("#textFieldName");
        write("Test");
        
        clickOn("#textFieldSurname");
        write("User");
        
        clickOn("#textFieldTelephone");
        write("123456789");
        
        clickOn("#textFieldCardN");
        write("ZA9081726354891027364512");
        
        clickOn("#textFieldPassword");
        write("pass123");
        
        clickOn("#textFieldCPassword");
        write("differentpass");
        
        clickOn("#rButtonM");
        
        clickOn("#buttonSignUp");
        sleep(500);
        pressEscape();
    }

    /**
     * Tests sign up behavior when a database exception occurs.
     * Should handle the exception gracefully and display an error.
     */
    @Test
    public void test6_SignUpWithException() {
        mockDAO.setShouldThrowException(true, new OurException("Database error"));

        clickOn("#textFieldEmail");
        write("test@example.com");
        
        clickOn("#textFieldUsername");
        write("testuser");
        
        clickOn("#textFieldName");
        write("Test");
        
        clickOn("#textFieldSurname");
        write("User");
        
        clickOn("#textFieldTelephone");
        write("123456789");
        
        clickOn("#textFieldCardN");
        write("ZA9081726354891027364512");
        
        clickOn("#textFieldPassword");
        write("pass123");
        
        clickOn("#textFieldCPassword");
        write("pass123");
        
        clickOn("#rButtonM");
        
        clickOn("#buttonSignUp");
        sleep(500);
        pressEscape();
    }
    
    /**
     * Tests navigation from sign up to login screen.
     * Verifies that the login window opens correctly.
     */
    @Test
    public void test7_NavigateToLogin() {
        clickOn("#buttonLogIn");
        sleep(500);
        
        Button loginButton = lookup("#Button_LogIn").query();
        assertNotNull(loginButton);
    }

    /**
     * Tests successful user registration with valid data.
     * Verifies that the main menu is displayed after successful sign up.
     */
    @Test
    public void test8_SuccessfulSignUp() {
        Profile newUser = new User("MALE", "ZA9081726354891027364512", "testuser", "pass123",
                "test@example.com", "Test", "123456789", "User");
        mockDAO.setMockProfile(newUser);

        clickOn("#textFieldEmail");
        write("test@example.com");

        clickOn("#textFieldUsername");
        write("testuser");

        clickOn("#textFieldName");
        write("Test");

        clickOn("#textFieldSurname");
        write("User");

        clickOn("#textFieldTelephone");
        write("123456789");

        clickOn("#textFieldCardN");
        write("ZA9081726354891027364512");

        clickOn("#textFieldPassword");
        write("pass123");

        clickOn("#textFieldCPassword");
        write("pass123");

        clickOn("#rButtonM");

        clickOn("#buttonSignUp");
        sleep(1000);

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