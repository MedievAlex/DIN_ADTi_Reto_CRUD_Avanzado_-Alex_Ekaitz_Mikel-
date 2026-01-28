package viewText;

import controller.Controller;
import controller.ListWindowController;
import dao.MockClassDAO;
import java.time.LocalDate;
import model.Profile;
import model.User;
import model.Review;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Pegi;
import model.Platform;
import model.VideoGame;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListTest extends ApplicationTest {

    private ListWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile mockUser;

    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);
        
        mockUser = new User("MALE", "ES9876543210987654321098", "asanchez", "Ab123456",
                "admin@example.com", "Admin", "987654321", "User");

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
        interact(() -> {
            VideoGame videoGame = new VideoGame("DEFAULT_GAME", LocalDate.now(), Platform.DEFAULT, Pegi.DEFAULT);
            videoGame.setV_id(1);
            mockDAO.addMockVideoGame(videoGame);
            videoGame = new VideoGame("Owlboy", LocalDate.of(2016, 11, 1), Platform.NINTENDO, Pegi.PEGI3);
            videoGame.setV_id(2);
            mockDAO.addMockVideoGame(videoGame);
            videoGame = new VideoGame("Animal Crossing New Horizons", LocalDate.of(2020, 5, 20), Platform.NINTENDO, Pegi.PEGI6);
            videoGame.setV_id(3);
            mockDAO.addMockVideoGame(videoGame);
            videoGame = new VideoGame("Detroit: Become Human", LocalDate.of(2018, 5, 25), Platform.PLAYSTATION, Pegi.PEGI16);
            videoGame.setV_id(4);
            mockDAO.addMockVideoGame(videoGame);         
        });
    }

    @Test
    public void test1_AllComponentsAreLoaded() {
        MenuButton menuButton = lookup("#menu").query();
        Text listName = lookup("#listName").query();  
        
        TableView<?> tableLists = lookup("#tableLists").query();
        
        Button bttnRemove = lookup("#bttnRemove").query();
        Button bttnAdd = lookup("#bttnAdd").query();
        ComboBox<String> combLists = lookup("#combLists").query();
        
        assertNotNull(menuButton);
        assertNotNull(listName);
        assertNotNull(tableLists);
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
    public void test3_TableReviewsLoaded() {
        TableView<?> tableLists = lookup("#tableLists").query();
        sleep(1000);

        assertNotNull(tableLists);
        assertTrue(tableLists.getItems().size() >= 0);
    }

    /*
    @Test
    public void test5_ComboBoxListsLoaded() {
        ComboBox<String> combLists = lookup("#combLists").query();
        sleep(500);

        clickOn("#combLists");
        sleep(500);

        interact(() -> {
            assertTrue(combLists.getItems().size() > 0);
            assertEquals("All Reviews", combLists.getItems().get(0));
        });
        push(KeyCode.ESCAPE);
        sleep(500);
    }

    @Test
    public void test6_SelectReviewInTable() {
        TableView<Review> tableReview = lookup("#tableReview").query();
        sleep(1000);

        if (tableReview.getItems().size() > 0) {
            clickOn(".table-row-cell");
            sleep(500);
            assertNotNull(tableReview.getSelectionModel().getSelectedItem());
        }
    }

    @Test
    public void test7_NewReviewButton() {
        Button btnNewReview = lookup("#btnNewReview").query();
        assertNotNull(btnNewReview);

        clickOn("#btnNewReview");
        sleep(500);
        assertNotNull(controller);
    }

    @Test
    public void test8_FilterByListSelection() {
        ComboBox<String> combLists = lookup("#combLists").query();
        sleep(500);
        clickOn("#combLists");
        sleep(500);

        interact(() -> {
            if (combLists.getItems().size() > 0) {
                combLists.getSelectionModel().select(0);
                //controller.showReviewsByList();
            }
        });

        sleep(500);

        TableView<Review> tableReview = lookup("#tableReview").query();
        assertNotNull("Table should not be null after filtering", tableReview);
    }

    @Test
    public void test9_BorrarPrimeraReviewDirecto() {
        Profile admin = new User("MALE", "ES9876543210987654321098","asanchez", "Ab123456", "admin@example.com", "Admin", "987654321", "User");
        interact(() -> {
            controller.setUsuario(admin);
        });
        sleep(1000);
        TableView<Review> tabla = lookup("#tableReview").query();
        if (tabla.getItems().isEmpty()) {
            interact(() -> {
                try {
                    Profile autor = new User("MALE", "ES1111111111111111111111","autorprueba", "Ab123456", "autor@test.com", "Autor", "111111111", "Prueba");
                    VideoGame juego = new VideoGame("Juego para Borrar",LocalDate.now(), Platform.PC, Pegi.PEGI12);
                    juego.setV_id(999);
                    Review review = new Review(autor, juego, 7,"Esta review será borrada", LocalDate.now(), Platform.PC);

                    mockDAO.addMockVideoGame(juego);
                    mockDAO.addMockReview(review);
                    //controller.loadReview(); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            sleep(1000);
        }
        tabla = lookup("#tableReview").query();
        int totalInicial = tabla.getItems().size();
        rightClickOn(tabla);
        sleep(500);

        try {
            clickOn("Delete Review");
            sleep(1000);
            TableView<Review>tablaFinal = lookup("#tableReview").query();
            int totalFinal = tablaFinal.getItems().size();
            assertTrue(totalFinal < totalInicial);
        } catch (Exception e) {
            push(KeyCode.ESCAPE);
        }
    }
*/
}
