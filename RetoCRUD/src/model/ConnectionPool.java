package model;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * ConnectionPool class for managing database connections.
 * Uses Apache DBCP2 BasicDataSource to maintain a pool of reusable connections.
 * 
 * Author: acer
 */
public class ConnectionPool
{

    private static final BasicDataSource DATASOURCE;

    static
    {
        DATASOURCE = new BasicDataSource();
        
        ResourceBundle configFile = ResourceBundle.getBundle("config.configClass");

        DATASOURCE.setUrl(configFile.getString("Conn"));
        DATASOURCE.setUsername(configFile.getString("DBUser"));
        DATASOURCE.setPassword(configFile.getString("DBPass"));
        DATASOURCE.setDriverClassName(configFile.getString("Driver"));

        DATASOURCE.setInitialSize(5);
        DATASOURCE.setMaxTotal(10);
        DATASOURCE.setMinIdle(2);
        DATASOURCE.setMaxIdle(5);
        DATASOURCE.setMaxWaitMillis(10000); // 10 seconds
    }

    /**
     * Returns a connection from the pool.
     *
     * @return Connection object
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException
    {
        return DATASOURCE.getConnection();
    }
}
