package threads;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import dao.HibernateUtil;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread class for managing Hibernate database sessions with delayed commit functionality.
 * This thread maintains an open Hibernate session and provides controlled commit operations
 * with a configurable delay. Useful for batch operations or scenarios where transactions
 * need to be held open for a specific period.
 *
 * @author ema
 */
public class SessionThread extends Thread
{
    /**
     * Flag indicating whether the thread should end its execution.
     */
    private boolean end = false;
    
    /**
     * Flag indicating whether the Hibernate session is ready for use.
     */
    private boolean ready = false;
    
    /**
     * The Hibernate Session managed by this thread.
     */
    private Session session;
    
    /**
     * Delay in seconds before committing the transaction when releaseSession() is called.
     */
    private int delay;

    /**
     * Constructs a new SessionThread.
     * Reads the delay configuration from the config file, defaulting to 30 seconds
     * if the configuration cannot be read.
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
            this.delay = 30;
        }
    }

    /**
     * Gets the Hibernate Session managed by this thread.
     * Should only be called after isReady() returns true.
     *
     * @return The Hibernate Session object
     */
    public Session getSession()
    {
        return session;
    }

    /**
     * Checks if the thread has successfully initialized the Hibernate session.
     *
     * @return true if the session is ready for use, false otherwise
     */
    public boolean isReady()
    {
        return ready;
    }

    /**
     * Signals the thread to end execution and commit any pending transaction.
     * This method sets the end flag and interrupts the thread's sleep.
     * The transaction will be committed after the configured delay.
     */
    public void releaseSession()
    {
        this.end = true;
        this.interrupt();
    }

    /**
     * Main execution method of the thread.
     * Opens a Hibernate session, waits for the release signal,
     * then commits any transaction with the configured delay.
     * Ensures proper cleanup of session resources in the finally block.
     */
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
                session.beginTransaction();

                try {
                    Thread.sleep(delay * 1000L);
                } catch (InterruptedException e) {
                    return;
                }

                session.getTransaction().commit();
            }
            catch (Exception e)
            {
                try
                {
                    if (session.getTransaction() != null && session.getTransaction().isActive())
                    {
                        session.getTransaction().rollback();
                    }
                }
                catch (Exception ex) {}
            }
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