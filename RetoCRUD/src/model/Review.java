package model;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;

/**
 *
 * @author ema
 */
@Entity
@Table(name = "review")
@IdClass(ReviewId.class)
public class Review implements Serializable {
    
    @Id
    @Column(name = "r_id", nullable = false)
    private String reviewId;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Profile profile;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "v_id", referencedColumnName = "v_id")
    private VideoGame videogame;
    
    @Column(name = "score", nullable = false)
    private int score;
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "review_date")  
    private LocalDate reviewDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;
    
    public Review() {}

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

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    
    public Profile getProfile() {  
        return profile;
    }

    public void setProfile(Profile profile) {  
        this.profile = profile;
    }

    public VideoGame getVideogame() {
        return videogame;
    }

    public void setVideogame(VideoGame videogame) {
        this.videogame = videogame;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
    public String getGameName() {
        return this.videogame.getV_name();
    }
    public String getProfileUsername() {
        return this.profile.getUsername();
    }
    public String getScoreFormatted() {
        return this.score + "/10";
    }

    @Override
    public String toString() {
        return "Review{" + "reviewId=" + reviewId + ", profile=" + profile + ", videogame=" + videogame + ", score=" + score + ", description=" + description + ", reviewDate=" + reviewDate + ", platform=" + platform + '}';
    }   
}