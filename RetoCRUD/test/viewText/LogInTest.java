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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogInTest extends ApplicationTest {
    private LogInWindowController loginController;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile mockUser;

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

    @Before
    public void setUp() {
        mockDAO.setMockProfile(null);
        mockDAO.setShouldThrowException(false, null);
    }

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

    @Test
    public void test2_TextFieldWriting() {
        clickOn("#TextField_Username");
        write("testuser");
        verifyThat("#TextField_Username", hasText("testuser"));

        push(javafx.scene.input.KeyCode.TAB);
        write("mypassword123");
        verifyThat("#PasswordField_Password", hasText("mypassword123"));
    }

    @Test
    public void test3_LoginWithEmptyFields() {
        clickOn("#Button_LogIn");
        sleep(500);
        
        Label errorLabel = lookup("#labelIncorrecto").query();
        assertTrue(errorLabel.getText().contains("Please fill in both fields"));
    }
    
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
    
    @Test
    public void test6_NavigateToSignUp() {
        clickOn("#Button_SignUp");
        sleep(1000);

        Button signUpConfirmButton = lookup("#buttonSignUp").query();
        assertNotNull(signUpConfirmButton);
        
        sleep(500);
        
        clickOn("#buttonLogIn");
    }
    
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

    private void pressEscape() {
        sleep(500);
        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }
}