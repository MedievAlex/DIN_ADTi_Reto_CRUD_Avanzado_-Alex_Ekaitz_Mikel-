package model;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Check;

/**
 * Entity class representing a regular User in the system.
 * Extends the Profile base class and adds user-specific attributes
 * such as gender and card number with validation constraints.
 *
 * @author ema
 */

@Entity
@Table(name = "user_")
@Check(constraints = "CARD_NUMBER REGEXP '^[A-Z]{2}[0-9]{22}$'")
public class User extends Profile implements Serializable
{
    /**
     * The gender of the user.
     * Typically values like "Male", "Female", "Other", etc.
     */
    @Column(name = "gender", length = 40)
    private String gender;
    
    /**
     * The card number of the user.
     * Must match the regex pattern '^[A-Z]{2}[0-9]{22}$'
     * (2 uppercase letters followed by 22 digits).
     */
    @Column(name = "card_number", length = 24)
    private String cardNumber;

    /**
     * Constructs a new User with the specified parameters.
     *
     * @param gender The user's gender
     * @param cardNumber The user's card number (must match pattern: 2 uppercase letters + 22 digits)
     * @param username The user's username
     * @param password The user's password
     * @param email The user's email address
     * @param name The user's first name
     * @param telephone The user's telephone number (must be 9 digits)
     * @param surname The user's surname
     */
    public User(String gender, String cardNumber, String username, String password, String email, String name, String telephone, String surname)
    {
        super(username, password, email, name, telephone, surname);
        this.gender = gender;
        this.cardNumber = cardNumber;
    }

    /**
     * Default constructor for User.
     * Creates an empty User instance with default values.
     */
    public User()
    {
        super();
        this.gender = "";
        this.cardNumber = "";
    }

    /**
     * Gets the user's gender.
     *
     * @return The gender as a String
     */
    public String getGender() { return gender; }
    
    /**
     * Sets the user's gender.
     *
     * @param gender The new gender
     */
    public void setGender(String gender) { this.gender = gender; }

    /**
     * Gets the user's card number.
     *
     * @return The card number as a String
     */
    public String getCardNumber() { return cardNumber; }
    
    /**
     * Sets the user's card number.
     * Must match the pattern: 2 uppercase letters followed by 22 digits.
     *
     * @param cardNumber The new card number
     */
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    /**
     * Returns a string representation of the User object.
     * Includes only the gender and cardNumber fields from the User class.
     *
     * @return A string representation of the User
     */
    @Override
    public String show()
    {
        return "User{" + "gender=" + gender + ", cardNumber=" + cardNumber + "}";
    }

    /**
     * Returns a string representation of the User object.
     * Calls the show() method to generate the string.
     *
     * @return A string representation of the User
     */
    @Override
    public String toString()
    {
        return show();
    }
}