package controller;

import dao.ClassDAO;
import java.util.ArrayList;
import model.Profile;
import model.VideoGame;

/**
 * Controller class that handles interaction between the GUI and the database.
 * Provides login, signup, deletion, modification, and data retrieval methods.
 * 
 * Author: acer
 */
public class Controller
{
    private final ClassDAO DAO;

    /**
     * Constructor for Controller.
     *
     * @param dao The DAO implementation to handle database operations
     */
    public Controller(ClassDAO dao)
    {
        this.DAO = dao;
    }

    /**
     * Attempts to log in a user or admin.
     *
     * @param username The username
     * @param password The password
     * @return Profile object if login succeeds, null otherwise
     */
    public Profile logIn(String username, String password)
    {
        return DAO.logIn(username, password);
    }

    /**
     * Signs up a new user.
     *
     * @return true if signup succeeds, false otherwise
     */
    public boolean signUp(String gender, String cardNumber, String username, String password, String email,
            String name, String telephone, String surname)
    {
        return DAO.signUp(gender, cardNumber, username, password, email, name, telephone, surname);
    }

    /**
     * Deletes a user account.
     */
    public boolean dropOutUser(String username, String password)
    {
        return DAO.dropOutUser(username, password);
    }

    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword)
    {
        return DAO.dropOutAdmin(usernameToDelete, adminUsername, adminPassword);
    }

    /**
     * Modifies user information.
     */
    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender)
    {
        return DAO.modificarUser(password, email, name, telephone, surname, username, gender);
    }

    /**
     * Retrieves a list of usernames for GUI combo boxes.
     * @return ArrayList
     */
    public ArrayList<String> comboBoxInsert()
    {
        return DAO.comboBoxInsert();
    }
    
    public ArrayList<VideoGame> getVideoGames()
    {
        return DAO.getVideoGames();
    }
    
    public void addGameToDB(Profile profile, VideoGame game)
    {
    }
    
    public void removeGameFromDB(Profile profile, VideoGame game)
    {
    }
    
    public void initializeDefault()
    {
        DAO.initializeDefault();
    }
}
