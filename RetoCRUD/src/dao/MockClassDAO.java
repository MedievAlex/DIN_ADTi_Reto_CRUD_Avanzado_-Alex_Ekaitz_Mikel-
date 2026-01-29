package dao;

import exception.OurException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import model.*;
/**
 * Mock implementation of ClassDAO for testing purposes. This class provides a
 * simulated data access layer that can be configured to return predefined
 * responses or throw exceptions for testing various application scenarios
 * without requiring a real database connection.
 */
public class MockClassDAO implements ClassDAO
{
    private Profile mockUser;
    private final ArrayList<VideoGame> mockVideoGames;
    private final ArrayList<Review> mockReviews;
    private final Map<String, ArrayList<String>> userLists;
    private final Map<String, Map<String, ArrayList<VideoGame>>> userListGames;
    private boolean shouldThrowException;
    private OurException exceptionToThrow;

    /**
     * Constructs a new MockClassDAO with default test data. Initializes the
     * mock with sample profiles, video games, and empty exception state.
     */
    public MockClassDAO()
    {
        this.mockUser = new User("MALE", "ES1234567890123456789012", "testuser", "Ab123456",
                "test@test.com", "Test", "123456789", "User");

        this.mockVideoGames = new ArrayList<>();
        mockVideoGames.add(new VideoGame(1, "The Legend of Zelda", LocalDate.of(2017, 3, 3),
                Platform.NINTENDO, Pegi.PEGI12));
        mockVideoGames.add(new VideoGame(2, "God of War", LocalDate.of(2018, 4, 20),
                Platform.PLAYSTATION, Pegi.PEGI18));
        mockVideoGames.add(new VideoGame(3, "Halo Infinite", LocalDate.of(2021, 12, 8),
                Platform.XBOX, Pegi.PEGI16));

        this.mockReviews = new ArrayList<>();
        mockReviews.add(new Review(mockUser, mockVideoGames.get(0), 9, "Amazing game!", 
        LocalDate.now(), Platform.NINTENDO));

        this.userLists = new HashMap<>();
        this.userListGames = new HashMap<>();

        ArrayList<String> defaultLists = new ArrayList<>();
        defaultLists.add("My Games");
        defaultLists.add("Favorites");
        defaultLists.add("Nintendo");
        defaultLists.add("Playstation");
        userLists.put("testuser", defaultLists);

        Map<String, ArrayList<VideoGame>> testUserLists = new HashMap<>();
        testUserLists.put("My Games", new ArrayList<>());
        testUserLists.put("Favorites", new ArrayList<>());
        userListGames.put("testuser", testUserLists);
        
        this.shouldThrowException = false;
    }

    /**
     * Configures the mock to throw an exception on subsequent method calls.
     *
     * @param shouldThrow if true, subsequent method calls will throw the
     * specified exception
     * @param exception the exception to throw when shouldThrow is true
     */
    public void setShouldThrowException(boolean shouldThrow, OurException exception)
    {
        this.shouldThrowException = shouldThrow;
        this.exceptionToThrow = exception;
    }

    /**
     * Sets the mock profile to be returned by subsequent method calls.
     *
     * @param profile the Profile object to set as the mock response
     */
    public void setMockProfile(Profile profile)
    {
        this.mockUser = profile;
    }

    /**
     * Adds a mock video game to the collection.
     *
     * @param game the VideoGame to add
     */
    public void addMockVideoGame(VideoGame game)
    {
        this.mockVideoGames.add(game);
    }

    /**
     * Adds a mock review to the collection.
     *
     * @param review the Review to add
     */
    public void addMockReview(Review review)
    {
        this.mockReviews.add(review);
    }

