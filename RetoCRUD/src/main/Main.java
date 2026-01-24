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
import logger.GeneraLog;

public class Main extends Application
{
    private HibernateImplementation dao;

    /**
     * Starts the JavaFX application by loading the login window.
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
    
    @Override
    public void stop()
    {
        dao.cleanupThreads();
        HibernateUtil.close();
    }
}
