package model;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 * Entity class representing a review of a video game by a user.
 * This class uses a composite primary key defined by the ReviewId class.
 * Each review is uniquely identified by the combination of user and video game.
 *
 * @author ema
 */
@Entity
@Table(name = "review")
@IdClass(ReviewId.class)
public class Review implements Serializable {
    
    /**
     * Composite key part: Unique review identifier.
     * Generated as a combination of username and game ID.
     */
    @Id
    @Column(name = "r_id", nullable = false)
    private String reviewId;
    
    /**
     * Composite key part: The user who wrote the review.
     * Maps to the 'username' column in the profile table.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Profile profile;
    
    /**
     * Composite key part: The video game being reviewed.
     * Maps to the 'v_id' column in the videogame table.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "v_id", referencedColumnName = "v_id")
    private VideoGame videogame;
    
    /**
     * The numerical score given in the review.
     * Range is typically 0-10.
     */
    @Column(name = "score", nullable = false)
    private int score;
    
    /**
     * The textual description of the review.
     * Contains the user's comments about the game.
     */
    @Column(name = "description", nullable = false)
    private String description;
    
    /**
     * The date when the review was written.
     */
    @Column(name = "review_date")  
    private LocalDate reviewDate;
    
    /**
     * The platform on which the game was played for this review.
     * Stored as an enumerated string value.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;
    
    /**
     * Default constructor required by JPA.
     * Creates an empty Review instance.
     */
    public Review() {}

    /**
     * Constructs a new Review with the specified parameters.
     * The reviewId is automatically generated as "username-gameId".
     *
     * @param profile The user who wrote the review
     * @param videogame The video game being reviewed
     * @param score The numerical score (0-10)
     * @param description The textual review content
     * @param reviewDate The date of the review
     * @param platform The platform on which the game was played
     */
    public Review(Profile profile, VideoGame videogame, int score, String description, 
                  LocalDate reviewDate, Platform platform) {
        this.reviewId = profile.getUsername() + "-" + videogame.getV_id();
        this.profile = profile;
        this.videogame = videogame;
        this.score = score;
        this.description = description;
        this.reviewDate = reviewDate;
        this.platform = platform;
    }

    /**
     * Gets the unique review identifier.
     *
     * @return The review ID as a String
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Sets the unique review identifier.
     *
     * @param reviewId The new review ID
     */
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    /**
     * Gets the user who wrote the review.
     *
     * @return The Profile object of the reviewer
     */
    public Profile getProfile() {  
        return profile;
    }

    /**
     * Sets the user who wrote the review.
     *
     * @param profile The new Profile object of the reviewer
     */
    public void setProfile(Profile profile) {  
        this.profile = profile;
    }

    /**
     * Gets the video game being reviewed.
     *
     * @return The VideoGame object being reviewed
     */
    public VideoGame getVideogame() {
        return videogame;
    }

    /**
     * Sets the video game being reviewed.
     *
     * @param videogame The new VideoGame object being reviewed
     */
    public void setVideogame(VideoGame videogame) {
        this.videogame = videogame;
    }

    /**
     * Gets the numerical score of the review.
     *
     * @return The score as an integer
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the numerical score of the review.
     *
     * @param score The new score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the textual description of the review.
     *
     * @return The review description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the textual description of the review.
     *
     * @param description The new review description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the date when the review was written.
     *
     * @return The review date
     */
    public LocalDate getReviewDate() {
        return reviewDate;
    }

    /**
     * Sets the date when the review was written.
     *
     * @param reviewDate The new review date
     */
    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    /**
     * Gets the platform on which the game was played.
     *
     * @return The platform as a Platform enum
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * Sets the platform on which the game was played.
     *
     * @param platform The new platform
     */
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
    
    /**
     * Gets the name of the game being reviewed.
     * Convenience method for accessing the game name directly.
     *
     * @return The name of the video game
     */
    public String getGameName() {
        return this.videogame.getV_name();
    }
    
    /**
     * Gets the username of the reviewer.
     * Convenience method for accessing the reviewer's username directly.
     *
     * @return The username of the reviewer
     */
    public String getProfileUsername() {
        return this.profile.getUsername();
    }
    
    /**
     * Gets the score formatted as "X/10".
     * Convenience method for displaying the score in a user-friendly format.
     *
     * @return The formatted score string
     */
    public String getScoreFormatted() {
        return this.score + "/10";
    }

    /**
     * Returns a string representation of the Review object.
     * Includes all fields of the review.
     *
     * @return A string representation of the Review
     */
    @Override
    public String toString() {
        return "Review{" + "reviewId=" + reviewId + ", profile=" + profile + ", videogame=" + videogame + ", score=" + score + ", description=" + description + ", reviewDate=" + reviewDate + ", platform=" + platform + '}';
    }   
}