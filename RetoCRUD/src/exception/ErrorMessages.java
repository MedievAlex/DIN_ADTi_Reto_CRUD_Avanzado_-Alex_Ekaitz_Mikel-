package exception;

/**
 * Utility class containing predefined error messages for the application. This class serves as a centralized repository for all user-facing and system error messages, ensuring consistency in error reporting throughout the application.
 *
 * All messages are defined as public static final constants to facilitate easy maintenance and internationalization of error texts.
 *
 * @author ema
 */
public class ErrorMessages
{
    /**
     * Error message displayed when user registration fails. This typically occurs due to database constraints, connection issues, or system errors during the registration process.
     */
    public static final String REGISTER_USER = "User could not be registered. Please try again later.";

    /**
     * Error message displayed when retrieving the list of users fails. This typically occurs due to database connection issues, query errors, or data access problems.
     */
    public static final String GET_USERS = "Failed to retrieve users.";

    /**
     * Error message displayed when user profile update fails. This typically occurs due to database constraints, validation errors, or system errors during the update process.
     */
    public static final String UPDATE_USER = "User could not be updated.";

    /**
     * Error message displayed when user deletion fails. This typically occurs due to database constraints, foreign key violations, or system errors during the deletion process.
     */
    public static final String DELETE_USER = "User could not be deleted.";

    /**
     * Error message displayed when user authentication fails. This typically occurs due to invalid credentials, user not found, or system errors during the login process.
     */
    public static final String LOGIN = "Login failed. Please check your credentials.";

    /**
     * Error message displayed when credential verification fails. This typically occurs during registration when checking for duplicate emails or usernames, and the verification process encounters errors.
     */
    public static final String VERIFY_CREDENTIALS = "Could not verify existing credentials.";

    /**
     * Error message displayed when database connection timeout occurs. This typically occurs when the connection pool cannot provide a connection within the expected time frame due to high load or resource constraints.
     */
    public static final String TIMEOUT = "Timeout retrieving a connection.";

    /**
     * Error message displayed when database initialization fails. This typically occurs during application startup when the system cannot establish initial connection to the database or initialize the data source.
     */
    public static final String DATABASE = "Error initializing database connection.";
    
    /**
     * Error message displayed when the connection pool is full and no connections are available.
     * This typically occurs during high load periods when all database connections are in use.
     */
    public static final String CONNECTION_POOL_FULL = "No connections available. The system is busy, please try again in a few moments.";
    
    /**
     * Error message displayed when a database session times out while trying to obtain a connection.
     * This typically occurs due to network issues or database server problems.
     */
    public static final String SESSION_TIMEOUT = "Session timeout while obtaining connection.";
    
    /**
     * Error message displayed when administrator credentials are invalid.
     * This typically occurs when an admin tries to perform a privileged operation with incorrect credentials.
     */
    public static final String INVALID_ADMIN_CREDENTIALS = "Invalid administrator credentials.";
    
    /**
     * Error message displayed when review data is invalid or incomplete.
     * This typically occurs when creating or updating a review with missing or invalid fields.
     */
    public static final String INVALID_REVIEW_DATA = "Invalid review data.";
    
    /**
     * Error message displayed when a user profile cannot be found in the database.
     * This typically occurs when trying to access a profile that has been deleted or doesn't exist.
     */
    public static final String PROFILE_NOT_FOUND = "Profile not found.";
    
    /**
     * Error message displayed when a video game cannot be found in the database.
     * This typically occurs when trying to access a game that has been deleted or doesn't exist.
     */
    public static final String GAME_NOT_FOUND = "Game not found.";
    
    /**
     * Error message displayed when saving a review fails.
     * This typically occurs due to database constraints, connection issues, or validation errors.
     */
    public static final String SAVE_REVIEW_ERROR = "Error Saving the review.";
    
    /**
     * Error message displayed when a specified user cannot be found.
     * This typically occurs when trying to access, modify, or delete a user that doesn't exist.
     */
    public static final String USER_NOT_FOUND = "The specified user does not exist.";
    
    /**
     * Error message displayed when password confirmation fails.
     * This typically occurs during registration or password change when the two password fields don't match.
     */
    public static final String PASSWORDS_DO_NOT_MATCH = "The passwords do not match";
}