    // ==================== USERS ====================
    /**
     * Authenticates a user with the provided credentials.
     * Returns the mock user if credentials match "testuser"/"Ab123456".
     *
     * @param username The username to authenticate
     * @param password The password to verify
     * @return Profile object if authentication succeeds
     * @throws OurException If credentials are invalid or shouldThrowException is true
     */
    @Override
    public Profile logIn(String username, String password) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if (mockUser != null && mockUser.getUsername().equals(username) && mockUser.getPassword().equals(password))
        {
            return mockUser;
        }
        throw new OurException("Invalid credentials");
    }

    /**
     * Simulates user registration.
     * Always returns true unless configured to throw an exception.
     *
     * @param gender The user's gender
     * @param cardNumber The user's card number
     * @param username The desired username
     * @param password The user's password
     * @param email The user's email address
     * @param name The user's first name
     * @param telephone The user's telephone number
     * @param surname The user's surname
     * @return true if registration succeeds
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public boolean signUp(String gender, String cardNumber, String username, String password,
            String email, String name, String telephone, String surname) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return true;
    }

    /**
     * Simulates user account deletion.
     * Always returns true unless configured to throw an exception.
     *
     * @param username The username of the account to delete
     * @param password The password for verification
     * @return true if deletion succeeds
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public boolean dropOutUser(String username, String password) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return true;
    }

    /**
     * Simulates admin account deletion.
     * Always returns true unless configured to throw an exception.
     *
     * @param usernameToDelete The username of the account to delete
     * @param adminUsername The administrator's username
     * @param adminPassword The administrator's password
     * @return true if deletion succeeds
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return true;
    }

    /**
     * Simulates user information modification.
     * Always returns true unless configured to throw an exception.
     *
     * @param password The new password (or existing if unchanged)
     * @param email The user's email address
     * @param name The user's first name
     * @param telephone The user's telephone number
     * @param surname The user's surname
     * @param username The user's username (cannot be changed)
     * @param gender The user's gender
     * @return true if modification succeeds
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public boolean modifyUser(String password, String email, String name, String telephone,
            String surname, String username, String gender) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return true;
    }

    /**
     * Returns a list of mock usernames for combo box population.
     *
     * @return ArrayList containing "User1", "User2", "User3"
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public ArrayList<String> comboBoxInsert() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        ArrayList<String> users = new ArrayList<>();
        users.add("User1");
        users.add("User2");
        users.add("User3");
        return users;
    }

    /**
     * Finds a mock profile by username.
     * Returns the mock user if username is "testuser".
     *
     * @param username The username to search for
     * @return Profile object if found
     * @throws OurException If user not found or shouldThrowException is true
     */
    @Override
    public Profile findProfileByUsername(String username) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if ("testuser".equals(username))
        {
            return mockUser;
        }
        throw new OurException("User not found");
    }

    // ==================== VIDEOGAMES ====================
    /**
     * Returns all mock video games.
     *
     * @return ArrayList of mock VideoGame objects
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public ArrayList<VideoGame> getAllVideoGames() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return new ArrayList<>(mockVideoGames);
    }

    /**
     * Returns games from a user's list.
     * Returns empty list if user or list doesn't exist.
     *
     * @param username The username
     * @param listName The name of the list
     * @return ArrayList of VideoGame objects in the specified list
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if (userListGames.containsKey(username) && userListGames.get(username).containsKey(listName))
        {
            return new ArrayList<>(userListGames.get(username).get(listName));
        }
        return new ArrayList<>();
    }

    /**
     * Verifies if a game is in a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to check
     * @return true if the game is in the list, false otherwise
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if (userListGames.containsKey(username) && userListGames.get(username).containsKey(listName))
        {
            return userListGames.get(username).get(listName).stream()
                    .anyMatch(game -> game.getV_id() == gameId);
        }
        return false;
    }

    /**
     * Adds a game to a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to add
     * @throws OurException If game not found or shouldThrowException is true
     */
    @Override
    public void addGameToList(String username, String listName, int gameId) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        VideoGame gameToAdd = mockVideoGames.stream()
                .filter(game -> game.getV_id() == gameId)
                .findFirst()
                .orElseThrow(() -> new OurException("Game not found"));

        if (!userListGames.containsKey(username))
        {
            userListGames.put(username, new HashMap<>());
        }
        if (!userListGames.get(username).containsKey(listName))
        {
            userListGames.get(username).put(listName, new ArrayList<>());
        }
        userListGames.get(username).get(listName).add(gameToAdd);
    }

    /**
     * Adds multiple games to a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to add
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void addGamesToList(String username, String listName, ArrayList<VideoGame> games) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        for (VideoGame game : games)
        {
            addGameToList(username, listName, game.getV_id());
        }
    }

    /**
     * Removes a game from a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param gameId The ID of the game to remove
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void removeGameFromList(String username, String listName, int gameId) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if (userListGames.containsKey(username) && userListGames.get(username).containsKey(listName))
        {
            userListGames.get(username).get(listName).removeIf(game -> game.getV_id() == gameId);
        }
    }

    /**
     * Removes multiple games from a user's list.
     *
     * @param username The username
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to remove
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void removeGamesFromList(String username, String listName, ArrayList<VideoGame> games) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        for (VideoGame game : games)
        {
            removeGameFromList(username, listName, game.getV_id());
        }
    }

    /**
     * Finds a video game by its name.
     *
     * @param gameName The name of the game to search for
     * @return VideoGame object if found
     * @throws OurException If game not found or shouldThrowException is true
     */
    @Override
    public VideoGame findVideoGameByName(String gameName) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return mockVideoGames.stream()
                .filter(game -> game.getV_name().equalsIgnoreCase(gameName))
                .findFirst()
                .orElseThrow(() -> new OurException("Game not found"));
    }

    // ==================== LISTS ====================
    /**
     * Retrieves all list names for a specific user.
     *
     * @param username The username
     * @return ArrayList of list names for the user
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public ArrayList<String> getUserLists(String username) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return new ArrayList<>(userLists.getOrDefault(username, new ArrayList<>()));
    }

    /**
     * Creates a new list for a user.
     *
     * @param profile The user's profile
     * @param listName The name of the new list
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void newList(Profile profile, String listName) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        String username = profile.getUsername();
        if (!userLists.containsKey(username))
        {
            userLists.put(username, new ArrayList<>());
            userListGames.put(username, new HashMap<>());
        }
        userLists.get(username).add(listName);
        userListGames.get(username).put(listName, new ArrayList<>());
    }

    /**
     * Deletes a user's list.
     *
     * @param username The username
     * @param listName The name of the list to delete
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void deleteList(String username, String listName) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if (userLists.containsKey(username))
        {
            userLists.get(username).remove(listName);
            userListGames.get(username).remove(listName);
        }
    }

    /**
     * Verifies if a list name already exists for a user.
     *
     * @param username The username
     * @param listName The list name to verify
     * @return true if the list name exists, false otherwise
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public boolean verifyListName(String username, String listName) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return userLists.containsKey(username) && userLists.get(username).contains(listName);
    }

    /**
     * Renames a user's list.
     *
     * @param username The username
     * @param listName The current name of the list
     * @param listNewName The new name for the list
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void renameList(String username, String listName, String listNewName) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if (userLists.containsKey(username) && userLists.get(username).contains(listName))
        {
            int index = userLists.get(username).indexOf(listName);
            userLists.get(username).set(index, listNewName);

            ArrayList<VideoGame> games = userListGames.get(username).remove(listName);
            userListGames.get(username).put(listNewName, games);
        }
    }

    // ==================== REVIEWS ====================
    /**
     * Finds a specific review by username and game ID.
     *
     * @param username The username
     * @param gameId The game ID
     * @return Review object if found, null otherwise
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public Review findReview(String username, int gameId) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return mockReviews.stream()
                .filter(review -> review.getProfile().getUsername().equals(username)
                && review.getVideogame().getV_id() == gameId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds all reviews for a specific game.
     *
     * @param gameId The game ID
     * @return ArrayList of Review objects for the specified game
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public ArrayList<Review> findReviews(int gameId) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        ArrayList<Review> gameReviews = new ArrayList<>();
        for (Review review : mockReviews)
        {
            if (review.getVideogame().getV_id() == gameId)
            {
                gameReviews.add(review);
            }
        }
        return gameReviews;
    }

    /**
     * Retrieves all mock reviews.
     *
     * @return ArrayList of all Review objects
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public ArrayList<Review> getAllReviews() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return new ArrayList<>(mockReviews);
    }

    /**
     * Saves a new review or updates an existing one.
     * Replaces existing review if one exists for same user and game.
     *
     * @param review The Review object to save or update
     * @return true if the operation succeeds
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public boolean saveOrUpdateReview(Review review) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        mockReviews.removeIf(r -> r.getProfile().getUsername().equals(review.getProfile().getUsername())
                && r.getVideogame().getV_id() == review.getVideogame().getV_id());
        mockReviews.add(review);
        return true;
    }

    /**
     * Deletes a review from the mock collection.
     *
     * @param review The Review object to delete
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void deleteReview(Review review) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        mockReviews.remove(review);
    }

    // ==================== OTHER ====================
    /**
     * Initializes the mock database.
     * No operation performed unless configured to throw an exception.
     *
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void initializeDefault() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
    }

    /**
     * Generates a mock report.
     * No operation performed unless configured to throw an exception.
     *
     * @param name The name for the report
     * @throws OurException If shouldThrowException is true
     */
    @Override
    public void generateReport(String name) throws OurException {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
    }
}