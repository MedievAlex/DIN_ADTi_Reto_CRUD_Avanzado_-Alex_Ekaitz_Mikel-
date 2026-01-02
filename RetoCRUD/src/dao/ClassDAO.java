package dao;

import java.util.ArrayList;
import model.Profile;
import model.VideoGame;

/**
 * Data Access Object interface for database operations.
 * Provides methods to interact with user and admin records in the database.
 */
public interface ClassDAO
{
    public Profile logIn(String username, String password);
    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname);
    public boolean dropOutUser(String username, String password);
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword);
    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender);
    public ArrayList<VideoGame> getVideoGames();
    
    public ArrayList<String> comboBoxInsert();
    public void initializeDefault();
}
