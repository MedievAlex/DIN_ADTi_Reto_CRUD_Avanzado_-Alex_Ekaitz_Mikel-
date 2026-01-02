package threads;

import org.hibernate.Session;
import org.hibernate.HibernateException;
import dao.HibernateUtil;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread class to obtain a Hibernate Session asynchronously.
 * Uses delay from config/configClass.properties to retain session
 * before closing. Useful for simulating concurrency in a GUI app.
 * 
 * This optimized version simplifies thread interruption handling
 * and avoids unnecessary re-interruption calls.
 */
public class SessionThread extends Thread
{
    private boolean end = false;       // Flag to stop the thread
    private boolean ready = false;     // Flag to indicate session is ready
    private Session session;           // Hibernate session
    private int delay;                 // Retention delay in seconds

    /**
     * Loads the retention delay from config/configClass.properties.
     * Defaults to 30 seconds if the property is missing or invalid.
     */
    public SessionThread()
    {
        try
        {
            ResourceBundle configFile = ResourceBundle.getBundle("config.configClass");
            this.delay = Integer.parseInt(configFile.getString("DELAY"));
        }
        catch (Exception e)
        {
            this.delay = 30; // default
        }
    }

    /**
     * Returns the Hibernate session obtained by the thread.
     * @return Session
     */
    public Session getSession()
    {
        return session;
    }

    /**
     * Returns true if the session is ready.
     * @return boolean
     */
    public boolean isReady()
    {
        return ready;
    }

    /**
     * Signals the thread to release the session and stop running.
     */
    public void releaseSession()
    {
        this.end = true;
        this.interrupt(); // wakes up the thread if sleeping
    }

    @Override
    public void run()
    {
        try
        {
            try
            {
                session = HibernateUtil.getSession();
            }
            catch (HibernateException ex)
            {
                Logger.getLogger(SessionThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            ready = true;

            while (!end)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }

            try
            {
                Thread.sleep(delay * 1000L);
            }
            catch (InterruptedException e) {}
        }
        finally
        {
            if (session != null && session.isOpen())
            {
                try
                {
                    session.close();
                }
                catch (HibernateException ex)
                {
                    Logger.getLogger(SessionThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
