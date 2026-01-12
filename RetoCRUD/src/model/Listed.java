package model;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "listed")
@IdClass(ListedId.class)
public class Listed implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "username")
    private Profile profile;

    @Id
    @ManyToOne
    @JoinColumn(name = "videogame_id")
    private VideoGame videogame;

    @Id
    @Column(name = "list_name")
    private String listName;

    public Listed() {
    }

    public Listed(Profile profile, VideoGame videogame, String listName) {
        this.profile = profile;
        this.videogame = videogame;
        this.listName = listName;
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

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }
}
