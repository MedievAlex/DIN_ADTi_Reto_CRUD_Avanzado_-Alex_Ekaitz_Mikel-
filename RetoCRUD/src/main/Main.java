package main;

import controller.Controller;
import controller.LogInWindowController;
import dao.HibernateImplementation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dao.HibernateUtil;
import javafx.scene.image.Image;
import logger.GeneraLog;

/**
 * Main class that serves as the entry point for the JavaFX application.
 * Initializes the database, sets up the controller, and launches the login window.
 * 
 * @author ema
 */
public class Main extends Application
{
    private HibernateImplementation dao;

    /**
     * Starts the JavaFX application by loading the login window.
     * Initializes the database, sets up global exception handling,
     * creates the controller, and displays the login interface.
     *
     * @param stage the primary stage for this application
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
        GeneraLog.getLogger().severe("Error in: " + thread.getName() 
            + " - " + throwable.getClass().getName() + ": " + throwable.getMessage());
        });
        
        try
        {
            dao = new HibernateImplementation();

            dao.initializeDefault();

            Controller controller = new Controller(dao);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LogInWindow.fxml"));
            Parent root = fxmlLoader.load();

            LogInWindowController loginController = fxmlLoader.getController();
            loginController.setCont(controller);

            stage.setTitle("LOGIN");
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
            stage.show();
        }
        catch (Exception e)
        {
            GeneraLog.getLogger().severe("Error initializing the app.");
        }
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args)
    {
        launch(args);
    }
    
    /**
     * Called when the application is shutting down.
     * Performs cleanup operations including closing database connections
     * and stopping any running threads.
     */
    @Override
    public void stop()
    {
        dao.cleanupThreads();
        HibernateUtil.close();
    }
}