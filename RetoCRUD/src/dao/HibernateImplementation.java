package dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Admin;
import model.Pegi;
import model.Platform;
import model.Profile;
import model.User;
import model.VideoGame;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import threads.SessionThread;

/**
 *
 * @author ema
 */
public class HibernateImplementation implements ClassDAO
{    
    @Override
    public Profile logIn(String username, String password)
    {
        Session session = null;

        try
        {
            session = HibernateUtil.getSession();

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("username", username));
            criteria.add(Restrictions.eq("password", password));
            User user = (User) criteria.uniqueResult();
            if (user != null) return user;

            criteria = session.createCriteria(Admin.class);
            criteria.add(Restrictions.eq("username", username));
            criteria.add(Restrictions.eq("password", password));
            Admin admin = (Admin) criteria.uniqueResult();

            return admin;
        }
        catch (Exception e)
        {
            return null;
        }
        finally
        {
            if (session != null && session.isOpen())
            {
                session.close();
            }
        }
    }


    @Override
    public boolean signUp(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname)
    {
        SessionThread thread = new SessionThread();
        thread.start();

        try
        {
            Session session = waitForSession(thread);

            session.beginTransaction();

            User user = new User(gender, cardNumber, username, password, email, name, telephone, surname);

            session.save(user);

            session.getTransaction().commit();

            return true;
        }
        catch (Exception e)
        {
            if (thread.getSession() != null && thread.getSession().getTransaction().isActive())
            {
                thread.getSession().getTransaction().rollback();
            }
            return false;
        }
        finally
        {
            thread.releaseSession();
        }
    }

    @Override
    public boolean dropOutUser(String username, String password)
    {
        boolean success = false;
        SessionThread thread = new SessionThread();
        thread.start();

        try
        {
            Session session = waitForSession(thread);
            session.beginTransaction();

            User user = session.get(User.class, username);

            if (user == null) return false;

            if (!user.getPassword().equals(password)) return false;

            session.delete(user);

            session.getTransaction().commit();

            success = true;
        }
        catch (Exception e)
        {
            try
            {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive())
                {
                    thread.getSession().getTransaction().rollback();
                }
            }
            catch (HibernateException he) {}
        } finally
        {
            thread.releaseSession();
        }

        return success;
    }


    @Override
    public boolean dropOutAdmin(String usernameToDelete, String adminUsername, String adminPassword)
    {
        boolean success = false;
        SessionThread thread = new SessionThread();
        thread.start();

        try
        {
            Session session = waitForSession(thread);
            session.beginTransaction();

            Admin admin = session.get(Admin.class, adminUsername);
            if (admin == null) return false;
            if (!admin.getPassword().equals(adminPassword)) return false;

            User userToDelete = session.get(User.class, usernameToDelete);
            if (userToDelete != null)
            {
                session.delete(userToDelete);
            }

            Admin adminToDelete = session.get(Admin.class, usernameToDelete);
            if (adminToDelete != null)
            {
                session.delete(adminToDelete);
            }

            Profile profileToDelete = session.get(Profile.class, usernameToDelete);
            if (profileToDelete != null)
            {
                session.delete(profileToDelete);
            }

            session.getTransaction().commit();

            success = true;
        }
        catch (Exception e)
        {
            try
            {
                if (thread.getSession() != null && thread.getSession().getTransaction().isActive())
                {
                    thread.getSession().getTransaction().rollback();
                }
            }
            catch (HibernateException he) {}
        }
        finally
        {
            thread.releaseSession();
        }

        return success;
    }


    @Override
    public boolean modificarUser(String password, String email, String name, String telephone, String surname, String username, String gender)
    {
        boolean success = false;
        SessionThread thread = new SessionThread();
        thread.start();

        try
        {
            Session session = waitForSession(thread);
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
            }
            else
            {
                session.getTransaction().rollback();
                success = false;
            }
        }
        catch (Exception e)
        {
            success = false;
        }
        finally
        {
            thread.releaseSession();
        }

        return success;
    }

    @Override
    public ArrayList<String> comboBoxInsert()
    {
        ArrayList<String> listaUsuarios = new ArrayList<>();
        SessionThread thread = new SessionThread();
        thread.start();

        try
        {
            Session session = waitForSession(thread);

            List<User> users = session.createCriteria(User.class).list();

            for (User u : users)
            {
                listaUsuarios.add(u.getUsername());
            }
        }
        catch (Exception e) {}
        finally
        {
            thread.releaseSession();
        }

        return listaUsuarios;
    }
    
    @Override
    public ArrayList<VideoGame> getVideoGames() {
        Session session = null;
        ArrayList<VideoGame> gamesList = new ArrayList<>();

        try
        {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            gamesList = new ArrayList<>(session.createQuery("from VideoGame", VideoGame.class).list());

            session.getTransaction().commit();
        }
        catch (Exception e)
        {
            if (session != null && session.getTransaction().isActive())
            {
                session.getTransaction().rollback();
            }
        }
        finally
        {
            if (session != null && session.isOpen())
            {
                session.close();
            }
        }

        return gamesList;
    }
    
    @Override
    public void initializeDefault()
    {
        Session session = null;

        try
        {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            if (session.get(User.class, "jlopez") == null)
            {
                session.save(new User("Masculino", "AB1234567890123456789012",
                        "jlopez", "pass123", "jlopez@example.com",
                        "Juan", "987654321", "Lopez"));
            }

            if (session.get(User.class, "mramirez") == null)
            {
                session.save(new User("Femenino", "ZX9081726354891027364512",
                        "mramirez", "pass456", "mramirez@example.com",
                        "Maria", "912345678", "Ramirez"));
            }

            if (session.get(User.class, "cperez") == null)
            {
                session.save(new User("Masculino", "LM0011223344556677889900",
                        "cperez", "pass789", "cperez@example.com",
                        "Carlos", "934567890", "Perez"));
            }

            if (session.get(Admin.class, "asanchez") == null)
            {
                session.save(new Admin("CTA-001", "asanchez", "qwerty",
                        "asanchez@example.com", "Ana", "900112233", "Sanchez"));
            }

            if (session.get(Admin.class, "rluna") == null)
            {
                session.save(new Admin("CTA-002", "rluna", "zxcvbn",
                        "rluna@example.com", "Rosa", "955667788", "Luna"));
            }

            ArrayList<VideoGame> allGames = new ArrayList<>();
            allGames.add(new VideoGame(1, "Owlboy", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
            allGames.add(new VideoGame(4, "ASTROBOT", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));
            allGames.add(new VideoGame(2, "Animal Crossing New Horizons", LocalDate.now(), Platform.NINTENDO, Pegi.PEGI3));
            allGames.add(new VideoGame(3, "Detroit: Become Human", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI18));
            allGames.add(new VideoGame(5, "Call of Duty: Black Ops II", LocalDate.now(), Platform.PLAYSTATION, Pegi.PEGI3));

            for (VideoGame game : allGames)
            {
                if (session.get(VideoGame.class, game.getV_id()) == null)
                {
                    session.save(game);
                }
            }

            session.getTransaction().commit();
        }
        catch (Exception e)
        {
            if (session != null && session.getTransaction().isActive())
            {
                session.getTransaction().rollback();
            }
        }
        finally
        {
            if (session != null && session.isOpen())
            {
                session.close();
            }
        }
    }
    
    private Session waitForSession(SessionThread thread) throws InterruptedException
    {
        int attempts = 0;

        while (!thread.isReady() && attempts < 50)
        {
            Thread.sleep(10);
            attempts++;
        }

        return thread.getSession();
    }
}
