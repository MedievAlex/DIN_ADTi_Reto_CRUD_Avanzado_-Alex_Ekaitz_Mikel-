package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "listed")
@IdClass(ListedId.class)

public class Listed {
    @Id
    private int profile_id;

    @Id
    private int videogame_id;

    @Id
    private String list_name;

    public Listed() {}

    public Listed(int profile_id, int videogame_id, String list_name) {
        this.profile_id = profile_id;
        this.videogame_id = videogame_id;
        this.list_name = list_name;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public int getVideogame_id() {
        return videogame_id;
    }

    public String getList_name() {
        return list_name;
    }
}
