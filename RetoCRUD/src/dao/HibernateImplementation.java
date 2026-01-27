package dao;

import exception.ErrorMessages;
import exception.OurException;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.*;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import threads.SessionThread;

/**
 * Hibernate implementation of the ClassDAO interface.
 * Provides data access operations for users, video games, lists, and reviews
 * using Hibernate ORM with connection pool management through SessionThread.
 *
 * @author ema
 */
public class HibernateImplementation implements ClassDAO {
    /**
     * List of active SessionThread instances for connection management.
     */
    private final List<SessionThread> activeThreads = new ArrayList<>();

    /**
     * Starts a new SessionThread and adds it to the active threads list.
     *
     * @return The started SessionThread
     */
    private SessionThread startSessionThread() {
        SessionThread thread = new SessionThread();
        activeThreads.add(thread);
        thread.start();
        return thread;
    }

    /**
     * Cleans up all active threads by releasing their sessions and waiting for them to finish.
     * Should be called when shutting down the application or when all operations are complete.
     */
    public void cleanupThreads() {
        for (SessionThread t : activeThreads) {
            t.releaseSession();
        }

        for (SessionThread t : activeThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        }
        activeThreads.clear();
    }

    /**
     * Authenticates a user or admin by username and password.
     *
     * @param username The username to authenticate
     * @param password The password to verify
     * @return The authenticated Profile (User or Admin), or null if authentication fails
     * @throws OurException if a database error occurs during login
     */
    @Override
    public Profile logIn(String username, String password) throws OurException {
        Session session = null;
        try {
            session = HibernateUtil.getSession();

            String hql = "FROM User WHERE username = :username AND password = :password";
            User user = session.createQuery(hql, User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();

            if (user != null) {
                return user;
            }

            hql = "FROM Admin WHERE username = :username AND password = :password";
            Admin admin = session.createQuery(hql, Admin.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();

            return admin;
        } catch (Exception e) {
            throw new OurException(ErrorMessages.LOGIN);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Registers a new user in the system.
     * Creates a new User entity and initializes their "My Games" list with the first game.
     *
     * @param gender User's gender
     * @param cardNumber User's card number
     * @param username Unique username for the new user
     * @param password User's password
     * @param email User's email address
     * @param name User's first name
     * @param telephone User's phone number
     * @param surname User's surname
     * @return true if registration is successful
     * @throws OurException if registration fails or connection pool is full
     */
    @Override
    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);

            session.beginTransaction();

            User user = new User(gender, cardNumber, username, password, email, name, telephone, surname);
            session.save(user);

            ArrayList<VideoGame> allGames = getAllVideoGames();
            session.save(new Listed(user, allGames.get(0), "My Games"));

            session.getTransaction().commit();
            return true;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.REGISTER_USER);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Deletes a user account from the system.
     * Validates the password before deletion.
     *
     * @param username The username of the account to delete
     * @param password The password for authentication
     * @return true if the user is successfully deleted
     * @throws OurException if deletion fails, credentials are invalid, or connection pool is full
     */
    @Override
    public boolean dropOutUser(String username, String password) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);

            session.beginTransaction();

            User user = session.get(User.class, username);
            if (user == null || !user.getPassword().equals(password)) {
                throw new OurException(ErrorMessages.LOGIN);
            }

            session.delete(user);
            session.getTransaction().commit();

            return true;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DELETE_USER);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Allows an admin to delete another user or admin account.
     * Requires valid admin credentials.
     *
     * @param usernameToDelete The username of the account to delete
     * @param adminUsername The admin's username
     * @param adminPassword The admin's password
     * @return true if the account is successfully deleted
     * @throws OurException if deletion fails, admin credentials are invalid, user not found, or connection pool is full
     */
    @Override
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Admin admin = session.get(Admin.class, adminUsername);
            if (admin == null || !admin.getPassword().equals(adminPassword)) {
                throw new OurException(ErrorMessages.INVALID_ADMIN_CREDENTIALS);
            }

            User userToDelete = session.get(User.class, usernameToDelete);
            if (userToDelete != null) {
                session.delete(userToDelete);
            } else {
                Admin adminToDelete = session.get(Admin.class, usernameToDelete);
                if (adminToDelete != null) {
                    session.delete(adminToDelete);
                } else {
                    throw new OurException(ErrorMessages.USER_NOT_FOUND);
                }
            }

