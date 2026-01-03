package dao;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil
{
    private static SessionFactory sessionFactory;
    private HibernateUtil() {}
    
    private static SessionFactory buildSessionFactory()
    {
        if (sessionFactory == null)
        {
            try
            {
                ResourceBundle configFile = ResourceBundle.getBundle("config.configClass");
                String db = configFile.getString("DB");
                String host = configFile.getString("HOST");
                String port = configFile.getString("PORT");
                String user = configFile.getString("DBUSER");
                String pass = configFile.getString("DBPASS");
                String driver = configFile.getString("DRIVER");
                String maxConnection = configFile.getString("MAXCONNECTIONS");
                
                try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":" + port + "/?serverTimezone=Europe/Madrid&useSSL=false&allowPublicKeyRetrieval=true",
                        user, pass);
                     java.sql.Statement stmt = conn.createStatement())
                {
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + db + " CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
                }
                catch (SQLException ex)
                {
                    Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?serverTimezone=Europe/Madrid&useSSL=false&allowPublicKeyRetrieval=true";
                
                Properties props = new Properties();
                
                props.put("hibernate.connection.driver_class", driver);
                props.put("hibernate.connection.url", url);
                props.put("hibernate.connection.username", user);
                props.put("hibernate.connection.password", pass);
                props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
                props.put("hibernate.show_sql", "true");
                props.put("hibernate.hbm2ddl.auto", "update");
                
                props.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
                
                props.put("hibernate.hikari.maximumPoolSize", maxConnection);
                props.put("hibernate.hikari.minimumIdle", "1");
                
                props.put("hibernate.hikari.connectionTimeout", "1000");
                props.put("hibernate.hikari.idleTimeout", "600000");
                props.put("hibernate.hikari.maxLifetime", "1800000");
                
                props.put("hibernate.hikari.leakDetectionThreshold", "60000");
                
                Configuration configuration = new Configuration();
                configuration.setProperties(props);
                
                configuration.addAnnotatedClass(model.Profile.class);
                configuration.addAnnotatedClass(model.User.class);
                configuration.addAnnotatedClass(model.Admin.class);
                configuration.addAnnotatedClass(model.VideoGame.class);
                configuration.addAnnotatedClass(model.Listed.class);
                
                sessionFactory = configuration.buildSessionFactory();
            }
            catch (HibernateException e)
            {
                throw new ExceptionInInitializerError("Error creating SessionFactory: " + e.getMessage());
            }
        }
        return sessionFactory;
    }
    
    public static Session getSession()
    {
        return buildSessionFactory().openSession();
    }
    
    public static void close()
    {
        if (sessionFactory != null && !sessionFactory.isClosed())
        {
            sessionFactory.close();
        }
    }
}