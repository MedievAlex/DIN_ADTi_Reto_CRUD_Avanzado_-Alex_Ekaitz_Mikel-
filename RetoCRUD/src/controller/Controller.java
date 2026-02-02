package controller;

import dao.ClassDAO;
import exception.OurException;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import model.Profile;
import model.Review;
import model.VideoGame;

/**
 * Controller class that handles interaction between the GUI and the database.
 * Provides login, signup, deletion, modification, and data retrieval methods.
 *
 * @author ema
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

    /**
     * Deletes a user account as an admin.
     *
     * @param usernameToDelete The username of the user to delete
     * @param adminUsername The admin's username
     * @param adminPassword The admin's password
     * @return true if deletion succeeds, false otherwise
     * @throws exception.OurException
     */
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
    public boolean modifyUser(String password, String email, String name, String telephone, String surname, String username, String gender) throws OurException {
        return DAO.modifyUser(password, email, name, telephone, surname, username, gender);
    }

    /**
     * Retrieves a list of usernames for GUI combo boxes.
     *
     * @return ArrayList of usernames
     * @throws exception.OurException
     */
    public ArrayList<String> comboBoxInsert() throws OurException {
        return DAO.comboBoxInsert();
    }

    //[USERS]
    /**
     * Finds a profile by username.
     *
     * @param username The username to search for
     * @return Profile object if found, null otherwise
     * @throws exception.OurException
     */
    public Profile findProfileByUsername(String username) throws OurException {
        return DAO.findProfileByUsername(username);
    }

    //[VIDEOGAMES]
    /**
     * Retrieves all video games from the database.
     *
     * @return ArrayList of all VideoGame objects
     * @throws exception.OurException
     */
    public ArrayList<VideoGame> getAllVideoGames() throws OurException {
        return DAO.getAllVideoGames();
    }

    /**
     * Retrieves games from a specific user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @return ArrayList of VideoGame objects in the specified list
     * @throws exception.OurException
     */
    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException {
        return DAO.getGamesFromList(username, listName);
    }

    /**
     * Verifies if a game is in a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game
     * @return true if the game is in the list, false otherwise
     * @throws exception.OurException
     */
    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException {
        return DAO.verifyGameInList(username, listName, gameId);
    }

    /**
     * Adds a game to a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to add
     * @throws exception.OurException
     */
    public void addGameToList(String username, String listName, int gameId) throws OurException {
        DAO.addGameToList(username, listName, gameId);
    }

    /**
     * Adds multiple games to a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to add
     * @throws exception.OurException
     */
    public void addGamesToList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        DAO.addGamesToList(username, listName, games);
    }

    /**
     * Removes a game from a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to remove
     * @throws exception.OurException
     */
    public void removeGameFromList(String username, String listName, int gameId) throws OurException {
        DAO.removeGameFromList(username, listName, gameId);
    }

    /**
     * Removes multiple games from a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to remove
     * @throws exception.OurException
     */
    public void removeGamesFromList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        DAO.removeGamesFromList(username, listName, games);
    }

    /**
     * Finds a video game by its name.
     *
     * @param gameName The name of the game to search for
     * @return VideoGame object if found, null otherwise
     * @throws exception.OurException
     */
    public VideoGame findVideoGameByName(String gameName) throws OurException {
        return DAO.findVideoGameByName(gameName);
    }

    //[LISTS]
    /**
     * Retrieves all list names for a specific user.
     *
     * @param username The username
     * @return ArrayList of list names
     * @throws exception.OurException
     */
    public ArrayList<String> getUserLists(String username) throws OurException {
        return DAO.getUserLists(username);
    }

    /**
     * Creates a new list for a user.
     *
     * @param profile The user's profile
     * @param listName The name of the new list
     * @throws exception.OurException
     */
    public void newList(Profile profile, String listName) throws OurException {
        DAO.newList(profile, listName);
    }

    /**
     * Deletes a user's list.
     *
     * @param username The username
     * @param listName The name of the list to delete
     * @throws exception.OurException
     */
    public void deleteList(String username, String listName) throws OurException {
        DAO.deleteList(username, listName);
    }

    /**
     * Verifies if a list name already exists for a user.
     *
     * @param username The username
     * @param listName The list name to verify
     * @return true if the list name exists, false otherwise
     * @throws exception.OurException
     */
    public boolean verifyListName(String username, String listName) throws OurException {
        return DAO.verifyListName(username, listName);
    }

    /**
     * Renames a user's list.
     *
     * @param username The username
     * @param listName The current name of the list
     * @param listNewName The new name for the list
     * @throws exception.OurException
     */
    public void renameList(String username, String listName, String listNewName) throws OurException {
        DAO.renameList(username, listName, listNewName);
    }

    //[REVIEWS]
    /**
     * Finds a specific review by username and game ID.
     *
     * @param username The username
     * @param gameId The game ID
     * @return Review object if found, null otherwise
     * @throws exception.OurException
     */
    public Review findReview(String username, int gameId) throws OurException {
        return DAO.findReview(username, gameId);
    }

    /**
     * Finds all reviews for a specific game.
     *
     * @param gameId The game ID
     * @return ArrayList of Review objects for the specified game
     * @throws exception.OurException
     */
    public ArrayList<Review> findReviews(int gameId) throws OurException {
        return DAO.findReviews(gameId);
    }

    /**
     * Retrieves all reviews from the database.
     *
     * @return ArrayList of all Review objects
     * @throws exception.OurException
     */
    public ArrayList<Review> getAllReviews() throws OurException {
        return DAO.getAllReviews();
    }

    /**
     * Saves a new review or updates an existing one.
     *
     * @param review The Review object to save or update
     * @return true if the operation succeeds, false otherwise
     * @throws exception.OurException
     */
    public boolean saveOrUpdateReview(Review review) throws OurException {
        return DAO.saveOrUpdateReview(review);
    }

    /**
     * Deletes a review from the database.
     *
     * @param review The Review object to delete
     * @throws exception.OurException
     */
    public void deleteReview(Review review) throws OurException {
        DAO.deleteReview(review);
    }
    
    /**
     * Show the video of new games.
     *
     * @throws exception.OurException
     */
    public void openVideo() throws OurException {
        try {
            WebView webview = new WebView();
            webview.getEngine().load("https://youtu.be/phyKDIryZWk?si=ugkWCRi_GpBrg_0z");
            webview.setPrefSize(640, 390);

            Stage stage = new Stage();
            stage.setScene(new Scene(webview));
            stage.setTitle("Tutorial Video");
            stage.setFullScreen(true);
            stage.show();
        } catch (Exception ex) {
            throw new OurException("Failed to open user manual: " + ex.getMessage());
        }
    }
    
    /**
     * Show the user manual.
     *
     * @throws exception.OurException
     */
    public void openManual() throws OurException {
        try {
            File path = new File("user manual/UserManual.pdf");
            if (!path.exists()) {
                throw new OurException("User manual not found at: " + path.getAbsolutePath());
            }
            Desktop.getDesktop().open(path);
        } catch (Exception ex) {
            throw new OurException("Failed to open user manual");
        }
    }
    
    /**
     * Generates a report with the specified name.
     *
     * @param name The name for the report
     * @throws exception.OurException
     */
    public void generateReport(String name) throws OurException {
        DAO.generateReport(name);
    }
}