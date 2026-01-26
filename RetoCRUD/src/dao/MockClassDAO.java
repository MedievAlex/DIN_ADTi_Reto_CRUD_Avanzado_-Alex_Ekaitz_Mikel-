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
    @Override
    public Profile logIn(String username, String password) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        if ("testuser".equals(username) && "Ab123456".equals(password))
        {
            return mockUser;
        }
        throw new OurException("Invalid credentials");
    }

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

    @Override
    public boolean dropOutUser(String username, String password) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return true;
    }

    @Override
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return true;
    }

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

    @Override
    public ArrayList<String> comboBoxInsert() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        ArrayList<String> genders = new ArrayList<>();
        genders.add("MALE");
        genders.add("FEMALE");
        genders.add("OTHER");
        return genders;
    }

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
    @Override
    public ArrayList<VideoGame> getAllVideoGames() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return new ArrayList<>(mockVideoGames);
    }

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
    @Override
    public ArrayList<String> getUserLists(String username) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return new ArrayList<>(userLists.getOrDefault(username, new ArrayList<>()));
    }

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

    @Override
    public boolean verifyListName(String username, String listName) throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return userLists.containsKey(username) && userLists.get(username).contains(listName);
    }

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

    @Override
    public ArrayList<Review> getAllReviews() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
        return new ArrayList<>(mockReviews);
    }

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
    @Override
    public void initializeDefault() throws OurException
    {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
    }

    @Override
    public void generateReport(String name) throws OurException {
        if (shouldThrowException)
        {
            throw exceptionToThrow;
        }
    }
}