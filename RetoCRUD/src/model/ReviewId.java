package model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for the Review entity.
 * This class represents the composite key consisting of videogame ID and profile username.
 * It implements Serializable for JPA compatibility and overrides equals() and hashCode() methods
 * for proper comparison and hashing in collections.
 *
 * @author Mikel
 */
public class ReviewId implements Serializable{
    /**
     * The ID of the video game being reviewed.
     * Corresponds to the videogame_id field in the Review entity.
     */
    private int videogame;
    
    /**
     * The username of the profile who wrote the review.
     * Corresponds to the username field in the Review entity.
     */
    private String profile;

    /**
     * Default constructor required by JPA.
     * Creates an empty ReviewId instance.
     */
    public ReviewId() {}

    /**
     * Constructs a new ReviewId with the specified composite key values.
     *
     * @param videogame The ID of the video game
     * @param profile The username of the profile
     */
    public ReviewId(int videogame, String profile)
    {
        this.videogame = videogame;
        this.profile = profile;
    }

    /**
     * Gets the ID of the video game.
     *
     * @return The video game ID
     */
    public int getVideogame() {
        return videogame;
    }

    /**
     * Gets the username of the profile.
     *
     * @return The profile username
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the ID of the video game.
     *
     * @param videogame The new video game ID
     */
    public void setVideogame(int videogame) {
        this.videogame = videogame;
    }

    /**
     * Sets the username of the profile.
     *
     * @param profile The new profile username
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Compares this ReviewId with another object for equality.
     * Two ReviewId objects are equal if they have the same videogame ID and profile username.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ReviewId)) return false;
        ReviewId that = (ReviewId) o;
        return videogame == that.videogame &&
               profile.equals(that.profile);
    }

    /**
     * Returns a hash code value for this ReviewId object.
     * The hash code is computed based on the profile username and videogame ID.
     *
     * @return A hash code value for this object
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(profile, videogame);
    }
}