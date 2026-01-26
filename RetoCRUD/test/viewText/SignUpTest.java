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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SignUpTest extends ApplicationTest {

    private SignUpWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;

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

    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
    }

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

    @Test
    public void test3_SignUpWithEmptyFields() {
        clickOn("#buttonSignUp");
        sleep(500);
        pressEscape();
    }

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
    
    @Test
    public void test7_NavigateToLogin() {
        clickOn("#buttonLogIn");
        sleep(500);
        
        Button loginButton = lookup("#Button_LogIn").query();
        assertNotNull(loginButton);
    }

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

    private void pressEscape() {
        sleep(500);
        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }
}