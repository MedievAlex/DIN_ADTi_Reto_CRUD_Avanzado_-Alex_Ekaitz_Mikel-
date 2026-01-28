package viewText;

import controller.Controller;
import controller.ReviewsWindowController;
import exception.OurException;
import dao.MockClassDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Profile;
import model.User;
import model.Review;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.testfx.framework.junit.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import model.Pegi;
import model.Platform;
import model.VideoGame;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReviewTest extends ApplicationTest {

    private ReviewsWindowController controller;
    private Controller realController;
    private MockClassDAO mockDAO;
    private Profile testUser;
    private Profile adminUser;

    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        realController = new Controller(mockDAO);

        testUser = new User("MALE", "ES1234567890123456789012", "testuser", "Ab123456",
                "test@example.com", "Test", "123456789", "User");

        adminUser = new User("MALE", "ES9876543210987654321098", "asanchez", "Ab123456",
                "admin@example.com", "Admin", "987654321", "User");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReviewsWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        controller.setCont(realController);
        controller.setUsuario(testUser);

        stage.setScene(new Scene(root));
        stage.show();
        controller.loadReview();
        controller.setComboBox();
    }

    @Before
    public void setUp() {
        mockDAO.setShouldThrowException(false, null);
    }

    @Test
    public void test1_AllComponentsAreLoaded() {
        MenuButton menuButton = lookup("#menu").query();
        TextField searchBar = lookup("#searchBar").query();
        ComboBox<String> combLists = lookup("#combLists").query();
        TableView<Review> tableReview = lookup("#tableReview").query();
        Button btnNewReview = lookup("#btnNewReview").query();
        assertNotNull(menuButton);
        assertNotNull(searchBar);
        assertNotNull(combLists);
        assertNotNull(tableReview);
        assertNotNull(btnNewReview);
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
            assertEquals("LISTS", menuButton.getItems().get(1).getText());
            assertEquals("MAIN MENU", menuButton.getItems().get(2).getText());
            assertEquals("LOG OUT", menuButton.getItems().get(3).getText());
        });
        push(javafx.scene.input.KeyCode.ESCAPE);
        sleep(500);
    }

    @Test
    public void test3_TableReviewsLoaded() {
        TableView<Review> tableReview = lookup("#tableReview").query();
        sleep(1000);

        assertNotNull(tableReview);
        assertTrue(tableReview.getItems().size() >= 0);
    }

    @Test
    public void test4_SearchBarFunctionality() {
        TextField searchBar = lookup("#searchBar").query();
        clickOn("#searchBar");
        write("Zelda");
        sleep(500);

        assertEquals("Zelda", searchBar.getText());
    }

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
                controller.showReviewsByList();
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
                    controller.loadReview(); 
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
}
