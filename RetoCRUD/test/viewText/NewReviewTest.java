package viewText;

import controller.Controller;
import controller.NewReviewWindowController;
import controller.ReviewsWindowController;
import dao.MockClassDAO;
import java.time.LocalDate;
import model.*;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import static org.junit.Assert.*;

/**
 * Test class for NewReviewWindow view.
 * Tests review creation and modification functionality including platform selection,
 * game filtering, rating input, and review text editing.
 *
 * @author ema
 */
public class NewReviewTest extends ApplicationTest {
    private NewReviewWindowController controller;
    private MockClassDAO mockDAO;
    private Profile testUser;

    /**
     * Initializes the JavaFX stage and loads the NewReviewWindow view.
     *
     * @param stage The primary stage for this test
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        mockDAO = new MockClassDAO();
        Controller realController = new Controller(mockDAO);
        testUser = new User("MALE", "ES1234567890123456789012", "testuser", "Ab123456", "test@example.com", "Test", "123456789", "User");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NewReviewWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setUsuario(testUser);
        controller.setCont(realController);
        ReviewsWindowController parent = new ReviewsWindowController();
        controller.setParentController(parent);

        stage.setScene(new Scene(root));
        stage.show();
        sleep(1000);
    }

    /**
     * Sets up the test environment before each test method.
     * Adds mock video games for testing.
     */
    @Before
    public void setUp() {
        interact(() -> {
            VideoGame psGame = new VideoGame("The Last of Us", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18);
            psGame.setV_id(4);
            VideoGame xboxGame = new VideoGame("Halo", LocalDate.now(), Platform.XBOX, Pegi.PEGI16);
            xboxGame.setV_id(2);
            mockDAO.addMockVideoGame(psGame);
            mockDAO.addMockVideoGame(xboxGame);
        });
    }

    /**
     * Verifies that all UI components exist and are properly loaded.
     */
    @Test
    public void test1_ComponentesExisten() {
        assertNotNull("Área de texto", lookup("#textAreaReview").query());
        assertNotNull("Lista de plataformas", lookup("#comboBoxPlatForm").query());
        assertNotNull("Lista de juegos", lookup("#comboBoxGame").query());
        assertNotNull("Selector de puntuación", lookup("#spinnerRating").query());
        assertNotNull("Botón Confirmar", lookup("#btnConfirm").query());
        assertNotNull("Botón Cancelar", lookup("#btnCancel").query());
    }

    /**
     * Verifies that all platforms are properly loaded into the platform combo box.
     */
    @Test
    public void test2_ListaPlataformasCargada() {
        ComboBox<String> plataformas = lookup("#comboBoxPlatForm").query();

        interact(() -> {
            assertTrue(plataformas.getItems().size() > 0);
            assertTrue(plataformas.getItems().contains("PLAYSTATION"));
            assertTrue(plataformas.getItems().contains("XBOX"));
            assertTrue(plataformas.getItems().contains("PC"));
            assertTrue(plataformas.getItems().contains("NINTENDO"));
        });
    }

    /**
     * Tests that selecting a platform enables the games combo box.
     * Verifies that games are filtered by the selected platform.
     */
    @Test
    public void test3_SeleccionarPlataformaHabilitaJuegos() {
        ComboBox<String> juegos = lookup("#comboBoxGame").query();

        assertTrue(juegos.isDisabled());
        clickOn("#comboBoxPlatForm");
        sleep(500);
        clickOn("PLAYSTATION");
        sleep(1000);

        assertFalse(juegos.isDisabled());
        interact(() -> {
            assertTrue(juegos.getItems().size() > 0);
            assertTrue(juegos.getItems().contains("The Last of Us"));
        });
    }

    /**
     * Tests all rating spinner functionality including value limits and manual input.
     * Verifies that rating values are constrained between 0 and 10.
     */
    @Test
    public void test4_TodoSobreLaPuntuacion() {
        Spinner<Integer> puntuacion = lookup("#spinnerRating").query();
        interact(() -> {
            assertEquals(Integer.valueOf(5), puntuacion.getValue());
        });
        interact(() -> {
            puntuacion.getValueFactory().setValue(10);
        });
        sleep(300);
        assertEquals(Integer.valueOf(10), puntuacion.getValue());

        interact(() -> {
            puntuacion.getValueFactory().setValue(0);
        });
        sleep(300);
        assertEquals(Integer.valueOf(0), puntuacion.getValue());

        interact(() -> {
            puntuacion.getValueFactory().setValue(100);
        });
        sleep(300);
        assertEquals(Integer.valueOf(10), puntuacion.getValue());

        interact(() -> {
            puntuacion.getValueFactory().setValue(5);
        });
        sleep(300);
        assertEquals(Integer.valueOf(5), puntuacion.getValue());
        clickOn(puntuacion);
        eraseText(1).write("8");
        push(KeyCode.ENTER);
        sleep(500);

        assertEquals(Integer.valueOf(8), puntuacion.getValue());
        assertTrue(puntuacion.isEditable());
    }

