package viewText;

import controller.Controller;
import controller.DeleteAccountAdminController;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Test class for DeleteAccountAdmin view.
 * Tests admin functionality for deleting user accounts through the GUI.
 *
 * @author ema
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteAccountAdminTest extends ApplicationTest {
    private DeleteAccountAdminController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile adminUser;

    /**
     * Initializes the JavaFX stage and loads the DeleteAccountAdmin view.
     *
     * @param stage The primary stage for this test
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);
        adminUser = new User("MALE", "ES1234567890123456789012", "rluna", "zxcvbn",
                "rluna@admin.com", "Admin", "111222333", "Luna");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteAccountAdmin.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        controller.setCont(realController);
        controller.setProfile(adminUser);
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
        ComboBox<String> userComboBox = lookup("#ComboBoxUser").query();
        TextField passwordField = lookup("#TextFieldPassword").query();
        Button deleteButton = lookup("#Button_Delete").query();
        Button cancelButton = lookup("#Button_Cancel").query();
        assertNotNull(userComboBox);
        assertNotNull(passwordField);
        assertNotNull(deleteButton);
        assertNotNull(cancelButton);
    }

    /**
     * Tests delete operation with empty fields.
     * Should display an error message.
     */
    @Test
    public void test2_DeleteWithEmptyFields() {
        clickOn("#Button_Delete");
        sleep(500);
        pressEscape();
    }

    /**
     * Tests delete operation without selecting a user.
     * Should display an error message.
     */
    @Test
    public void test3_DeleteWithoutUserSelected() {
        clickOn("#TextFieldPassword");
        write("zxcvbn");
        
        clickOn("#Button_Delete");
        sleep(500);
        pressEscape();
    }

    /**
     * Tests successful user account deletion.
     * Selects a user, enters admin password, and confirms deletion.
     */
    @Test
    public void test4_SuccessfulDelete() {
        interact(() -> {
            controller.setComboBoxUser();
        });
        sleep(500);
        ComboBox<String> usersComboBox = lookup("#ComboBoxUser").query();
        sleep(500);
        interact(() -> {
            if (!usersComboBox.getItems().isEmpty()) {
                usersComboBox.getSelectionModel().select(0);
            }
        });
        sleep(500);
        clickOn("#TextFieldPassword");
        write("zxcvbn");
        clickOn("#Button_Delete");
        sleep(500);
        clickOn("Aceptar");
        sleep(1000);
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