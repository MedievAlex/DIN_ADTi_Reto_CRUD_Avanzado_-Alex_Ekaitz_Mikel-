package model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author ema
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