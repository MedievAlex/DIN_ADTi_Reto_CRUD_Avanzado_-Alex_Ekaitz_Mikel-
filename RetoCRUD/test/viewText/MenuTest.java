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

public class MenuTest extends ApplicationTest {

    private MenuWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile testUser;

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

    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
    }

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

    @Test
    public void test4_Logout() {
        clickOn("#Button_LogOut");
        sleep(500);
    }

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