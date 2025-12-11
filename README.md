RENAME IT TO DIN_ADTi_Reto_CRUD_Avanzado_-Alex_Ekaitz_Mikel-

https://github.com/MedievAlex/DIN_DW_ADTi_Reto_CRUD_Avanzado_-Alex_Ekaitz_Mikel-.git

[Para añadir un Videojuego nuevo]:
- Al ser administrador, dandole con el raton click right aparecerá la opción de añadir juego. Esta añadirá una fila nueva a la tabla. Marcándola o seleccionándola para editarla directamente.


public class AdminHomeWindowController implements Initializable {

    @FXML
    private Label label;
    @FXML
    public TableView<User> adminTable;
    @FXML
    private TableColumn<User, String> user_name;
    @FXML
    private TableColumn<User, String> card_no;
    @FXML
    private TableColumn buttons;
    @FXML
    private ImageView logOut;

    private Controller cont = new Controller();

    private HashMap<Integer, User> users = cont.getAllUsers();

    private Admin admin;

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    @FXML
    private void settingsWindow(MouseEvent event) {
        Stage stage = new Stage();
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminModifyAdmin.fxml"));
            root = loader.load();

            AdminModifyAdminController controller = loader.getController();
            controller.setAdmin((Admin) this.admin);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void logOutPopup(MouseEvent event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogOutPopUp.fxml"));
            root = loader.load();
            LogOutPopUpController controller = loader.getController();
            controller.setParentStage((Stage) logOut.getScene().getWindow());
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ObservableList<User> initialData() {
        return FXCollections.observableArrayList(users.values());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        user_name.setCellValueFactory(new PropertyValueFactory<User, String>("user_name"));
        card_no.setCellValueFactory(new PropertyValueFactory<User, String>("card_no"));
        buttons.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {   
                System.out.println(col);
                // Style buttons
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                //Button onClick methods
                editButton.setOnAction((new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        try {
                            User user = getTableView().getItems().get(getIndex());

                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminModifyUser.fxml"));
                            Parent root = loader.load();

                            AdminModifyUserController controller = loader.getController();
                            controller.setUser(user);

                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.show();

                        } catch (IOException ex) {
                            throw new RuntimeException("Error creating modify user window", ex);
                        }
                    }
                }));

                deleteButton.setOnAction((new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        User user = getTableView().getItems().get(getIndex());
                        System.out.println("Delete clicked for: " + user.getUser_name());

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeleteAccountPopUp.fxml"));
                            Parent root = loader.load();

                            DeleteAccountPopUpController controller = loader.getController();
                            controller.setUser(user);
                            controller.fromAdminWindow(true);
                            controller.setParentStage((Stage) deleteButton.getScene().getWindow());

                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.show();

                        } catch (IOException ex) {
                            throw new RuntimeException("Error creating delete popup", ex);
                        }
                    }
                }));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttonsBox = new javafx.scene.layout.HBox(10, editButton, deleteButton);
                    setGraphic(buttonsBox);
                }
            }
        });

        adminTable.setItems(initialData());
    }
}
