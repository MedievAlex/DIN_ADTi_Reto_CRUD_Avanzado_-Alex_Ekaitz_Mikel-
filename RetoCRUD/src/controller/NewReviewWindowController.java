/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
