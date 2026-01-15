package model;

import java.io.Serializable;
import java.util.Objects;

public class ListedId implements Serializable
{
    private int videogame;
    private String profile;
    private String listName;

    public ListedId() {}

    public ListedId(int videogame, String profile, String listName)
    {
        this.videogame = videogame;
        this.profile = profile;
        this.listName = listName;
    }

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

    @Override
    public int hashCode()
    {
        return Objects.hash(profile, videogame, listName);
    }
}
