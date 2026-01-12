package controller;

import dao.ClassDAO;
import exception.OurException;
import java.util.ArrayList;
import model.Profile;
import model.Review;
import model.VideoGame;

/**
 * Controller class that handles interaction between the GUI and the database.
 * Provides login, signup, deletion, modification, and data retrieval methods.
 *
 * Author: ema
 */
public class Controller {

    private final ClassDAO DAO;

    /**
     * Constructor for Controller.
     *
     * @param dao The DAO implementation to handle database operations
     */
    public Controller(ClassDAO dao) {
        this.DAO = dao;
    }

    //[USERS]
    /**
     * Attempts to log in a user or admin.
     *
     * @param username The username
     * @param password The password
     * @return Profile object if login succeeds, null otherwise
     * @throws exception.OurException
     */
    public Profile logIn(String username, String password) throws OurException {
        return DAO.logIn(username, password);
    }

    /**
     * Signs up a new user.
     *
     * @param gender
     * @param cardNumber
     * @param username
     * @param password
     * @param email
     * @param name
     * @param telephone
     * @param surname
     * @return true if signup succeeds, false otherwise
     * @throws exception.OurException
     */
    public boolean signUp(String gender, String cardNumber, String username, String password, String email,
            String name, String telephone, String surname) throws OurException {
        return DAO.signUp(gender, cardNumber, username, password, email, name, telephone, surname);
    }

    /**
     * Deletes a user account.
     *
     * @param username
     * @param password
     * @return
     * @throws exception.OurException
     */
    public boolean dropOutUser(String username, String password) throws OurException {
        return DAO.dropOutUser(username, password);
    }

    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException {
        return DAO.dropOutAdmin(usernameToDelete, adminUsername, adminPassword);
    }

    /**
     * Modifies user information.
     *
     * @param password
     * @param email
     * @param name
     * @param telephone
     * @param surname
     * @param username
     * @param gender
     * @return
     * @throws exception.OurException
     */
    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) throws OurException {
        return DAO.modificarUser(password, email, name, telephone, surname, username, gender);
    }

    /**
     * Retrieves a list of usernames for GUI combo boxes.
     *
     * @return ArrayList
     * @throws exception.OurException
     */
    public ArrayList<String> comboBoxInsert() throws OurException {
        return DAO.comboBoxInsert();
    }

    //[VIDEOGAMES]
    public ArrayList<VideoGame> getAllVideoGames() throws OurException {
        return DAO.getAllVideoGames();
    }

    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException {
        return DAO.getGamesFromList(username, listName);
    }

    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException {
        return DAO.verifyGameInList(username, listName, gameId);
    }

    public void addGameToList(String username, String listName, int gameId) throws OurException {
        DAO.addGameToList(username, listName, gameId);
    }

    public void addGamesToList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        DAO.addGamesToList(username, listName, games);
    }

    public void removeGameFromList(String username, String listName, int gameId) throws OurException {
        DAO.removeGameFromList(username, listName, gameId);
    }

    public void removeGamesFromList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        DAO.removeGamesFromList(username, listName, games);
    }

    //[LISTS]
    public ArrayList<String> getUserLists(String username) throws OurException {
        return DAO.getUserLists(username);
    }

    public void newList(Profile profile, String listName) throws OurException {
        DAO.newList(profile, listName);
    }

    public void deleteList(String username, String listName) throws OurException {
        DAO.deleteList(username, listName);
    }

    public boolean verifyListName(String username, String listName) throws OurException {

        return DAO.verifyListName(username, listName);
    }

    public boolean renameList(String username, String listName, String listNewName) throws OurException {

        return DAO.renameList(username, listName, listNewName);
    }

    //[REVIEWS]
    public Review findReview(String username, int gameId) throws OurException {
        return DAO.findReview(username, gameId);
    }

    public VideoGame findVideoGameByName(String gameName) throws OurException {
        return DAO.findVideoGameByName(gameName);
    }

    public boolean saveOrUpdateReview(Review review) throws OurException {
        return DAO.saveOrUpdateReview(review);
    }

    public ArrayList<Review> getAllReviews() throws OurException {
        return DAO.getAllReviews();
    }

    public Profile findProfileByUsername(String username) throws OurException {
        return DAO.findProfileByUsername(username);
    }

    //[OTHER]
    public void initializeDefault() throws OurException {
        DAO.initializeDefault();
    }
}