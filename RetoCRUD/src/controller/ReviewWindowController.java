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
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class ReviewWindowController implements Initializable {

    @FXML
    private TableView<?> tableReview;
    @FXML
    private TableColumn<?, ?> tcGame;
    @FXML
    private TableColumn<?, ?> tcRelease;
    @FXML
    private TableColumn<?, ?> tcPlatform;
    @FXML
    private TableColumn<?, ?> tcRate;
    @FXML
    private TableColumn<?, ?> tcReview;
    @FXML
    private TextField searchBar;
    @FXML
    private ComboBox<?> comboBoxLists;
    @FXML
    private Button bttnSearch;
    @FXML
    private SplitMenuButton menu;
    @FXML
    private Button bttnNewReview;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
