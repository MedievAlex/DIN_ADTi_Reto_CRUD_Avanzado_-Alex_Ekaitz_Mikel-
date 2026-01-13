package dao;

import exception.ErrorMessages;
import exception.OurException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Admin;
import model.Listed;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.Review;
import model.ReviewId;
import model.User;
import model.VideoGame;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import threads.SessionThread;

public class HibernateImplementation implements ClassDAO {

    private final List<SessionThread> activeThreads = new ArrayList<>();

    private SessionThread startSessionThread() {
        SessionThread thread = new SessionThread();
        activeThreads.add(thread);
        thread.start();
        return thread;
    }

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

    //[USERS]
    @Override
    public Profile logIn(String username, String password) throws OurException {
        Session session = null;

        try {
            session = HibernateUtil.getSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("username", username));
            criteria.add(Restrictions.eq("password", password));
            User user = (User) criteria.uniqueResult();
            if (user != null) {
                return user;
            }

            criteria = session.createCriteria(Admin.class);
            criteria.add(Restrictions.eq("username", username));
            criteria.add(Restrictions.eq("password", password));
            Admin admin = (Admin) criteria.uniqueResult();

            return admin;
        } catch (Exception e) {
            throw new OurException(ErrorMessages.LOGIN);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname) throws OurException {
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            User user = new User(gender, cardNumber, username, password, email, name, telephone, surname);

            session.save(user);

            ArrayList<VideoGame> allGames = getAllVideoGames();
            session.save(new Listed(session.get(User.class, username), allGames.get(0), "My Games"));

            session.getTransaction().commit();

            return true;
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.REGISTER_USER);
        } finally {
            thread.releaseSession();
        }
    }

    @Override
    public boolean dropOutUser(String username, String password) throws OurException {
        boolean success = false;
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            User user = session.get(User.class, username);

            if (user == null) {
                throw new OurException(ErrorMessages.LOGIN);
            }

            if (!user.getPassword().equals(password)) {
                throw new OurException(ErrorMessages.LOGIN);
            }

            session.delete(user);

            session.getTransaction().commit();

            success = true;
        } catch (OurException e) {
            try {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                    thread.getSession().getTransaction().rollback();
                }
            } catch (HibernateException he) {
            }
            throw e;
        } catch (Exception e) {
            try {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                    thread.getSession().getTransaction().rollback();
                }
            } catch (HibernateException he) {
            }
            throw new OurException(ErrorMessages.DELETE_USER);
        } finally {
            thread.releaseSession();
        }

        return success;
    }

    @Override
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword) throws OurException {
        boolean success = false;
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Admin admin = session.get(Admin.class, adminUsername);
            if (admin == null) {
                throw new OurException(ErrorMessages.INVALID_ADMIN_CREDENTIALS);
            }
            if (!admin.getPassword().equals(adminPassword)) {
                throw new OurException(ErrorMessages.INVALID_ADMIN_CREDENTIALS);
            }

            User userToDelete = session.get(User.class, usernameToDelete);
            if (userToDelete != null) {
                session.delete(userToDelete);
            }

            Admin adminToDelete = session.get(Admin.class, usernameToDelete);
            if (adminToDelete != null) {
                session.delete(adminToDelete);
            }

            Profile profileToDelete = session.get(Profile.class, usernameToDelete);
            if (profileToDelete != null) {
                session.delete(profileToDelete);
            }

            session.getTransaction().commit();

            success = true;
        } catch (OurException e) {
            try {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                    thread.getSession().getTransaction().rollback();
                }
            } catch (HibernateException he) {
            }
            throw e;
        } catch (Exception e) {
            try {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                    thread.getSession().getTransaction().rollback();
                }
            } catch (HibernateException he) {
            }
            throw new OurException(ErrorMessages.DELETE_USER);
        } finally {
            thread.releaseSession();
        }

        return success;
    }

    @Override
    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender) throws OurException {
        boolean success = false;
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            User user = session.get(User.class, username);
            if (user != null) {
                user.setPassword(password);
                user.setEmail(email);
                user.setName(name);
                user.setTelephone(telephone);
                user.setSurname(surname);
                user.setGender(gender);

                session.update(user);

                session.getTransaction().commit();
                success = true;
            } else {
                session.getTransaction().rollback();
                throw new OurException(ErrorMessages.UPDATE_USER);
            }
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            throw new OurException(ErrorMessages.UPDATE_USER);
        } finally {
            thread.releaseSession();
        }

        return success;
    }

    @Override
    public ArrayList<String> comboBoxInsert() throws OurException {
        ArrayList<String> listaUsuarios = new ArrayList<>();
        Session session = null;

        try {
            session = HibernateUtil.getSession();

            List<User> users = session.createCriteria(User.class).list();

            for (User u : users) {
                listaUsuarios.add(u.getUsername());
            }
        } catch (Exception e) {
            throw new OurException(ErrorMessages.GET_USERS);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return listaUsuarios;
    }

    @Override
    public Profile findProfileByUsername(String username) throws OurException {
        SessionThread thread = startSessionThread();
        Profile profile = null;

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            // Query para buscar el perfil por username
            List<Profile> profiles = session.createQuery(
                    "FROM Profile p WHERE p.username = :username", Profile.class)
                    .setParameter("username", username)
                    .list();

            if (!profiles.isEmpty()) {
                profile = profiles.get(0);
            }

            session.getTransaction().commit();

        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }

        return profile;
    }

    //[VIDEOGAMES]
    @Override
    public ArrayList<VideoGame> getAllVideoGames() throws OurException {
        Session session = null;
        ArrayList<VideoGame> gamesList = new ArrayList<>();

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            gamesList = new ArrayList<>(session.createQuery("FROM VideoGame v", VideoGame.class).list());

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

        return gamesList;
    }

    @Override
    public ArrayList<VideoGame> getGamesFromList(String username, String listName) throws OurException {
        ArrayList<VideoGame> games = new ArrayList<>();
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

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
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }

        return games;
    }

    @Override
    public boolean verifyGameInList(String username, String listName, int gameId) throws OurException {
        SessionThread thread = startSessionThread();
        boolean listed = false;

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Profile profile = session.get(Profile.class,
                    username);

            if (profile != null) {
                List<Listed> listedGames = session.createQuery(
                        "FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class
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
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
        return listed;
    }

    @Override
    public void addGameToList(String username, String listName, int gameId) throws OurException {
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);
            VideoGame game = session.get(VideoGame.class, gameId);

            if (profile != null && game != null) {
                Listed listed = new Listed(profile, game, listName);
                session.save(listed);
            }

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    @Override
    public void addGamesToList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);
            for (VideoGame game : games) {
                VideoGame gameToAdd = session.get(VideoGame.class, game.getV_id());

                if (profile != null && gameToAdd != null) {
                    Listed listed = new Listed(profile, gameToAdd, listName);
                    session.save(listed);
                }

                session.getTransaction().commit();
            }
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    @Override
    public void removeGameFromList(String username, String listName, int gameId) throws OurException {
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            session.createQuery("DELETE FROM Listed WHERE profile.username = :username AND videogame.v_id = :gameId")
                    .setParameter("username", username)
                    .setParameter("gameId", gameId)
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    @Override
    public void removeGamesFromList(String username, String listName, ArrayList<VideoGame> games) throws OurException {
        SessionThread thread = startSessionThread();

        if (!games.isEmpty()) {
            try {
                Session session = waitForSession(thread);

                if (session == null) {
                    throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
                }

                session.beginTransaction();
                for (VideoGame game : games) {
                    session.createQuery("DELETE FROM Listed WHERE profile.username = :username AND listName = :listName AND videogame.v_id = :gameId")
                            .setParameter("username", username)
                            .setParameter("listName", listName)
                            .setParameter("gameId", game.getV_id())
                            .executeUpdate();

                    session.getTransaction().commit();
                }
            } catch (OurException e) {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                    thread.getSession().getTransaction().rollback();
                }
                throw e;
            } catch (Exception e) {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                    thread.getSession().getTransaction().rollback();
                }
                throw new OurException(ErrorMessages.DATABASE);
            } finally {
                thread.releaseSession();
            }
        }
    }

    @Override
    public VideoGame findVideoGameByName(String gameName) throws OurException {
        SessionThread thread = startSessionThread();
        VideoGame videoGame = null;

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }
            session.beginTransaction();
            List<VideoGame> games = session.createQuery(
                    "FROM VideoGame v WHERE v.v_name = :gameName", VideoGame.class)
                    .setParameter("gameName", gameName)
                    .list();

            if (!games.isEmpty()) {
                videoGame = games.get(0);
            }

            session.getTransaction().commit();

        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }

        return videoGame;
    }

    //[LISTS]
    @Override
    public ArrayList<String> getUserLists(String username) throws OurException {
        ArrayList<String> lists = new ArrayList<>();
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Profile profile = session.get(Profile.class,
                    username);

            if (profile != null) {
                List<Listed> listNames = session.createQuery(
                        "FROM Listed l WHERE l.profile.username = :username GROUP BY listName", Listed.class
                )
                        .setParameter("username", username)
                        .list();

                for (Listed listed : listNames) {
                    lists.add(listed.getListName());
                }
            }

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }

        return lists;
    }

    @Override
    public void newList(Profile profile, String listName) throws OurException {
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Listed listed = new Listed(profile, session.get(VideoGame.class, 1), listName);

            session.save(listed);

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    @Override
    public void deleteList(String username, String listName) throws OurException {
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            session.createQuery("DELETE FROM Listed WHERE profile.username = :username AND listName = :listName")
                    .setParameter("username", username)
                    .setParameter("listName", listName)
                    .executeUpdate();

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
    }

    @Override
    public boolean verifyListName(String username, String listName) throws OurException {
        boolean nameExist = true;;
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);

            if (profile != null) {
                Listed list = session.createQuery(
                        "FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName", Listed.class)
                        .setParameter("username", username)
                        .setParameter("listName", listName)
                        .getSingleResult();

                if (list == null) {
                    nameExist = false;
                }

                session.getTransaction().commit();
            }

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
        return nameExist;
    }

    @Override
    public boolean renameList(String username, String listName, String listNewName) throws OurException {
        boolean listRenamed = false;

        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();

            Profile profile = session.get(Profile.class, username);
            if (profile != null) {

                int update = session.createQuery(
                        "UPDATE Listed SET listName = :listNewName WHERE username = :username AND list_name = :listName", Listed.class)
                        .setParameter("listName", listName)
                        .setParameter("listNewName", listNewName)
                        .setParameter("username", username)
                        .executeUpdate();
                if (update != 0) {
                    listRenamed = true;
                }

                session.getTransaction().commit();
            }

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            e.printStackTrace(); //test
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }

        return listRenamed;
    }

    //[REVIEWS]
    @Override
    public Review findReview(String username, int gameId) throws OurException {
        SessionThread thread = startSessionThread();
        Review review = null;
        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

            session.beginTransaction();
            review = session.createQuery("SELECT r FROM Review r JOIN r.profile p JOIN r.videogame v WHERE p.username = :username AND v.v_id = :gameId", Review.class)
                    .setParameter("username", username)
                    .setParameter("gameId", gameId)
                    .setMaxResults(1)
                    .uniqueResult();

            session.getTransaction().commit();

        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }

        return review;
    }
   
    @Override
    public ArrayList<Review> getAllReviews() throws OurException {
        ArrayList<Review> reviews = new ArrayList<>();
        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);

            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }
            session.beginTransaction();
            List<Review> reviewList = session.createQuery("FROM Review r ORDER BY r.reviewDate DESC", Review.class).list();
            reviews = new ArrayList<>(reviewList);

            session.getTransaction().commit();
        } catch (OurException e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.DATABASE);
        } finally {
            thread.releaseSession();
        }
        return reviews;
    }
    
    @Override
    public boolean saveOrUpdateReview(Review review) throws OurException {

        SessionThread thread = startSessionThread();

        try {
            Session session = waitForSession(thread);
            if (session == null) {
                throw new OurException(ErrorMessages.CONNECTION_POOL_FULL);
            }

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
                // UPDATE
                existing.setScore(review.getScore());
                existing.setDescription(review.getDescription());
                existing.setPlatform(review.getPlatform());
                existing.setReviewDate(review.getReviewDate());
            } else {
                session.persist(review);
            }

            session.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive()) {
                thread.getSession().getTransaction().rollback();
            }
            throw new OurException(ErrorMessages.SAVE_REVIEW_ERROR);
        } finally {
            thread.releaseSession();
        }
    }

    //[OTHER]
    @Override
    public void initializeDefault() throws OurException {
        Session session = null;

        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            Profile profile;
            VideoGame game;
            Listed existingList;
            Review existingReview;

            /**
             * ******************************************************USERS*******************************************************
             */
            if (session.get(User.class,
                    "jlopez") == null) {
                session.save(new User("Masculino", "AB1234567890123456789012",
                        "jlopez", "pass123", "jlopez@example.com",
                        "Juan", "987654321", "Lopez"));
            }

            if (session.get(User.class,
                    "mramirez") == null) {
                session.save(new User("Femenino", "ZX9081726354891027364512",
                        "mramirez", "pass456", "mramirez@example.com",
                        "Maria", "912345678", "Ramirez"));
            }

            if (session.get(User.class,
                    "cperez") == null) {
                session.save(new User("Masculino", "LM0011223344556677889900",
                        "cperez", "pass789", "cperez@example.com",
                        "Carlos", "934567890", "Perez"));
            }

            if (session.get(Admin.class,
                    "asanchez") == null) {
                session.save(new Admin("CTA-001", "asanchez", "qwerty",
                        "asanchez@example.com", "Ana", "900112233", "Sanchez"));
            }

            if (session.get(Admin.class,
                    "rluna") == null) {
                session.save(new Admin("CTA-002", "rluna", "zxcvbn",
                        "rluna@example.com", "Rosa", "955667788", "Luna"));
            }

            /**
             * ******************************************************GAMES*******************************************************
             */
            ArrayList<VideoGame> allGames = new ArrayList<>();
            allGames.add(new VideoGame());
            allGames.add(new VideoGame("Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
            allGames.add(new VideoGame("Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI6));
            allGames.add(new VideoGame("Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI16));
            allGames.add(new VideoGame("ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
            allGames.add(new VideoGame("Call of Duty: Black Ops II", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));

            for (VideoGame allGameGames : allGames) {
                VideoGame existing = session
                        .createQuery("FROM VideoGame v WHERE v.v_name = :name", VideoGame.class
                        )
                        .setParameter("name", allGameGames.getV_name())
                        .uniqueResult();

                if (existing == null) {
                    session.save(allGameGames);
                }
            }

            /**
             * ******************************************************LISTS*******************************************************
             */
            profile = session.get(User.class, "asanchez");
            game = session.get(VideoGame.class, 1);

            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "My Games")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 1), "My Games"));
            }

            game = session.get(VideoGame.class, 2);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "My Games")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 2), "My Games"));
            }

            game = session.get(VideoGame.class, 3);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "My Games")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 3), "My Games"));
            }

            game = session.get(VideoGame.class, 5);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "My Games")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 5), "My Games"));
            }

            game = session.get(VideoGame.class, 1);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "NINTENDO")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 1), "NINTENDO"));
            }

            game = session.get(VideoGame.class, 2);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "NINTENDO")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 2), "NINTENDO"));
            }

            game = session.get(VideoGame.class, 3);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "NINTENDO")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 3), "NINTENDO"));
            }

            game = session.get(VideoGame.class, 1);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "PLAYSTATION")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 1), "PLAYSTATION"));
            }

            game = session.get(VideoGame.class, 5);
            existingList = session.createQuery("FROM Listed l WHERE l.profile.username = :username AND l.listName = :listName AND l.videogame = :gameId", Listed.class)
                    .setParameter("username", profile)
                    .setParameter("listName", "PLAYSTATION")
                    .setParameter("gameId", game)
                    .uniqueResult();

            if (existingList == null) {
                session.save(new Listed(session.get(Admin.class, "asanchez"), session.get(VideoGame.class, 5), "PLAYSTATION"));
            }

            /**
             * ******************************************************REVIEWS*******************************************************
             */
            game = session.get(VideoGame.class, 2);
            profile = session.get(Admin.class, "asanchez");
            existingReview = session.createQuery(
                    "FROM Review r WHERE r.profile.username = :username AND r.videogame.v_id = :gameId",
                    Review.class)
                    .setParameter("username", profile.getUsername())
                    .setParameter("gameId", game.getV_id())
                    .uniqueResult();

            if (existingReview == null) {
                Review newReview = new Review(profile, game, 7, "Descripción de la review", LocalDate.now(), Platform.XBOX);
                session.save(newReview);
            }

            game = session.get(VideoGame.class, 3);
            existingReview = session.createQuery(
                    "FROM Review r WHERE r.profile.username = :username AND r.videogame.v_id = :gameId",
                    Review.class)
                    .setParameter("username", profile.getUsername())
                    .setParameter("gameId", game.getV_id())
                    .uniqueResult();

            if (existingReview == null) {
                Review newReview = new Review(profile, game, 7, "Descripción de la review", LocalDate.now(), Platform.XBOX);
                session.save(newReview);
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

    private Session waitForSession(SessionThread thread) throws InterruptedException {
        int attempts = 0;

        while (!thread.isReady() && attempts < 50) {
            Thread.sleep(10);
            attempts++;
        }

        return thread.getSession();
    }
}
