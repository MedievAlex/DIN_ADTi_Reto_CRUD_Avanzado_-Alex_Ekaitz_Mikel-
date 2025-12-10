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
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class ListWindowController implements Initializable {

    @FXML
    private SplitMenuButton menu;
    @FXML
    private MenuItem miProfile;
    @FXML
    private MenuItem miReviews;
    @FXML
    private MenuItem miMainMenu;
    @FXML
    private MenuItem miLogOut;
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
    @FXML
    private Button bttnNewList;
    @FXML
    private Button bttnAllGames;
    @FXML
    private Button bttnRemove;
    @FXML
    private Button bttnAdd;
    @FXML
    private ComboBox<?> combLists;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
