package model;

import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import org.hibernate.annotations.Check;

/**
 * Abstract base class representing a user profile in the system.
 * This class uses JOINED inheritance strategy to map subclasses to different tables.
 * It includes basic user information and manages lists of video games.
 *
 * @author ema
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "profile_")
@Check(constraints = "TELEPHONE REGEXP '^[0-9]{9}$'")
public abstract class Profile implements Serializable {

    /**
     * Unique username for the profile.
     * Serves as the primary key in the database.
     */
    @Id
    @Column(name = "username", length = 40)
    private String username;

    /**
     * User's password for authentication.
     */
    @Column(name = "password_", length = 40)
    private String password;

    /**
     * User's email address.
     * Must be unique across all profiles.
     */
    @Column(name = "email", unique = true, length = 40)
    private String email;

    /**
     * User's first name.
     */
    @Column(name = "name_", length = 40)
    private String name;

    /**
     * User's telephone number.
     * Must match the regex pattern '^[0-9]{9}$' (exactly 9 digits).
     */
    @Column(name = "telephone", length = 9)
    private String telephone;

    /**
     * User's surname.
     */
    @Column(name = "surname", length = 40)
    private String surname;

    /**
     * Set of listed games associated with this profile.
     * Uses eager fetching to load the collection with the profile.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Listed> listedGames = new HashSet<>();

    /**
     * Default constructor required by JPA.
     * Creates an empty Profile instance.
     */
    public Profile() {
    }

    /**
     * Constructs a new Profile with the specified parameters.
     *
     * @param username The username for the profile
     * @param password The password for authentication
     * @param email The email address
     * @param name The first name
     * @param telephone The telephone number (must be 9 digits)
     * @param surname The surname
     */
    public Profile(String username, String password, String email, String name, String telephone, String surname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.telephone = telephone;
        this.surname = surname;
    }

    /**
     * Returns the profile instance itself.
     *
     * @return This Profile object
     */
    public Profile getProfile() {
        return this;
    }

    /**
     * Gets the username of the profile.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the profile.
     *
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the profile.
     *
     * @return The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the profile.
     *
     * @param password The new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the email address of the profile.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the profile.
     *
     * @param email The new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the first name of the profile.
     *
     * @return The first name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the first name of the profile.
     *
     * @param name The new first name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the telephone number of the profile.
     *
     * @return The telephone number
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the telephone number of the profile.
     *
     * @param telephone The new telephone number
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Gets the surname of the profile.
     *
     * @return The surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the surname of the profile.
     *
     * @param surname The new surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the set of listed games associated with this profile.
     *
     * @return Set of Listed objects
     */
    public Set<Listed> getListedGames() {
        return listedGames;
    }

    /**
     * Sets the set of listed games associated with this profile.
     *
     * @param listedGames The new set of Listed objects
     */
    public void setListedGames(Set<Listed> listedGames) {
        this.listedGames = listedGames;
    }

    /**
     * Renames a list associated with this profile.
     *
     * @param oldName The current name of the list
     * @param newName The new name for the list
     * @return true if the rename operation was successful, false otherwise
     */
    public boolean renameList(String oldName, String newName) {
        HashMap<String, ArrayList<VideoGame>> memLists = null;
        if (!memLists.containsKey(oldName) || memLists.containsKey(newName)) {
            return false;
        }

        ArrayList<VideoGame> games = memLists.remove(oldName);
        memLists.put(newName, games);

        for (Listed listed : listedGames) {
            if (listed.getListName().equals(oldName)) {
                listed.setListName(newName);
            }
        }

        return true;
    }

    /**
     * Adds a game to a specific list for this profile.
     *
     * @param listName The name of the list to add the game to
     * @param game The VideoGame to add
     * @return true if the game was added successfully, false if it already exists in the list
     */
    public boolean addGame(String listName, VideoGame game) {
        for (Listed l : listedGames) {
            if (l.getListName().equals(listName) && l.getVideogame().getV_id() == game.getV_id()) {
                return false;
            }
        }
        listedGames.add(new Listed(this, game, listName));

        return true;
    }

    /**
     * Removes a game from a specific list for this profile.
     *
     * @param listName The name of the list to remove the game from
     * @param game The VideoGame to remove
     * @return true if the game was removed successfully, false otherwise
     */
    public boolean removeGame(String listName, VideoGame game) {
        boolean removed = listedGames.removeIf(l -> l.getListName().equals(listName) && l.getVideogame().getV_id() == game.getV_id());

        return removed;
    }

    /**
     * Returns a string representation of the Profile object.
     * Includes username, email, name, telephone, and surname fields.
     *
     * @return A string representation of the Profile
     */
    @Override
    public String toString() {
        return "Profile{"
                + "username=" + username
                + ", email=" + email
                + ", name=" + name
                + ", telephone=" + telephone
                + ", surname=" + surname
                + "}";
    }

    /**
     * Abstract method to be implemented by subclasses.
     * Provides a subclass-specific string representation.
     *
     * @return A string representation specific to the subclass
     */
    public abstract String show();
}