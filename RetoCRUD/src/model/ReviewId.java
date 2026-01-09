/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Mikel
 */
public class ReviewId implements Serializable{
    private String reviewId;
    private int videogame;
    private String profile;

    public ReviewId() {}

    public ReviewId(String reviewId,int videogame, String profile)
    {
        this.reviewId = reviewId;
        this.videogame = videogame;
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ReviewId)) return false;
        ReviewId that = (ReviewId) o;
        return videogame == that.videogame &&
               profile.equals(that.profile) &&
                reviewId.equals(that.reviewId);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(reviewId,profile, videogame);
    }
}