    /**
     * Tests writing review text in the text area.
     */
    @Test
    public void test5_EscribirReview() {
        TextArea areaTexto = lookup("#textAreaReview").query();
        clickOn("#textAreaReview");
        write("Este juego es increíble!");
        sleep(500);
        assertEquals("Este juego es increíble!", areaTexto.getText());
    }

    /**
     * Tests creating a complete review with all fields filled.
     * Verifies that platform, game, rating, and text are correctly set.
     */
    @Test
    public void test6_CrearReviewCompleta() {
        clickOn("#comboBoxPlatForm");
        sleep(500);
        clickOn("PLAYSTATION");
        sleep(1500);
        clickOn("#comboBoxGame");
        sleep(500);
        clickOn("The Last of Us");
        sleep(500);
        clickOn("#textAreaReview");
        write("Juego espectacular con buena historia");
        sleep(500);
        Spinner<Integer> puntuacion = lookup("#spinnerRating").query();
        interact(() -> {
            puntuacion.getValueFactory().setValue(9);
        });
        sleep(500);
        ComboBox<String> comboPlataforma = lookup("#comboBoxPlatForm").query();
        ComboBox<String> comboJuego = lookup("#comboBoxGame").query();
        TextArea areaTexto = lookup("#textAreaReview").query();
        interact(() -> {
            assertEquals("PLAYSTATION", comboPlataforma.getValue());
            assertEquals("The Last of Us", comboJuego.getValue());
            assertEquals("Juego espectacular con buena historia", areaTexto.getText());
            assertEquals(Integer.valueOf(9), puntuacion.getValue());
        });
    }

    /**
     * Tests cancel button functionality.
     */
    @Test
    public void test7_BotonCancelar() {
        Button cancelar = lookup("#btnCancel").query();
        assertNotNull(cancelar);
        clickOn("#btnCancel");
        sleep(500);
    }

    /**
     * Tests filtering games by platform selection.
     * Verifies that different platforms show their respective games.
     */
    @Test
    public void test8_FiltrarJuegosPorPlataforma() {
        clickOn("#comboBoxPlatForm");
        sleep(500);
        clickOn("PLAYSTATION");
        sleep(1000);
        ComboBox<String> juegos = lookup("#comboBoxGame").query();
        interact(() -> {
            assertTrue(juegos.getItems().contains("The Last of Us"));
        });
        clickOn("#comboBoxPlatForm");
        sleep(500);
        clickOn("XBOX");
        sleep(1000);

        interact(() -> {
            assertTrue(juegos.getItems().contains("Halo"));
        });
    }

    /**
     * Tests loading and updating an existing review.
     * Verifies that existing review data is loaded and can be modified.
     */
    @Test
    public void test9_CargarYActualizarReview() {
        Profile user = new User("MALE", "ES1234567890123456789012", "testuser", "Ab123456","test@example.com", "Test", "123456789", "User");
        VideoGame juego = new VideoGame("Juego para Actualizar",LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI16);
        juego.setV_id(101);
        Review reviewOriginal = new Review(user, juego, 7,"Review original - no me gustó mucho",LocalDate.now(), Platform.PLAYSTATION);
        
        interact(() -> {
            mockDAO.addMockVideoGame(juego);
            mockDAO.addMockReview(reviewOriginal);
        });
        sleep(500);
        ComboBox<String> plataforma = lookup("#comboBoxPlatForm").query();
        ComboBox<String> comboJuego = lookup("#comboBoxGame").query();
        interact(() -> {
            plataforma.setValue("PLAYSTATION");
        });
        sleep(1000);
        interact(() -> {
            comboJuego.setValue("Juego para Actualizar");
        });
        sleep(1000);
        TextArea texto = lookup("#textAreaReview").query();
        Spinner<Integer> puntuacion = lookup("#spinnerRating").query();
        assertEquals(Integer.valueOf(7), puntuacion.getValue());
        assertEquals("Review original - no me gustó mucho", texto.getText());
        interact(() -> {
            puntuacion.getValueFactory().setValue(9);
            texto.setText("Review ACTUALIZADA!");
        });
        assertEquals(Integer.valueOf(9), puntuacion.getValue());
        assertEquals("Review ACTUALIZADA!", texto.getText());
    }
}