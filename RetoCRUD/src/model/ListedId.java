package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for the Listed entity.
 * This class represents the composite key consisting of videogame ID, profile username, and list name.
 * It implements Serializable for JPA compatibility and overrides equals() and hashCode() methods
 * for proper comparison and hashing in collections.
 *
 * @author ema
 */
public class ListedId implements Serializable
{
    /**
     * The ID of the video game in the listing.
     * Corresponds to the videogame_id field in the Listed entity.
     */
    private int videogame;
    
    /**
     * The username of the profile in the listing.
     * Corresponds to the username field in the Listed entity.
     */
    private String profile;
    
    /**
     * The name of the list in the listing.
     * Corresponds to the list_name field in the Listed entity.
     */
    private String listName;

    /**
     * Default constructor required by JPA.
     * Creates an empty ListedId instance.
     */
    public ListedId() {}

    /**
     * Constructs a new ListedId with the specified composite key values.
     *
     * @param videogame The ID of the video game
     * @param profile The username of the profile
     * @param listName The name of the list
     */
    public ListedId(int videogame, String profile, String listName)
    {
        this.videogame = videogame;
        this.profile = profile;
        this.listName = listName;
    }

    /**
     * Compares this ListedId with another object for equality.
     * Two ListedId objects are equal if they have the same videogame ID,
     * profile username, and list name.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ListedId)) return false;
        ListedId that = (ListedId) o;
        return videogame == that.videogame &&
               profile.equals(that.profile) &&
               listName.equals(that.listName);
    }

    /**
     * Returns a hash code value for this ListedId object.
     * The hash code is computed based on the profile username, videogame ID, and list name.
     *
     * @return A hash code value for this object
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(profile, videogame, listName);
    }
}