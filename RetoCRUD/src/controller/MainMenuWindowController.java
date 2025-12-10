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
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class MainMenuWindowController implements Initializable {

    @FXML
    private SplitMenuButton menu;
    @FXML
    private MenuItem miProfile;
    @FXML
    private MenuItem miLists;
    @FXML
    private MenuItem miReviews;
    @FXML
    private MenuItem miLogOut;
    @FXML
    private TextField searchBar;
    @FXML
    private Button bttnSearch;
    @FXML
    private CheckBox chkNintendo;
    @FXML
    private CheckBox chkPC;
    @FXML
    private CheckBox chkPlayStation;
    @FXML
    private CheckBox chkXbox;
    @FXML
    private CheckBox chkPegi3;
    @FXML
    private CheckBox chkPegi7;
    @FXML
    private CheckBox chkPegi12;
    @FXML
    private CheckBox chkPegi16;
    @FXML
    private CheckBox chkPegi18;
    @FXML
    private TextField txtFromDate;
    @FXML
    private TextField txtToDate;
    @FXML
    private TableView<?> tableReview;
    @FXML
    private TableColumn<?, ?> tcGame;
    @FXML
    private TableColumn<?, ?> tcRelease;
    @FXML
    private TableColumn<?, ?> tcPlatform;
    @FXML
    private TableColumn<?, ?> tcPegi;
    @FXML
    private TableColumn<?, ?> tcCheckBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
