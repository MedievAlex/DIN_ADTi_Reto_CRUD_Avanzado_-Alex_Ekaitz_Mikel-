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
    /**
     * Authenticates a user or admin by username and password.
     *
     * @param username The username to authenticate
     * @param password The password to verify
     * @return Profile object if authentication succeeds, null otherwise
     * @throws OurException If there's an error during the authentication process
     */
    public Profile logIn(String username, String password) throws OurException;

    /**
     * Registers a new user in the system.
     *
     * @param gender The user's gender
     * @param cardNumber The user's card number
     * @param username The desired username
     * @param password The user's password
     * @param email The user's email address
     * @param name The user's first name
     * @param telephone The user's telephone number
     * @param surname The user's surname
     * @return true if registration succeeds, false otherwise
     * @throws OurException If there's an error during the registration process
     */
    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) throws OurException;

    /**
     * Deletes a user account from the system.
     *
     * @param username The username of the account to delete
     * @param password The password for verification
     * @return true if deletion succeeds, false otherwise
     * @throws OurException If there's an error during the deletion process
     */
    public boolean dropOutUser(String username, String password) throws OurException;

    /**
     * Deletes a user account as an administrator.
     *
     * @param usernameToDelete The username of the account to delete
     * @param adminUsername The administrator's username
     * @param adminPassword The administrator's password
     * @return true if deletion succeeds, false otherwise
     * @throws OurException If there's an error during the deletion process
     */
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException;

    /**
     * Modifies an existing user's information.
     *
     * @param password The new password (or existing if unchanged)
     * @param email The user's email address
     * @param name The user's first name
     * @param telephone The user's telephone number
     * @param surname The user's surname
     * @param username The user's username (cannot be changed)
     * @param gender The user's gender
     * @return true if modification succeeds, false otherwise
     * @throws OurException If there's an error during the modification process
     */
    public boolean modifyUser(String password, String email, String name, String telephone, String surname, String username, String gender) throws OurException;

    /**
     * Retrieves a list of all usernames for GUI components.
     *
     * @return ArrayList of usernames
     * @throws OurException If there's an error retrieving the usernames
     */
    public ArrayList<String> comboBoxInsert() throws OurException;

    /**
     * Finds a user profile by username.
     *
     * @param username The username to search for
     * @return Profile object if found, null otherwise
     * @throws OurException If there's an error during the search process
     */
    public Profile findProfileByUsername(String username) throws OurException;

    //[VIDEOGAMES]
    /**
     * Retrieves all video games from the database.
     *
     * @return ArrayList of all VideoGame objects
     * @throws OurException If there's an error retrieving the video games
     */
    public ArrayList<VideoGame> getAllVideoGames() throws OurException;

    /**
     * Retrieves games from a specific user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @return ArrayList of VideoGame objects in the specified list
     * @throws OurException If there's an error retrieving the games
     */
    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException;

    /**
     * Verifies if a specific game is in a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to check
     * @return true if the game is in the list, false otherwise
     * @throws OurException If there's an error during the verification
     */
    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException;

    /**
     * Adds a game to a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to add
     * @throws OurException If there's an error adding the game
     */
    public void addGameToList(String username, String listName, int gameId) throws OurException;

    /**
     * Adds multiple games to a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to add
     * @throws OurException If there's an error adding the games
     */
    public void addGamesToList(String username, String listName, ArrayList<VideoGame> games) throws OurException;

    /**
     * Removes a game from a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to remove
     * @throws OurException If there's an error removing the game
     */
    public void removeGameFromList(String username, String listName, int gameId) throws OurException;

    /**
     * Removes multiple games from a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to remove
     * @throws OurException If there's an error removing the games
     */
    public void removeGamesFromList(String username, String listName, ArrayList<VideoGame> games) throws OurException;

    /**
     * Finds a video game by its name.
     *
     * @param gameName The name of the game to search for
     * @return VideoGame object if found, null otherwise
     * @throws OurException If there's an error during the search
     */
    public VideoGame findVideoGameByName(String gameName) throws OurException;

    //[LISTS]
    /**
     * Retrieves all list names for a specific user.
     *
     * @param username The username
     * @return ArrayList of list names
     * @throws OurException If there's an error retrieving the lists
     */
    public ArrayList<String> getUserLists(String username) throws OurException;

    /**
     * Creates a new list for a user.
     *
     * @param profile The user's profile
     * @param listName The name of the new list
     * @throws OurException If there's an error creating the list
     */
    public void newList(Profile profile, String listName) throws OurException;

    /**
     * Deletes a user's list.
     *
     * @param username The username
     * @param listName The name of the list to delete
     * @throws OurException If there's an error deleting the list
     */
    public void deleteList(String username, String listName) throws OurException;

    /**
     * Verifies if a list name already exists for a user.
     *
     * @param username The username
     * @param listName The list name to verify
     * @return true if the list name exists, false otherwise
     * @throws OurException If there's an error during verification
     */
    public boolean verifyListName(String username, String listName) throws OurException;

    /**
     * Renames a user's list.
     *
     * @param username The username
     * @param listName The current name of the list
     * @param listNewName The new name for the list
     * @throws OurException If there's an error renaming the list
     */
    public void renameList(String username, String listName, String listNewName) throws OurException;

    //[REVIEWS]
    /**
     * Finds a specific review by username and game ID.
     *
     * @param username The username
     * @param gameId The game ID
     * @return Review object if found, null otherwise
     * @throws OurException If there's an error during the search
     */
    public Review findReview(String username, int gameId) throws OurException;

    /**
     * Finds all reviews for a specific game.
     *
     * @param gameId The game ID
     * @return ArrayList of Review objects for the specified game
     * @throws OurException If there's an error retrieving the reviews
     */
    public ArrayList<Review> findReviews(int gameId) throws OurException;

    /**
     * Retrieves all reviews from the database.
     *
     * @return ArrayList of all Review objects
     * @throws OurException If there's an error retrieving the reviews
     */
    public ArrayList<Review> getAllReviews() throws OurException;

    /**
     * Saves a new review or updates an existing one.
     *
     * @param review The Review object to save or update
     * @return true if the operation succeeds, false otherwise
     * @throws OurException If there's an error saving or updating the review
     */
    public boolean saveOrUpdateReview(Review review) throws OurException;

    /**
     * Deletes a review from the database.
     *
     * @param review The Review object to delete
     * @throws OurException If there's an error deleting the review
     */
    public void deleteReview(Review review) throws OurException;

    //[OTHER]
    /**
     * Initializes the database with default data if needed.
     *
     * @throws OurException If there's an error during initialization
     */
    public void initializeDefault() throws OurException;
    
    /**
     * Generates a report with the specified name.
     *
     * @param name The name for the report
     * @throws OurException If there's an error generating the report
     */
    public void generateReport(String name) throws OurException;
}