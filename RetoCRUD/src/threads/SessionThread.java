package threads;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import dao.HibernateUtil;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionThread extends Thread
{
    private boolean end = false;
    private boolean ready = false;
    private Session session;
    private int delay;

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

    public Session getSession()
    {
        return session;
    }

    public boolean isReady()
    {
        return ready;
    }

    public void releaseSession()
    {
        this.end = true;
        this.interrupt();
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
                session.beginTransaction();
                session.doWork(connection -> {
                    try
                    {
                        Thread.sleep(delay * 1000L);
                    }
                    catch (InterruptedException e) {}
                });
                session.getTransaction().commit();
            }
            catch (Exception e) {}
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