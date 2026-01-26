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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteAccountAdminTest extends ApplicationTest {

    private DeleteAccountAdminController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile adminUser;

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

    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
    }

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

    @Test
    public void test2_DeleteWithEmptyFields() {
        clickOn("#Button_Delete");
        sleep(500);
        pressEscape();
    }

    @Test
    public void test3_DeleteWithoutUserSelected() {
        clickOn("#TextFieldPassword");
        write("zxcvbn");
        
        clickOn("#Button_Delete");
        sleep(500);
        pressEscape();
    }

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

    private void pressEscape() {
        sleep(500);
        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }
}