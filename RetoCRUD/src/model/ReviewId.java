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
    private int videogame;
    private String profile;

    public ReviewId() {}

    public ReviewId(int videogame, String profile)
    {
        this.videogame = videogame;
        this.profile = profile;
    }


    public int getVideogame() {
        return videogame;
    }

    public String getProfile() {
        return profile;
    }

    public void setVideogame(int videogame) {
        this.videogame = videogame;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ReviewId)) return false;
        ReviewId that = (ReviewId) o;
        return videogame == that.videogame &&
               profile.equals(that.profile);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(profile, videogame);
    }
}
