package model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity class representing the relationship between a Profile, a VideoGame, and a list.
 * This class implements a many-to-many relationship with an additional attribute (listName).
 * It uses a composite primary key defined by the ListedId class.
 *
 * @author ema
 */
@Entity
@Table(name = "listed")
@IdClass(ListedId.class)
public class Listed implements Serializable {

    /**
     * Composite key part: The profile associated with this listing.
     * Maps to the 'username' column in the database.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "username")
    private Profile profile;

    /**
     * Composite key part: The video game associated with this listing.
     * Maps to the 'videogame_id' column in the database.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "videogame_id")
    private VideoGame videogame;

    /**
     * Composite key part: The name of the list where the video game is added.
     * Maps to the 'list_name' column in the database.
     */
    @Id
    @Column(name = "list_name")
    private String listName;

    /**
     * Default constructor required by JPA.
     * Creates an empty Listed instance.
     */
    public Listed() {
    }

    /**
     * Constructs a new Listed with the specified parameters.
     *
     * @param profile The profile associated with this listing
     * @param videogame The video game associated with this listing
     * @param listName The name of the list where the video game is added
     */
    public Listed(Profile profile, VideoGame videogame, String listName) {
        this.profile = profile;
        this.videogame = videogame;
        this.listName = listName;
    }

    /**
     * Gets the profile associated with this listing.
     *
     * @return The Profile object
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Sets the profile associated with this listing.
     *
     * @param profile The new Profile object
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * Gets the video game associated with this listing.
     *
     * @return The VideoGame object
     */
    public VideoGame getVideogame() {
        return videogame;
    }

    /**
     * Sets the video game associated with this listing.
     *
     * @param videogame The new VideoGame object
     */
    public void setVideogame(VideoGame videogame) {
        this.videogame = videogame;
    }

    /**
     * Gets the name of the list where the video game is added.
     *
     * @return The list name as a String
     */
    public String getListName() {
        return listName;
    }

    /**
     * Sets the name of the list where the video game is added.
     *
     * @param listName The new list name
     */
    public void setListName(String listName) {
        this.listName = listName;
    }
}