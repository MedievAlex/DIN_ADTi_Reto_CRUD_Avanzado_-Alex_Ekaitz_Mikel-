package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Profile;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class NewReviewWindowController implements Initializable {

    @FXML
    private TextArea textAreaReview;
    @FXML
    private ComboBox<?> comboBoxPlatForm;
    @FXML
    private ComboBox<?> comboBoxGame;
    @FXML
    private Spinner<?> spinnerRating;
    @FXML
    private Button bttnConfirm;
    @FXML
    private Button bttnCancel;

    private Profile profile;
    private Controller cont;

    public void setUsuario(Profile profile) {
        this.profile = profile;
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    public void cancel(ActionEvent event) {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