            session.getTransaction().commit();
            return true;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DELETE_USER);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Updates user profile information.
     * If the profile is a User (not Admin), also updates the gender field.
     *
     * @param password New password
     * @param email New email address
     * @param name New first name
     * @param telephone New phone number
     * @param surname New surname
     * @param username Username of the profile to update
     * @param gender New gender (only for User profiles)
     * @return true if the profile is successfully updated
     * @throws OurException if update fails, user not found, or connection pool is full
     */
    @Override
    public boolean modifyUser(String password, String email, String name, String telephone, String surname, String username, String gender) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);
            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);
            if (profile == null) {
                throw new OurException(ErrorMessages.USER_NOT_FOUND);
            }

            profile.setPassword(password);
            profile.setEmail(email);
            profile.setName(name);
            profile.setTelephone(telephone);
            profile.setSurname(surname);

            if (profile instanceof User) {
                ((User) profile).setGender(gender);
            }

            session.update(profile);
            session.getTransaction().commit();
            return true;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.UPDATE_USER);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Retrieves all usernames for populating combo boxes or selection lists.
     *
     * @return ArrayList of all usernames in the system
     * @throws OurException if retrieval fails
     */
    @Override
    public ArrayList<String> comboBoxInsert() throws OurException {
        Session session = null;
        try {
            session = HibernateUtil.getSession();

            List<String> usernames = session.createQuery(
                    "SELECT u.username FROM User u", String.class
            ).list();

            return new ArrayList<>(usernames);
        } catch (Exception e) {
            throw new OurException(ErrorMessages.GET_USERS);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Finds a profile (User or Admin) by username.
     *
     * @param username The username to search for
     * @return The Profile object, or null if not found
     * @throws OurException if search fails or connection pool is full
     */
    @Override
    public Profile findProfileByUsername(String username) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);
            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);

            session.getTransaction().commit();
            return profile;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Retrieves all video games from the database.
     *
     * @return ArrayList of all VideoGame objects
     * @throws OurException if retrieval fails or connection pool is full
     */
    @Override
    public ArrayList<VideoGame> getAllVideoGames() throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);
            session.beginTransaction();

            ArrayList<VideoGame> gamesList = new ArrayList<>(
                    session.createNativeQuery("SELECT * FROM videogame", VideoGame.class).getResultList()
            );

            session.getTransaction().commit();
            return gamesList;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Retrieves all games from a specific user's list.
     *
     * @param username The username of the profile
     * @param listName The name of the list
     * @return ArrayList of VideoGame objects in the specified list
     * @throws OurException if retrieval fails or connection pool is full
     */
    @Override
    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException {
        ArrayList<VideoGame> games = new ArrayList<>();
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);

            if (profile != null) {
                List<Listed> listedGames = session.createQuery(
                        "FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName", Listed.class)
                        .setParameter("username", username)
                        .setParameter("listName", listName)
                        .list();

                for (Listed listed : listedGames) {
                    games.add(listed.getVideogame());
                }
            }

            session.getTransaction().commit();

            return games;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Verifies if a specific game exists in a user's list.
     *
     * @param username The username of the profile
     * @param listName The name of the list
     * @param gameId The ID of the game to verify
     * @return true if the game exists in the list, false otherwise
     * @throws OurException if verification fails or connection pool is full
     */
    @Override
    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        boolean listed = false;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);

            if (profile != null) {
                List<Listed> listedGames = session.createQuery(
                        "FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame.v_id = :gameId", Listed.class
                )
                        .setParameter("username", username)
                        .setParameter("listName", listName)
                        .setParameter("gameId", gameId)
                        .list();

                if (!listedGames.isEmpty()) {
                    listed = true;
                }
            }

            session.getTransaction().commit();

            return listed;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Adds a game to a user's list.
     *
     * @param username The username of the profile
     * @param listName The name of the list
     * @param gameId The ID of the game to add
     * @throws OurException if addition fails or connection pool is full
     */
    @Override
    public void addGameToList(String username, String listName, int gameId) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();
            Profile profile = session.get(Profile.class, username);
            VideoGame game = session.get(VideoGame.class, gameId);
            if (profile != null && game != null) {
                Listed listed = new Listed(profile, game, listName);
                session.save(listed);
            }
            session.getTransaction().commit();

        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Adds multiple games to a user's list.
     *
     * @param username The username of the profile
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to add
     * @throws OurException if addition fails or connection pool is full
     */
    @Override
    public void addGamesToList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);
            for (VideoGame game : games) {
                VideoGame gameToAdd = session.get(VideoGame.class, game.getV_id());

                if (profile != null && gameToAdd != null) {
                    Listed listed = new Listed(profile, gameToAdd, listName);
                    session.save(listed);
                }
            }

            session.getTransaction().commit();
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Removes a game from a user's list.
     * If the list is "My Games", removes the game from all lists.
     *
     * @param username The username of the profile
     * @param listName The name of the list
     * @param gameId The ID of the game to remove
     * @throws OurException if removal fails or connection pool is full
     */
    @Override
    public void removeGameFromList(String username, String listName, int gameId) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            if ("My Games".equals(listName)) {
                session.createQuery("DELETE FROM Listed l WHERE l.profile.username = :username AND l.videogame.v_id = :gameId")
                        .setParameter("username", username)
                        .setParameter("gameId", gameId)
                        .executeUpdate();
            } else {
                session.createQuery("DELETE FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame.v_id = :gameId")
                        .setParameter("username", username)
                        .setParameter("listName", listName)
                        .setParameter("gameId", gameId)
                        .executeUpdate();
            }

            session.getTransaction().commit();
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Removes multiple games from a user's list.
     *
     * @param username The username of the profile
     * @param listName The name of the list
     * @param games ArrayList of VideoGame objects to remove
     * @throws OurException if removal fails or connection pool is full
     */
    @Override
    public void removeGamesFromList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;

        if (!games.isEmpty()) {
            try {
                session = waitForSession(thread);

                session.beginTransaction();
                for (VideoGame game : games) {
                    session.createQuery("DELETE FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame.v_id = :gameId")
                            .setParameter("username", username)
                            .setParameter("listName", listName)
                            .setParameter("gameId", game.getV_id())
                            .executeUpdate();
                }

                session.getTransaction().commit();
            } catch (OurException e) {
                throw e;
            } catch (Exception e) {
                if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                    try {
                        session.getTransaction().rollback();
                    } catch (Exception rollbackEx) {
                    }
                }
                throw new OurException(ErrorMessages.DATABASE);
            } finally {
                thread.releaseSession();
            }
        }
    }

    /**
     * Finds a video game by its name.
     *
     * @param name The name of the video game to search for
     * @return The VideoGame object, or null if not found
     * @throws OurException if search fails or connection pool is full
     */
    @Override
    public VideoGame findVideoGameByName(String name) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);
            session.beginTransaction();

            VideoGame videoGame = (VideoGame) session.createNativeQuery(
                    "SELECT * FROM videogame WHERE v_name = :name", VideoGame.class)
                    .setParameter("name", name)
                    .uniqueResult();

            session.getTransaction().commit();
            return videoGame;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Retrieves all list names for a specific user.
     *
     * @param username The username of the profile
     * @return ArrayList of distinct list names
     * @throws OurException if retrieval fails or connection pool is full
     */
    @Override
    public ArrayList<String> getUserLists(String username) throws OurException {
        ArrayList<String> lists = new ArrayList<>();
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);

            if (profile != null) {
                List<String> listNames = session.createQuery(
                        "SELECT DISTINCT l.listName FROM Listed l WHERE l.profile.username = :username",
                        String.class
                )
                        .setParameter("username", username)
                        .list();

                lists.addAll(listNames);
            }

            session.getTransaction().commit();

            return lists;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Creates a new list for a profile.
     * Initializes the list with the first game in the database.
     *
     * @param profile The Profile object for which to create the list
     * @param listName The name of the new list
     * @throws OurException if creation fails or connection pool is full
     */
    @Override
    public void newList(Profile profile, String listName) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Listed listed = new Listed(profile, session.get(VideoGame.class, 1), listName);

            session.save(listed);

            session.getTransaction().commit();
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Deletes all entries of a specific list for a user.
     *
     * @param username The username of the profile
     * @param listName The name of the list to delete
     * @throws OurException if deletion fails or connection pool is full
     */
    @Override
    public void deleteList(String username, String listName) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            session.createQuery("DELETE FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName")
                    .setParameter("username", username)
                    .setParameter("listName", listName)
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Verifies if a list name already exists for a user.
     *
     * @param username The username of the profile
     * @param listName The list name to verify
     * @return true if the list name exists, false otherwise
     * @throws OurException if verification fails or connection pool is full
     */
    @Override
    public boolean verifyListName(String username, String listName) throws OurException {
        boolean nameExist = true;
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);

            if (profile != null) {
                List<Listed> list = session.createQuery(
                        "FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName", Listed.class)
                        .setParameter("username", username)
                        .setParameter("listName", listName)
                        .list();

                if (list.isEmpty()) {
                    nameExist = false;
                }

                session.getTransaction().commit();
            }

            return nameExist;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Renames an existing list for a user.
     *
     * @param username The username of the profile
     * @param listName The current name of the list
     * @param listNewName The new name for the list
     * @throws OurException if renaming fails or connection pool is full
     */
    @Override
    public void renameList(String username, String listName, String listNewName) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);
            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);
            if (profile != null) {
                session.createQuery(
                        "UPDATE Listed l SET l.listName = :listNewName WHERE l.profile.username = :username AND l.listName = :listName")
                        .setParameter("listName", listName)
                        .setParameter("listNewName", listNewName)
                        .setParameter("username", username)
                        .executeUpdate();
            }

            session.getTransaction().commit();
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Finds a specific review by username and game ID.
     *
     * @param username The username of the reviewer
     * @param gameId The ID of the game
     * @return The Review object, or null if not found
     * @throws OurException if search fails or connection pool is full
     */
    @Override
    public Review findReview(String username, int gameId) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        Review review = null;
        try {
            session = waitForSession(thread);

            session.beginTransaction();
            review = session.createQuery("SELECT r FROM Review r JOIN r.profile p JOIN r.videogame v WHERE p.username = :username AND v.v_id = :gameId", Review.class)
                    .setParameter("username", username)
                    .setParameter("gameId", gameId)
                    .setMaxResults(1)
                    .uniqueResult();

            session.getTransaction().commit();

            return review;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Retrieves all reviews for a specific game.
     *
     * @param gameId The ID of the game
     * @return ArrayList of Review objects for the game
     * @throws OurException if retrieval fails or connection pool is full
     */
    @Override
    public ArrayList<Review> findReviews(int gameId) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        ArrayList<Review> review = new ArrayList<>();

        try {
            session = waitForSession(thread);
            session.beginTransaction();
            List<Review> reviews = session.createQuery("FROM Review r WHERE r.videogame.v_id = :gameId", Review.class)
                    .setParameter("gameId", gameId)
                    .list();
            
            review.addAll(reviews);
            session.getTransaction().commit();
            return review;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Retrieves all reviews in the system, ordered by review date descending.
     *
     * @return ArrayList of all Review objects
     * @throws OurException if retrieval fails or connection pool is full
     */
    @Override
    public ArrayList<Review> getAllReviews() throws OurException {
        ArrayList<Review> reviews;
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();
            List<Review> reviewList = session.createQuery("FROM Review r ORDER BY r.reviewDate DESC", Review.class).list();
            reviews = new ArrayList<>(reviewList);

            session.getTransaction().commit();

            return reviews;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Saves a new review or updates an existing review.
     * If a review already exists for the user-game combination, updates it.
     * Otherwise, creates a new review.
     *
     * @param review The Review object to save or update
     * @return true if the operation is successful
     * @throws OurException if save/update fails, profile or game not found, or connection pool is full
     */
    @Override
    public boolean saveOrUpdateReview(Review review) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;

        try {
            session = waitForSession(thread);

            session.beginTransaction();

            Profile managedProfile = session.get(Profile.class, review.getProfile().getUsername());
            if (managedProfile == null) {
                throw new OurException(ErrorMessages.PROFILE_NOT_FOUND);
            }

            VideoGame managedGame = session.get(VideoGame.class, review.getVideogame().getV_id());
            if (managedGame == null) {
                throw new OurException(ErrorMessages.GAME_NOT_FOUND);
            }
            review.setProfile(managedProfile);
            review.setVideogame(managedGame);

            ReviewId pk = new ReviewId(
                    managedGame.getV_id(),
                    managedProfile.getUsername()
            );
            Review existing = session.get(Review.class, pk);

            if (existing != null) {
                existing.setScore(review.getScore());
                existing.setDescription(review.getDescription());
                existing.setPlatform(review.getPlatform());
                existing.setReviewDate(review.getReviewDate());
            } else {
                session.persist(review);
            }

            session.getTransaction().commit();
            return true;

        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.SAVE_REVIEW_ERROR);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Deletes a review from the database.
     *
     * @param review The Review object to delete
     * @throws OurException if deletion fails or connection pool is full
     */
    @Override
    public void deleteReview(Review review) throws OurException {
        SessionThread thread = startSessionThread();
        Session session = null;
        try {
            session = waitForSession(thread);

            session.beginTransaction();
            session.createQuery("DELETE FROM Review r WHERE r.profile.username = :username AND r.videogame.v_id = :gameId")
                    .setParameter("username", review.getProfile().getUsername())
                    .setParameter("gameId", review.getVideogame().getV_id())
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null && session.getTransaction().isActive()) {
                try {
                    session.getTransaction().rollback();
                } catch (Exception rollbackEx) {
                }
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    /**
     * Initializes the database with default data including users, admins, games, lists, and reviews.
     * Only creates entities that don't already exist in the database.
     *
     * @throws OurException if initialization fails
     */
    @Override
    public void initializeDefault() throws OurException {
        Session session = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            String[][] users = {
                {"User", "jlopez", "Masculino", "AB1234567890123456789012", "pass123", "jlopez@example.com", "Juan", "987654321", "Lopez", null},
                {"User", "mramirez", "Femenino", "ZX9081726354891027364512", "pass456", "mramirez@example.com", "Maria", "912345678", "Ramirez", null},
                {"User", "cperez", "Masculino", "LM0011223344556677889900", "pass789", "cperez@example.com", "Carlos", "934567890", "Perez", null},
                {"Admin", "asanchez", "CTA-001", null, "qwerty", "asanchez@example.com", "Ana", "900112233", "Sanchez", null},
                {"Admin", "rluna", "CTA-002", null, "zxcvbn", "rluna@example.com", "Rosa", "955667788", "Luna", null}
            };

            for (String[] userData : users) {
                boolean exists;
                if (userData[0].equals("User")) {
                    exists = session.get(User.class, userData[1]) != null;
                    if (!exists) {
                        session.save(new User(userData[2], userData[3], userData[1], userData[4], userData[5], userData[6], userData[7], userData[8]));
                    }
                } else {
                    exists = session.get(Admin.class, userData[1]) != null;
                    if (!exists) {
                        session.save(new Admin(userData[2], userData[1], userData[4], userData[5], userData[6], userData[7], userData[8]));
                    }
                }
            }

            Object[][] games = {
                {"DEFAULT_GAME", LocalDate.now(), Platform.DEFAULT, Pegi.DEFAULT},
                {"Owlboy", LocalDate.of(2016, 11, 1), Platform.NINTENDO, Pegi.PEGI3},
                {"Animal Crossing New Horizons", LocalDate.of(2020, 5, 20), Platform.NINTENDO, Pegi.PEGI6},
                {"Detroit: Become Human", LocalDate.of(2018, 5, 25), Platform.PLAYSTATION, Pegi.PEGI16},
                {"ASTROBOT", LocalDate.of(2024, 9, 6), Platform.PLAYSTATION, Pegi.PEGI3},
                {"Call of Duty: Black Ops II", LocalDate.of(2012, 11, 13), Platform.PLAYSTATION, Pegi.PEGI18},
                {"Halo Infinite", LocalDate.of(2021, 12, 8), Platform.PC, Pegi.PEGI16},
                {"Balatro", LocalDate.of(2024, 2, 20), Platform.PC, Pegi.PEGI12}
            };

            for (Object[] gameData : games) {
                VideoGame existing = session.createQuery("FROM VideoGame v WHERE v.v_name = :name", VideoGame.class)
                        .setParameter("name", (String) gameData[0]).uniqueResult();
                if (existing == null) {
                    session.save(new VideoGame((String) gameData[0], (LocalDate) gameData[1], (Platform) gameData[2], (Pegi) gameData[3]));
                }
            }

            String[][] listEntries = {
                {"jlopez", "User", "My Games", "1"},
                {"mramirez", "User", "My Games", "1"},
                {"cperez", "User", "My Games", "1"},
                {"asanchez", "Admin", "My Games", "1"},
                {"rluna", "Admin", "My Games", "1"},
                {"asanchez", "Admin", "My Games", "2"},
                {"asanchez", "Admin", "My Games", "3"},
                {"asanchez", "Admin", "My Games", "5"},
                {"asanchez", "Admin", "NINTENDO", "1"},
                {"asanchez", "Admin", "NINTENDO", "2"},
                {"asanchez", "Admin", "NINTENDO", "3"},
                {"asanchez", "Admin", "PLAYSTATION", "1"},
                {"asanchez", "Admin", "PLAYSTATION", "5"}
            };

            for (String[] entry : listEntries) {
                Profile profile;
                if (entry[1].equals("User")) {
                    profile = session.get(User.class, entry[0]);
                } else {
                    profile = session.get(Admin.class, entry[0]);
                }

                VideoGame game = session.get(VideoGame.class, Integer.parseInt(entry[3]));
                Listed existing = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame.v_id = :gameId", Listed.class)
                        .setParameter("username", entry[0]).setParameter("listName", entry[2]).setParameter("gameId", Integer.parseInt(entry[3])).uniqueResult();
                if (existing == null) {
                    session.save(new Listed(profile, game, entry[2]));
                }
            }

            Object[][] reviews = {
                {"asanchez", "Admin", "2", 7, "Review description", Platform.NINTENDO},
                {"asanchez", "Admin", "3", 2, "Bad experience", Platform.NINTENDO},
                {"cperez", "User", "4", 5, "Bad game", Platform.PLAYSTATION},
                {"cperez", "User", "5", 10, "Great game", Platform.PLAYSTATION},
                {"jlopez", "User", "6", 6, "Mid game", Platform.PLAYSTATION}
            };

            for (Object[] reviewData : reviews) {
                Profile profile;
                if (((String) reviewData[1]).equals("User")) {
                    profile = session.get(User.class, (String) reviewData[0]);
                } else {
                    profile = session.get(Admin.class, (String) reviewData[0]);
                }

                VideoGame game = session.get(VideoGame.class, Integer.parseInt((String) reviewData[2]));
                Review existing = session.createQuery("FROM Review r WHERE r.profile.username = :username AND r.videogame.v_id = :gameId", Review.class)
                        .setParameter("username", (String) reviewData[0]).setParameter("gameId", Integer.parseInt((String) reviewData[2])).uniqueResult();
                if (existing == null) {
                    session.save(new Review(profile, game, (int) reviewData[3], (String) reviewData[4], LocalDate.now(), (Platform) reviewData[5]));
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    /**
     * Waits for a SessionThread to be ready and validates the connection.
     * Performs a test transaction to verify connection pool availability.
     *
     * @param thread The SessionThread to wait for
     * @return The Session object from the thread
     * @throws InterruptedException if the wait is interrupted
     * @throws OurException if session creation fails or connection pool is full
     */
    private Session waitForSession(SessionThread thread) throws InterruptedException, OurException {
        int attempts = 0;
        while (!thread.isReady() && attempts < 50) {
            Thread.sleep(10);
            attempts++;
        }

        if (thread.hasSessionCreationFailed()) {
            throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
        }

        Session session = thread.getSession();
        if (session == null) {
            throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
        }

        try {
            session.beginTransaction();
            session.getTransaction().rollback();
        } catch (org.hibernate.exception.JDBCConnectionException ex) {
            throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
        }

        return session;
    }
    
    /**
     * Generates a PDF report of all reviews using JasperReports.
     * The report is saved with a timestamp in the reports directory and opened automatically.
     *
     * @param name The name of the user generating the report
     * @throws OurException if report generation fails
     */
    @Override
    public void generateReport(String name) throws OurException {
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            HashMap<String, Object> params = new HashMap<>();
            params.put("GENERATED_BY", name);
            params.put("GENERATION_DATE", new java.util.Date());

            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    try {
                        String reportPath = "/reports/Report.jasper";
                        InputStream reportStream = getClass().getResourceAsStream(reportPath);

                        if (reportStream == null) {
                            throw new SQLException("Report not found: " + reportPath);
                        }

                        JasperPrint jasperPrint = JasperFillManager.fillReport(
                            reportStream, 
                            params, 
                            connection
                        );

                        File reportsDir = new File("reports");
                        if (!reportsDir.exists()) {
                            reportsDir.mkdirs();
                        }

                        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
                        String pdfPath = "reports/Report_" + timestamp + ".pdf";

                        JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

                        File pdfFile = new File(pdfPath);
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().open(pdfFile);
                        }

                    } catch (Exception ex) {
                        throw new SQLException("Error generating PDF: " + ex.getMessage(), ex);
                    }
                }
            });
        } catch (Exception ex) {
            throw new OurException("Error generating report: " + ex.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}