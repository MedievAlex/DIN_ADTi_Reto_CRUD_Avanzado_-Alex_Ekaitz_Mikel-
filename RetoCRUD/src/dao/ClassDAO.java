package dao;

import exception.OurException;
import java.util.ArrayList;
import model.Profile;
import model.Review;
import model.VideoGame;

/**
 * Data Access Object interface for database operations. Provides methods to
 * interact with user and admin records in the database.
 */
public interface ClassDAO {

    //[USERS]
    public Profile logIn(String username, String password) throws OurException;

    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) throws OurException;

    public boolean dropOutUser(String username, String password) throws OurException;

    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException;

    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) throws OurException;

    public ArrayList<String> comboBoxInsert() throws OurException;
    
    public Profile findProfileByUsername(String username) throws OurException;

    //[VIDEOGAMES]
    public ArrayList<VideoGame> getAllVideoGames() throws OurException;

    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException;

    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException;

    public void addGameToList(String username, String listName, int gameId) throws OurException;

    public void addGamesToList(String username, String listName, ArrayList<VideoGame> games) throws OurException;

    public void removeGameFromList(String username, String listName, int gameId) throws OurException;

    public void removeGamesFromList(String username, String listName, ArrayList<VideoGame> games) throws OurException;
    
    public VideoGame findVideoGameByName(String gameName) throws OurException;

    //[LISTS]
    public ArrayList<String> getUserLists(String username) throws OurException;

    public void newList(Profile profile, String listName) throws OurException;

    public void deleteList(String username, String listName) throws OurException;

    public boolean verifyListName(String username, String listName) throws OurException;

    public void renameList(String username, String listName, String listNewName) throws OurException;

    //[REVIEWS]
    public Review findReview(String username, int gameId) throws OurException;

    public ArrayList<Review> getAllReviews() throws OurException;

    public boolean saveOrUpdateReview(Review review) throws OurException;

    //[OTHER]
    public void initializeDefault() throws OurException;
}
