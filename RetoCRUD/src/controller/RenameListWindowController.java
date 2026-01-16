/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import exception.OurException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Profile;

/**
 * FXML Controller class
 *
 * @author 2dami
 */
public class RenameListWindowController implements Initializable {

    @FXML
    private TextField listNewName;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnConfirm;
    @FXML
    private Text txtMessage;

    private Profile profile;
    private Controller cont;
    private ListWindowController parentController;
    private String listName;

    //[USERS & CONTROLLER]
    public void setUsuario(Profile profile) {
        this.profile = profile;
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }
    
    public void setParentCont(ListWindowController parentController) {
        this.parentController = parentController;
    }

    public Controller getCont() {
        return cont;
    }

    public void setListToRename(String listName) {
        this.listName = listName;
        txtMessage.setText(listName + " list new name:");
    }

    private void renameList() {
        String newName = listNewName.getText();

        try {
            if (cont.verifyListName(profile.getUsername(), listNewName.getText())) {
                txtMessage.setText(" List named " + newName + " already exists.");
            } else {
                cont.renameList(profile.getUsername(), listName, newName);
                txtMessage.setText(listName + " updated to " + newName + ".");
                Button newNameButton = new Button(newName);
                
                parentController.loadListButtons();
                parentController.showList(newNameButton);
                parentController.setComboBox();
                close();
            }
        } catch (OurException ex) {
            Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void close(){
        Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
    }
    
    private void setOnActionHandlers() {
        btnConfirm.setOnAction(e
                -> {
            renameList();
        });

        btnCancel.setOnAction(e
                -> {
            close();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setOnActionHandlers();
    }
}
