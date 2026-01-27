package logger;

import java.io.IOException;
import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger utility class for generating and managing application logs.
 * This class follows the Singleton pattern to ensure only one logger instance
 * is created throughout the application. Logs are written to a file in the
 * "./logs" directory.
 *
 * @author ema
 */
public class GeneraLog
{
  // [ VARIABLES ]
  private static Logger logger = null;
  private static FileHandler fileHandler = null;

  // [ CONSTRUCTORES ]
  /**
   * Private constructor to prevent external instantiation.
   * Initializes the logger only if it hasn't been initialized previously.
   * Creates the logs directory if it doesn't exist, configures the file handler,
   * and sets up the logger with appropriate formatting and log level.
   */
	private GeneraLog()
	{
		// Inicializa el logger solo si no se ha inicializado previamente
		if (logger == null)
		{
			File carpeta = new File("./logs"); // Ruta de la carpeta

			if (!carpeta.exists())
			{
				carpeta.mkdir();
			}

			try
			{
				// Configura el logger
				logger = Logger.getLogger(GeneraLog.class.getName());
				// A false evita la salida en consola
				logger.setUseParentHandlers(false);
				fileHandler = new FileHandler("./logs/ProjectLogs.log", true); // Ruta de la carpeta + nombre archivo .log [true = append mode]

				SimpleFormatter formatter = new SimpleFormatter();
				fileHandler.setFormatter(formatter);
				logger.addHandler(fileHandler);
				logger.setLevel(Level.ALL); // Establece el nivel de log (puedes ajustarlo según tu necesidad)
			}
			catch (IOException e)
			{
				System.err.println("[ERROR IN LOGGER CONFIGURATION]: " + e.getMessage());
			}
		}
	}

	// [ METODOS ]
	/**
	 * Returns the singleton logger instance.
	 * If the logger hasn't been initialized yet, it creates a new instance.
	 *
	 * @return The Logger instance for the application
	 */
	public static Logger getLogger()
	{
		if (logger == null)
		{
			new GeneraLog(); // Si aún no se ha creado, inicialízalo
		}
		return logger;
	}

	/**
	 * Closes the file handler to release system resources.
	 * This method should be called when the application is shutting down
	 * to ensure all log data is properly written and resources are freed.
	 */
	public static void closeLogger()
	{
		if (fileHandler != null)
		{
			fileHandler.close();
		}
	}
}