package dao;

import exception.OurException;
import java.util.ArrayList;
import model.Profile;
import model.VideoGame;

/**
 * Data Access Object interface for database operations. Provides methods to
 * interact with user and admin records in the database.
 */
public interface ClassDAO {

    public Profile logIn(String username, String password) throws OurException;

    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) throws OurException;

    public boolean dropOutUser(String username, String password) throws OurException;

    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException;

    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) throws OurException;

    public ArrayList<VideoGame> getVideoGames() throws OurException;

    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException;

    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException;
    
    public void addGameToList(String username, String listName, int gameId) throws OurException;

    public void removeGameFromList(String username, String listName, int gameId) throws OurException;

    public ArrayList<String> comboBoxInsert() throws OurException;

    public void initializeDefault() throws OurException;
}
