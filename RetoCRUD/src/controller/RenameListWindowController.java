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
import javafx.scene.control.TextArea;
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
    private TextArea listNewName;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnConfirm;
    @FXML
    private Text txtMessage;

    private Profile profile;
    private Controller cont;
    private String listName;

    //[USERS & CONTROLLER]
    public void setUsuario(Profile profile) {
        this.profile = profile;
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public Controller getCont() {
        return cont;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    private void renameList() {
        boolean nameUpdated = false;

        do {
            try {
                if (cont.verifyListName(profile.getUsername(), listNewName.getText())) {
                    txtMessage.setText(listNewName + " List already exists.");
                } else {
                    cont.renameList(profile.getUsername(), listName, listNewName.getText());
                    nameUpdated = true;
                }
            } catch (OurException ex) {
                Logger.getLogger(ListWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (nameUpdated);
    }

    private void setOnActionHandlers() {
        txtMessage.setText(listName + " List new name:");

        btnConfirm.setOnAction(e
                -> {
            renameList();
        });

        btnCancel.setOnAction(e
                -> {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.close();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setOnActionHandlers();
        System.out.println("List: " + listName);
    }
}
