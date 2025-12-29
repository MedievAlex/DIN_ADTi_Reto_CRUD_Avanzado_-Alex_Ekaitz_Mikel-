package model;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author ema
 */

public class ListedId implements Serializable {

    private int profile_id;
    private int videogame_id;
    private String list_name;

    public ListedId() {
    }

    public ListedId(int profile_id, int videogame_id, String list_name) {
        this.profile_id = profile_id;
        this.videogame_id = videogame_id;
        this.list_name = list_name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListedId)) return false;
        ListedId that = (ListedId) o;
        return profile_id == that.profile_id &&
               videogame_id == that.videogame_id &&
               Objects.equals(list_name, that.list_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profile_id, videogame_id, list_name);
    }
}
