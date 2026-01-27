package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity class representing an Administrator in the system.
 * Extends the Profile base class and adds administrator-specific attributes.
 *
 * @author ema
 */

@Entity
@Table(name = "admin_")
public class Admin extends Profile implements Serializable
{
    @Column(name = "current_account", length = 40)
    private String currentAccount;

    /**
     * Constructs a new Admin with the specified parameters.
     *
     * @param currentAccount The administrator's current account information
     * @param username The administrator's username
     * @param password The administrator's password
     * @param email The administrator's email address
     * @param name The administrator's first name
     * @param telephone The administrator's telephone number
     * @param surname The administrator's surname
     */
    public Admin(String currentAccount, String username, String password, String email, String name, String telephone, String surname)
    {
        super(username, password, email, name, telephone, surname);
        this.currentAccount = currentAccount;
    }

    /**
     * Default constructor for Admin.
     * Creates an empty Admin instance with default values.
     */
    public Admin()
    {
        super();
        this.currentAccount = "";
    }

    /**
     * Gets the administrator's current account information.
     *
     * @return The current account information as a String
     */
    public String getCurrentAccount() { return currentAccount; }
    
    /**
     * Sets the administrator's current account information.
     *
     * @param currentAccount The new current account information
     */
    public void setCurrentAccount(String currentAccount) { this.currentAccount = currentAccount; }

    /**
     * Returns a string representation of the Admin object.
     * Includes only the currentAccount field from the Admin class.
     *
     * @return A string representation of the Admin
     */
    @Override
    public String show()
    {
        return "Admin{" + "currentAccount=" + currentAccount + "}";
    }
    
    /**
     * Returns a string representation of the Admin object.
     * Calls the show() method to generate the string.
     *
     * @return A string representation of the Admin
     */
    @Override
    public String toString()
    {
        return show();
    }
}